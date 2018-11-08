package com.gcr.acm.restclient;

import com.gcr.acm.restclient.exception.RestException;
import com.gcr.acm.restclient.exception.RestExceptionFactory;
import com.gcr.acm.restclient.exception.ServerException;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Rest client.
 *
 * @author Razvan Dani
 */
@Component public class RestClient {
	private RestTemplate restTemplate = new RestTemplate();
	private static Method getAuthorizationHeaderMethod;
	private static Method getLanguageCodeMethod;

	static {
		try {
			Class userIdentityClass = Class.forName("com.gcr.acm.iam.user.UserIdentity");

			try {
				getAuthorizationHeaderMethod = userIdentityClass.getMethod("getAuthorizationHeader");
				getLanguageCodeMethod = userIdentityClass.getMethod("getLanguageCode");
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException ignore) {
			// if commons is not the classpath, we don't keep track of UserIdentity
		}
	}

	/**
	 * Sends a POST request without http header.
	 *
	 * @param url           The Url
	 * @param requestObject The request object
	 * @param responseClass The class of the response
	 * @return The response object
	 */
	@SuppressWarnings("unused") public <T> T post(String url, Object requestObject, Class<T> responseClass)
			throws RestException {
		return post(url, requestObject, responseClass, null);
	}

	/**
	 * Sends a POST request without http header.
	 *
	 * @param url           The Url
	 * @param requestObject The request object
	 * @param responseJavaType The JavaType of the response
	 * @return The response object
	 */
	@SuppressWarnings("unused") public <T> T post(
			String url,
			Object requestObject,
			JavaType responseJavaType,
			Map<String, String> requestHeaderMap) {
		HttpEntity<Object> httpEntity = getHttpEntity(requestObject, requestHeaderMap);
		return exchange(url, HttpMethod.POST, responseJavaType, httpEntity);
	}

	/**
	 * Sends a POST request with http headers.
	 *
	 * @param url              The Url
	 * @param requestObject    The request object
	 * @param responseClass    The class of the response
	 * @param requestHeaderMap The map of http request headers
	 * @return The response object
	 */
	public <T> T post(String url, Object requestObject, Class<T> responseClass, Map<String, String> requestHeaderMap)
			throws RestException {
		HttpEntity<Object> httpEntity = getHttpEntity(requestObject, requestHeaderMap);

		return exchange(url, HttpMethod.POST, responseClass, httpEntity);
	}

	private <T> T exchange(
			String url,
			HttpMethod httpMethod,
			Class<T> responseClass,
			HttpEntity<Object> httpEntity,
			Object... uriVariables) {
		return exchangeInternal(url, httpMethod, responseClass, httpEntity, uriVariables);
	}

	private <T> T exchange(
			String url,
			HttpMethod httpMethod,
			JavaType responseType,
			HttpEntity<Object> httpEntity,
			Object... uriVariables) {
		return exchangeInternal(url, httpMethod, responseType, httpEntity, uriVariables);
	}

	private <T> T exchangeInternal(
			String url,
			HttpMethod httpMethod,
			Object responseType,
			HttpEntity<Object> httpEntity,
			Object... uriVariables) {
		try {
			ResponseEntity<String> response = restTemplate
					.exchange(url, httpMethod, httpEntity, String.class, uriVariables);

			if (response != null) {
				String jsonString = response.getBody();

				if (response.getStatusCode().value() < 200 && response.getStatusCode().value() >= 300) {
					throw RestExceptionFactory.getException(response.getStatusCode().value(), jsonString);
				}

				return jsonString == null ? null : JsonUtils.getEntity(jsonString, responseType);
			}

			return null;
		} catch (HttpStatusCodeException e) {
			throw RestExceptionFactory.getException(e.getStatusCode().value(), e.getResponseBodyAsString());
		} catch (RestClientException e) {
			throw new ServerException(new ErrorWrapper(e.getMessage(), e.getClass().getSimpleName()), e);
		}
	}

	/**
	 * Sends a PUT request without http header.
	 *
	 * @param url           The Url
	 * @param requestObject The request object
	 * @param responseClass The class of the response
	 * @return The response object
	 */
	@SuppressWarnings("unused") public <T> T put(String url, Object requestObject, Class<T> responseClass)
			throws RestException {
		return put(url, requestObject, responseClass, null);
	}

	/**
	 * Sends a PUT request without http header.
	 *
	 * @param url           The Url
	 * @param requestObject The request object
	 * @param responseJavaType The JavaType of the response
	 * @return The response object
	 */
	@SuppressWarnings("unused") public <T> T put(
			String url,
			Object requestObject,
			JavaType responseJavaType,
			Map<String, String> requestHeaderMap) {
		HttpEntity<Object> httpEntity = getHttpEntity(requestObject, requestHeaderMap);
		return exchange(url, HttpMethod.PUT, responseJavaType, httpEntity);
	}

	/**
	 * Sends a PUT request with http headers.
	 *
	 * @param url              The Url
	 * @param requestObject    The request object
	 * @param responseClass    The class of the response
	 * @param requestHeaderMap The map of http request headers
	 * @return The response object
	 */
	public <T> T put(String url, Object requestObject, Class<T> responseClass, Map<String, String> requestHeaderMap)
			throws RestException {
		HttpEntity<Object> httpEntity = getHttpEntity(requestObject, requestHeaderMap);
		return exchange(url, HttpMethod.PUT, responseClass, httpEntity);
	}

	/**
	 * Sends a DELETE request without http header.
	 *
	 * @param url           The Url
	 * @param responseClass The class of the response
	 * @return The response object
	 */
	@SuppressWarnings("unused") public <T> T delete(String url, Class<T> responseClass)
			throws RestException {
		return delete(url, responseClass, null);
	}

	/**
	 * Sends a DELETE request without http header.
	 *
	 * @param url           The Url
	 * @param responseJavaType The JavaType of the response
	 * @return The response object
	 */
	@SuppressWarnings("unused") public <T> T delete(
			String url,
			JavaType responseJavaType,
			Map<String, String> requestHeaderMap) {
		HttpEntity<Object> httpEntity = getHttpEntity(null, requestHeaderMap);
		return exchange(url, HttpMethod.DELETE, responseJavaType, httpEntity);
	}

	/**
	 * Sends a DELETE request with http headers.
	 *
	 * @param url              The Url
	 * @param responseClass    The class of the response
	 * @param requestHeaderMap The map of http request headers
	 * @return The response object
	 */
	public <T> T delete(String url, Class<T> responseClass, Map<String, String> requestHeaderMap)
			throws RestException {
		HttpEntity<Object> httpEntity = getHttpEntity(null, requestHeaderMap);
		return exchange(url, HttpMethod.DELETE, responseClass, httpEntity);
	}

	private HttpEntity<Object> getHttpEntity(Object requestObject, Map<String, String> requestHeaderMap) {
		if (requestHeaderMap == null) {
			requestHeaderMap = new HashMap<>();
		}

//		requestHeaderMap.put("Content-Type", "application/json");

		String defaultAuthorizationHeader = null;

		if (getAuthorizationHeaderMethod != null) {
			try {
				defaultAuthorizationHeader = (String) getAuthorizationHeaderMethod.invoke(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (defaultAuthorizationHeader != null) {
			if (!requestHeaderMap.containsKey("Authorization")) {
				requestHeaderMap.put("Authorization", defaultAuthorizationHeader);
			}
		}

		if (!requestHeaderMap.containsKey("Accept-Language")) {
			try {
				String languageCode = (String) getLanguageCodeMethod.invoke(null);

				if (languageCode != null) {
					requestHeaderMap.put("Accept-Language", languageCode);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		for (Map.Entry<String, String> requestHeaderEntrySet : requestHeaderMap.entrySet()) {
			httpHeaders.set(requestHeaderEntrySet.getKey(), requestHeaderEntrySet.getValue());
		}

		return new HttpEntity<>(requestObject, httpHeaders);
	}

	/**
	 * Sends a GET request without http header.
	 *
	 * @param url               The Url
	 * @param responseClass     The class of the response
	 * @param requestParameters Varargs for the request parameters
	 * @return The response object
	 */
	public <T> T get(String url, Class<T> responseClass, Object... requestParameters)
			throws RestException {
		return get(url, responseClass, null, requestParameters);
	}

	/**
	 * Sends a GET request without http header.
	 *
	 * @param url               The Url
	 @param responseJavaType The JavaType of the response
	  * @param requestParameters Varargs for the request parameters
	 * @return The response object
	 */
	public <T> T get(
			String url,
			JavaType responseJavaType,
			Map<String, String> requestHeaderMap,
			Object... requestParameters) {
		HttpEntity<Object> httpEntity = getHttpEntity(null, requestHeaderMap);
		return exchange(url, HttpMethod.GET, responseJavaType, httpEntity, requestParameters);
	}

	public <T> T get(String url, Class<T> responseClass, Map<String, String> requestHeaderMap, Object... requestParams)
			throws RestException {
		HttpEntity<Object> httpEntity = getHttpEntity(null, requestHeaderMap);

		return exchange(url, HttpMethod.GET, responseClass, httpEntity, requestParams);
	}
}
