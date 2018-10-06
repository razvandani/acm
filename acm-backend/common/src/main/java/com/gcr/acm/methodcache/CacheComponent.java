package com.gcr.acm.methodcache;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;;
import com.gcr.acm.common.utils.JsonUtils;
import com.gcr.acm.iam.user.UserIdentity;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import redis.clients.jedis.*;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

/**
 * Cache component.
 *
 * @author Razvan Dani
 */
@Component
@PropertySource("classpath:common-${spring.profiles.active}.properties")
public class CacheComponent {
    @Value("${cache.enabled}")
    private Boolean cacheEnabled;

    @Value("${rediscluster.minConnections}")
    private Integer redisClusterMinConnections;

    @Value("${rediscluster.maxConnections}")
    private Integer redisClusterMaxConnections;

    @Value("${rediscluster.endpointsList}")
    public void setRedisClusterEndpointsList(String redisClusterEndpointsList) {
        initJedisCluster(redisClusterEndpointsList);
    }

    private ConcurrentHashMap<String, ResultObjectContainer> localCache = new ConcurrentHashMap<>();
    private DelayQueue<LocalCacheDelayed> localCacheExpirationQueue = new DelayQueue<>();

    private void initJedisCluster(String redisClusterEndpointsList) {
        Set<HostAndPort> connectionPoints = new HashSet<>();

        if (!redisClusterEndpointsList.equals("")) {
            String[] endpointArray = redisClusterEndpointsList.split(",");

            for (String endpoint : endpointArray) {
                String[] hostAndPortArray = endpoint.split(":");
                String host = hostAndPortArray[0];
                Integer port = new Integer(hostAndPortArray[1]);
                connectionPoints.add(new HostAndPort(host, port));
            }

            GenericObjectPoolConfig genericObjectPoolConfig = initRedisPoolConfiguration();

            jedisCluster = new JedisCluster(connectionPoints, genericObjectPoolConfig);
        }
    }

    private GenericObjectPoolConfig initRedisPoolConfiguration() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();

        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setBlockWhenExhausted(false);
        config.setMinIdle(redisClusterMinConnections);
        config.setMaxTotal(redisClusterMaxConnections);
        config.setTestWhileIdle(false);
        config.setSoftMinEvictableIdleTimeMillis(30000L);
        config.setNumTestsPerEvictionRun(5);
        config.setTimeBetweenEvictionRunsMillis(50000L);
        config.setJmxEnabled(false);

