package com.gcr.acm.common.auth;

import com.gcr.acm.iam.permission.PermissionValidationResponseInfo;
import com.gcr.acm.iam.permission.RestRequestPermissionValidationInfo;
import com.gcr.acm.iam.user.UserInfo;
import com.gcr.acm.methodcache.MethodCache;
import com.gcr.acm.restclient.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class AuthenticationInterceptorCache {

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Lazy
    @Autowired
    private RestClient restClient;

    @MethodCache(resultObjectCacheKeywords = {"permissionId"}, localMemoryExpirationSeconds = 600)
    public PermissionValidationResponseInfo verifyAccess(String method, Integer userRoleId, String endPoint, String path) {
        RestRequestPermissionValidationInfo restRequestPermissionValidationInfo = new RestRequestPermissionValidationInfo();
        restRequestPermissionValidationInfo.setRoleId(userRoleId);
        restRequestPermissionValidationInfo.setRestRequestPath(endPoint);
        restRequestPermissionValidationInfo.setRestRequestMethod(method);

        PermissionValidationResponseInfo permissionValidInfo = restClient.post(path, restRequestPermissionValidationInfo,
                PermissionValidationResponseInfo.class);

        return permissionValidInfo;
    }

    @MethodCache(resultObjectCacheKeywords = {"username"})
    public UserInfo findUser(String path, String userName, String password, String encryptedPassword) throws UnsupportedEncodingException {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(userName);
        userInfo.setPassword(password);
        userInfo.setEncryptedPassword(encryptedPassword);
        userInfo = restClient.post(path, userInfo, UserInfo.class);

        if (userInfo == null || !userInfo.getIsActive()) {
            throw new ForbiddenException("Bad credentials");
        }

        return userInfo;
    }
}
