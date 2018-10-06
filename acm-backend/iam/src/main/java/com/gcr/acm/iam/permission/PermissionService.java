package com.gcr.acm.iam.permission;

import com.gcr.acm.common.exception.NotFoundException;
import com.gcr.acm.methodcache.CacheComponent;
import com.gcr.acm.methodcache.MethodCache;
import com.google.common.collect.Sets;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gcr.acm.common.utils.ValidationUtils.validateRequiredObject;

/**
 * Permission Service.
 *
 * @author Razvan Dani
 */
@Service
public class PermissionService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    private PermissionEAO permissionEAO;

    @Autowired
    private CacheComponent methodCacheComponent;

    @Transactional(readOnly = true)
    public PermissionInfo getPermission(Integer permissionId) {
        validateRequiredObject(permissionId, "permissionId");

        PermissionInfo permissionInfo = getPermissionInfo(permissionEAO.getPermission(permissionId), true);

        if (permissionInfo == null) {
            throw new NotFoundException("Permission with id " + permissionId + " not found");
        }

        return permissionInfo;
    }


    @Transactional(readOnly = true)
    @MethodCache(resultObjectCacheKeywords = {"permissionId"}, localMemoryExpirationSeconds = 60 * 10)
    public PermissionValidationResponseInfo checkPermissionForRequest(RestRequestPermissionValidationInfo restRequestPermissionValidationInfo) {
        String restRequestPath = restRequestPermissionValidationInfo.getRestRequestPath();

        if (restRequestPath.endsWith("/")) {
            restRequestPath = restRequestPath.substring(0, restRequestPath.length() - 1);
        }

        String[] restRequestPathElements = restRequestPath.split("/");
        String lastRequestPathElement = restRequestPathElements[restRequestPathElements.length-1];

        if (NumberUtils.isNumber(lastRequestPathElement)) {
            restRequestPath = restRequestPath.substring(0, restRequestPath.length() - 1 - lastRequestPathElement.length());

            restRequestPathElements = restRequestPath.split("/");
            lastRequestPathElement = restRequestPathElements[restRequestPathElements.length-1];

            if (NumberUtils.isNumber(lastRequestPathElement)) {
                Integer lastSlashIndex = restRequestPath.lastIndexOf("/");
                restRequestPathElements = restRequestPath.split("/");

                if (NumberUtils.isNumber(restRequestPathElements[restRequestPathElements.length - 1])) {
                    restRequestPath = restRequestPath.substring(0, lastSlashIndex);
                }
            }
        }

        restRequestPermissionValidationInfo.setRestRequestPath(restRequestPath);

        PermissionValidationResponseInfo permissionValidInfo = new PermissionValidationResponseInfo();

        PermissionEntity permissionEntity = permissionEAO.getPermissionForRestRequest(restRequestPermissionValidationInfo.getRoleId(),
                restRequestPermissionValidationInfo.getRestRequestPath(), restRequestPermissionValidationInfo.getRestRequestMethod());
        permissionValidInfo.setIsPermissionValid(permissionEntity != null);

        return permissionValidInfo;
    }



    public PermissionEntity checkExistingPermissionEntity(PermissionInfo permissionInfo) {
        if (permissionInfo.getPermissionId() == null) {
            return null; //create
        }
        //update
        PermissionEntity permissionEntity = permissionEAO.getPermission(permissionInfo.getPermissionId());

        if (permissionEntity == null) {
            throw new ValidationException("Permission with id " + permissionInfo.getPermissionId() + " does not exist");
        }

        return permissionEntity;
    }



    private void validatePermissionRestMethods(Set<PermissionRestMethodInfo> permissionRestMethodInfoSet) {
        if (!CollectionUtils.isEmpty(permissionRestMethodInfoSet)) {
            for (PermissionRestMethodInfo permissionRestMethodInfo : permissionRestMethodInfoSet) {
                validateRequiredObject(permissionRestMethodInfo.getRestRequestPath(), "Permission Rest Path", 200);
                validateRequiredObject(permissionRestMethodInfo.getRestRequestMethod(), "Permission Rest Method", 10);
            }
        }
    }

    private void validateUniquePermissionName(String permissionName) {
        PermissionEntitySearchCriteria searchCriteria = new PermissionEntitySearchCriteria();
        searchCriteria.setPermissionName(permissionName);

        if (!CollectionUtils.isEmpty(permissionEAO.findPermissions(searchCriteria))) {
            throw new ValidationException("Permission name already exists");
        }
    }

    private void validateUniquePermissionCode(String permissionCode) {
        PermissionEntitySearchCriteria searchCriteria = new PermissionEntitySearchCriteria();
        searchCriteria.setPermissionCode(permissionCode);

        if (!CollectionUtils.isEmpty(permissionEAO.findPermissions(searchCriteria))) {
            throw new ValidationException("Permission code already exists");
        }
    }

    private void updatePermissionRestEntities(PermissionInfo permissionInfo, PermissionEntity permissionEntity,
                                              Set<PermissionRestMethodEntity> existingPermissionRestMethodEntitySet) {
        Set<Pair<String, String>> newMethodKeys;
        Set<Pair<String, String>> existingMethodKeys;

        if (permissionInfo.getPermissionRestMethods() == null) {
            newMethodKeys = new HashSet<>();
        } else {
            newMethodKeys = permissionInfo.getPermissionRestMethods().stream()
                    .map(m -> buildRestMethodKey(m.getRestRequestMethod(), m.getRestRequestPath()))
                    .collect(Collectors.toSet());
        }

        existingMethodKeys = existingPermissionRestMethodEntitySet.stream()
                .map(m -> buildRestMethodKey(m.getRestRequestMethod(), m.getRestRequestPath()))
                .collect(Collectors.toSet());

        Sets.SetView<Pair<String, String>> toAdd = Sets.difference(newMethodKeys, existingMethodKeys);
        Set<PermissionRestMethodEntity> permissionRestMethodEntitySetToAdd = new HashSet<>();

        for (Pair<String, String> restMethodKey : toAdd) {
            PermissionRestMethodEntity permissionRestMethodEntity = new PermissionRestMethodEntity();
            permissionRestMethodEntity.setRestRequestMethod(restMethodKey.getLeft());
            permissionRestMethodEntity.setRestRequestPath(restMethodKey.getRight());
            permissionRestMethodEntitySetToAdd.add(permissionRestMethodEntity);
        }
        permissionEntity.addPermissionRestMethodEntities(permissionRestMethodEntitySetToAdd);

        Sets.SetView<Pair<String, String>> toDelete = Sets.difference(existingMethodKeys, newMethodKeys);
        Set<PermissionRestMethodEntity> permissionRestMethodEntitySetToRemove = new HashSet<>();

        for (PermissionRestMethodEntity permissionRestMethodEntity : existingPermissionRestMethodEntitySet) {
            ImmutablePair<String, String> restMethodKey = buildRestMethodKey(
                    permissionRestMethodEntity.getRestRequestMethod(), permissionRestMethodEntity.getRestRequestPath());
            if (toDelete.contains(restMethodKey)) {
                permissionRestMethodEntitySetToRemove.add(permissionRestMethodEntity);
            }
        }

        permissionEntity.removePermissionRestMethodEntities(permissionRestMethodEntitySetToRemove);
    }

    private ImmutablePair<String, String> buildRestMethodKey(String restRequestMethod, String restRequestPath) {
        return new ImmutablePair<>(restRequestMethod, restRequestPath);
    }

    private PermissionRestMethodEntity getPermissionRestMethodEntity(String restRequestMethod, String restRequestPath) {
        PermissionRestMethodEntity permissionRestMethodEntity = new PermissionRestMethodEntity();
        permissionRestMethodEntity.setRestRequestMethod(restRequestMethod);
        permissionRestMethodEntity.setRestRequestPath(restRequestPath);
        return permissionRestMethodEntity;
    }

    private RolePermissionEntity createRolePermissionEntity(Integer roleId) {
        RolePermissionEntity rolePermissionEntity = new RolePermissionEntity();
        rolePermissionEntity.setRoleId(roleId);
        return rolePermissionEntity;
    }

    private PermissionInfo getPermissionInfo(PermissionEntity permissionEntity, boolean includeRestMethods) {
        if (permissionEntity == null) {
            return null;
        }
        PermissionInfo permissionInfo = new PermissionInfo();

        permissionInfo.setPermissionId(permissionEntity.getPermissionId());
        permissionInfo.setPermissionCode(permissionEntity.getPermissionCode());
        permissionInfo.setPermissionName(permissionEntity.getPermissionName());
        permissionInfo.setPermissionDesc(permissionEntity.getPermissionDesc());
        permissionInfo.setIsDeleted(permissionEntity.getIsDeleted());

        if (includeRestMethods && permissionEntity.getPermissionRestMethodEntitySet() != null) {
            permissionInfo.setPermissionRestMethods(permissionEntity.getPermissionRestMethodEntitySet().stream()
                    .map(this::getPermissionRestMethodInfo)
                    .collect(Collectors.toSet()));
        }

        if (permissionEntity.getRolePermissionEntitySet() != null) {
            permissionInfo.setRoleIds(permissionEntity.getRolePermissionEntitySet().stream()
                    .map(RolePermissionEntity::getRoleId)
                    .collect(Collectors.toSet()));
        }

        return permissionInfo;
    }

    private PermissionRestMethodInfo getPermissionRestMethodInfo(PermissionRestMethodEntity permissionRestMethodEntity) {
        PermissionRestMethodInfo permissionRestMethodInfo = new PermissionRestMethodInfo();
        permissionRestMethodInfo.setRestRequestMethod(permissionRestMethodEntity.getRestRequestMethod());
        permissionRestMethodInfo.setRestRequestPath(permissionRestMethodEntity.getRestRequestPath());
        return permissionRestMethodInfo;
    }

    private void evictMethodCacheForPermission(Integer permissionId) {
        if (permissionId != null) {
            methodCacheComponent.evictMethodCacheForKeyword(PermissionService.class, "checkPermissionForRequest", "permissionId", permissionId);
            methodCacheComponent.evictMethodCacheForKeyword("AuthenticationInterceptorCache", "verifyAccess", "permissionId", permissionId);
        } else {
            methodCacheComponent.evictMethodCacheForKeyword(PermissionService.class, "checkPermissionForRequest", "cacheConstant", "_");
            methodCacheComponent.evictMethodCacheForKeyword("AuthenticationInterceptorCache", "verifyAccess", "cacheConstant", "_");
        }
    }
}