        return config;
    }

    private JedisCluster jedisCluster;

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheComponent.class);

    // Map that caches Method objects by Class and Method names (key is a string of format Class.Method)
    private ConcurrentHashMap<String, Method> getterMethodByClassAndMethodNameMap = new ConcurrentHashMap<>();

    public void addMethodCallToCache(ProceedingJoinPoint joinPoint, Object returnedObject) throws JsonProcessingException, UnsupportedEncodingException {
        try {
            // add or update actual cache
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            MethodCache methodCacheAnnotation = methodSignature.getMethod().getAnnotation(MethodCache.class);
            String cacheKey = getCacheKey(joinPoint);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String resultObjectAsString = null;

            if (returnedObject != null) {
                resultObjectAsString = objectMapper.writeValueAsString(returnedObject);
            }

            ResultObjectContainer resultObjectContainer = getResultObjectContainer(returnedObject, resultObjectAsString);
            addToLocalCache(cacheKey, resultObjectContainer, methodCacheAnnotation.localMemoryExpirationSeconds());

            if (cacheEnabled) {
                String cacheValue = objectMapper.writeValueAsString(resultObjectContainer);
                resultObjectContainer.setResultObject(returnedObject);

                jedisCluster.set(cacheKey, cacheValue);

                if (methodCacheAnnotation.expirationSeconds() > 0) {
                    jedisCluster.expire(cacheKey, methodCacheAnnotation.expirationSeconds());
                }

                // add or update eviction cache
                for (String evictionKeyword : methodCacheAnnotation.resultObjectCacheKeywords()) {
                    List<String> keywordValueList = getKeywordValueList(evictionKeyword, returnedObject);

                    for (String keywordValue : keywordValueList) {
                        String evictionCacheKey = getEvictionCacheKey(joinPoint, evictionKeyword, keywordValue);
                        jedisCluster.lpush(evictionCacheKey, cacheKey);
                    }
                }
            } else {
                resultObjectContainer.setResultObject(returnedObject);
            }
        } catch (Throwable t) {
            LOGGER.error("Exception in addMethodCallToCache", t);
        }
    }

    private void addToLocalCache(String cacheKey, ResultObjectContainer resultObjectContainer, Integer expirationSeconds) {
        if (expirationSeconds > 0) {
            localCacheExpirationQueue.add(new LocalCacheDelayed(cacheKey, expirationSeconds * 1000));
            localCache.put(cacheKey, resultObjectContainer);
        }
    }

    private String getEvictionCacheKey(ProceedingJoinPoint joinPoint, String evictionKeyword, String keywordValue) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();

        return getEvictionCacheKey(className, methodName, evictionKeyword, keywordValue);
    }

    private String getEvictionCacheKey(String className, String methodName, String evictionKeyword, String keywordValue) {
        return "evictionkey_" + className + "_" + methodName + "_" + evictionKeyword + "_" + keywordValue;
    }

    private ResultObjectContainer getResultObjectContainer(Object returnedObject, String resultObjectAsString) {
        ResultObjectContainer resultObjectContainer = new ResultObjectContainer();
        resultObjectContainer.setResultObjectAsString(resultObjectAsString);

        if (returnedObject != null) {
            if (returnedObject instanceof Collection) {
                resultObjectContainer.setIsCollection(true);
                resultObjectContainer.setCollectionClassName(returnedObject.getClass().getName());

                if (((Collection) returnedObject).iterator().hasNext()) {
                    Object firstObject = ((Collection) returnedObject).iterator().next();
                    resultObjectContainer.setResultObjectClassName(firstObject.getClass().getName());
                }
            } else {
                resultObjectContainer.setResultObjectClassName(returnedObject.getClass().getName());
            }
        }

        return resultObjectContainer;
    }

    private String getCacheKey(ProceedingJoinPoint joinPoint) throws JsonProcessingException, UnsupportedEncodingException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        MethodCache methodCacheAnnotation = methodSignature.getMethod().getAnnotation(MethodCache.class);
        List<ParameterInfo> parameterInfoList = getMethodParameterInfoList(joinPoint);

        String parametersString = JsonUtils.getObjectMapper().writeValueAsString(parameterInfoList);
        parametersString = Base64Utils.encodeToString(parametersString.getBytes("UTF-8"));
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();

        return className + "_" + methodName + "_" + parametersString +
                ((methodCacheAnnotation.cacheDependsOnLoginUser()) ? "_userName=" + UserIdentity.getLoginUserName() : "" +
                "_" + UserIdentity.getLanguageCode());
    }

    public ResultObjectContainer getResultFromCache(ProceedingJoinPoint joinPoint) throws IOException, ClassNotFoundException {
        evictExpiredLocalCache();
//        System.out.println("methodSignature.getMethod().getName() = " + ((MethodSignature) joinPoint.getSignature()).getMethod().getName());

        ResultObjectContainer resultObjectContainer = null;

        try {
            String cacheKey = getCacheKey(joinPoint);
            resultObjectContainer = localCache.get(cacheKey);

            if (resultObjectContainer == null && cacheEnabled) {
                String cacheValue = jedisCluster.get(cacheKey);

                if (cacheValue != null) {
                    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
                    MethodCache methodCacheAnnotation = methodSignature.getMethod().getAnnotation(MethodCache.class);
                    Object resultObject = null;
                    resultObjectContainer = JsonUtils.getObjectMapper().readValue(cacheValue, ResultObjectContainer.class);

                    if (resultObjectContainer.getIsCollection()) {
                        if (resultObjectContainer.getResultObjectClassName() != null) {
                            resultObject = JsonUtils.getObjectMapper().readValue(resultObjectContainer.getResultObjectAsString(),
                                    JsonUtils.getCollectionType((Class<List>) Class.forName(resultObjectContainer.getCollectionClassName()),
                                            Class.forName(resultObjectContainer.getResultObjectClassName())));
                        } else {
                            resultObject = JsonUtils.getObjectMapper().readValue(resultObjectContainer.getResultObjectAsString(),
                                    JsonUtils.getCollectionType((Class<List>) Class.forName(resultObjectContainer.getCollectionClassName()),
                                            Object.class));
                        }
                    } else {
                        if (resultObjectContainer.getResultObjectClassName() != null) {
                            resultObject = JsonUtils.getObjectMapper().readValue(resultObjectContainer.getResultObjectAsString(),
                                    Class.forName(resultObjectContainer.getResultObjectClassName()));
                        }
                    }

                    resultObjectContainer.setResultObject(resultObject);
                    addToLocalCache(cacheKey, resultObjectContainer, methodCacheAnnotation.localMemoryExpirationSeconds());
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Exception in getResultFromCache", t);
        }

        return resultObjectContainer;
    }

    private Long lastExpiredLocalCacheEvictTimestamp = null;

    private void evictExpiredLocalCache() {
        if (lastExpiredLocalCacheEvictTimestamp == null || System.currentTimeMillis() > lastExpiredLocalCacheEvictTimestamp + 1000) {
            lastExpiredLocalCacheEvictTimestamp = System.currentTimeMillis();
            List<LocalCacheDelayed> localCacheDelayedList = new ArrayList<>();
            localCacheExpirationQueue.drainTo(localCacheDelayedList);

            for (LocalCacheDelayed localCacheDelayed : localCacheDelayedList) {
                localCache.remove(localCacheDelayed.getCacheKey());
            }
        }
    }

    private List<String> getKeywordValueList(String keywordName, Object resultObject) {
        List<String> keywordValueList = new ArrayList<>();

        if (resultObject != null) {
            int dotIndex = keywordName.indexOf('.');
            String parentAttributeName = (dotIndex > -1) ? keywordName.substring(0, dotIndex) : keywordName;
            String childAttributeNames = (dotIndex > -1) ? keywordName.substring(dotIndex + 1) : null;
            Set<Object> attributeValues = getAttributeValues(parentAttributeName, resultObject);

            if (attributeValues != null) {
                for (Object attributeValue : attributeValues) {
                    if (childAttributeNames == null) {
                        keywordValueList.add(attributeValue.toString());
                    } else {
                        keywordValueList = getKeywordValueList(childAttributeNames, attributeValue);
                    }
                }
            }
        }

        return keywordValueList;
    }

    private Set<Object> getAttributeValues(String attributeName, Object object) {
        Set<Object> attributeValueSet = null;
        try {
            if (object != null) {
                attributeValueSet = new HashSet<>();

                if (object instanceof Collection) {
                    Collection resultCollection = (Collection) object;

                    for (Object collectionElement : resultCollection) {
                        if (collectionElement != null) {
                            attributeValueSet.addAll(getAttributeValues(attributeName, collectionElement));
                        }
                    }
                } else {
                    String methodName = "get" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
                    String mapKey = object.getClass().getName() + "." + methodName;
                    Method getterMethod = getterMethodByClassAndMethodNameMap.get(mapKey);

                    if (getterMethod == null) {
                        getterMethod = object.getClass().getMethod(methodName);
                        getterMethodByClassAndMethodNameMap.put(mapKey, getterMethod);
                    }

                    Object keywordValueObject = getterMethod.invoke(object);

                    if (keywordValueObject != null) {
                        if (keywordValueObject instanceof Collection) {
                            attributeValueSet = new HashSet<>();

                            attributeValueSet.addAll((Collection) keywordValueObject);
                        } else {
                            attributeValueSet = new HashSet<>();
                            attributeValueSet.add(keywordValueObject);
                        }
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception in getAttributeValues", e);
        }

        return attributeValueSet;
    }

    private List<ParameterInfo> getMethodParameterInfoList(ProceedingJoinPoint joinPoint) {
        List<ParameterInfo> parameterInfoList = new ArrayList<>();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        for (int i = 0; i < methodSignature.getParameterNames().length; i++) {
            String parameterName = methodSignature.getParameterNames()[i];
            Object parameterValue = joinPoint.getArgs()[i];
            ParameterInfo parameterInfo = new ParameterInfo();
            parameterInfo.setParameterName(parameterName);
            parameterInfo.setParameterValue(parameterValue);

            parameterInfoList.add(parameterInfo);
        }

        return parameterInfoList;
    }

    private static class ParameterInfo implements Serializable {
        private String parameterName;
        private Object parameterValue;

        public String getParameterName() {
            return parameterName;
        }

        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }

        public Object getParameterValue() {
            return parameterValue;
        }

        public void setParameterValue(Object parameterValue) {
            this.parameterValue = parameterValue;
        }
    }

    /**
     * Evicts relevant method cache information from memory and from database, for the given class, method, keyword name
     * and list of keyword values.
     *
     * @param serviceClass      The service class
     * @param methodName        The method name
     * @param keywordName       The keyword name
     * @param keywordObjectList The list of keyword objects (keyword values)
     */
    public void evictMethodCacheForKeywords(Class serviceClass, String methodName, String keywordName, List<?> keywordObjectList) {
        for (Object keywordObject : keywordObjectList) {
            evictMethodCacheForKeyword(serviceClass, methodName, keywordName, keywordObject);
        }
    }

    /**
     * Evicts relevant method cache information from memory and from database, for the given class, method, keyword name
     * and keyword value.
     *
     * @param serviceClass  The service class
     * @param methodName    The method name
     * @param keywordName   The keyword name
     * @param keywordObject The keyword object
     */
    public void evictMethodCacheForKeyword(Class serviceClass, String methodName, String keywordName, Object keywordObject) {
        evictMethodCacheForKeyword(serviceClass.getSimpleName(), methodName, keywordName, keywordObject);
    }

    public void evictMethodCacheForKeyword(String serviceClassSimpleName, String methodName, String keywordName, Object keywordObject) {
        try {
            if (cacheEnabled && keywordObject != null) {
                String evictionCacheKey = getEvictionCacheKey(serviceClassSimpleName, methodName, keywordName, keywordObject.toString());
                List<String> cacheKeyList = jedisCluster.lrange(evictionCacheKey, 0, -1);

                for (String cacheKey : cacheKeyList) {
                    jedisCluster.del(cacheKey);
                }

                jedisCluster.del(evictionCacheKey);
            }
        } catch (Throwable t) {
            LOGGER.error("Exception in evictMethodCacheForKeyword", t);
        }
    }

    public void set(String cacheKey, String cacheValue) {
        jedisCluster.set(cacheKey, cacheValue);
    }

    public String get(String cacheKey) {
        return jedisCluster.get(cacheKey);
    }

    public Long delete(String cacheKey) {
        return jedisCluster.del(cacheKey);
    }
}

