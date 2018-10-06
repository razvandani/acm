package com.gcr.acm.common.auth;

import com.gcr.acm.iam.user.UserIdentity;
import com.gcr.acm.iam.user.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Authentication filter.
 *
 * @author Razvan Dani
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private static final List<String> PUBLIC_URI_LIST = Arrays.asList("login",
            "checkPermissionForRequest",
            "checkUserAndPassword", "/configuration/ui", "/configuration/security",
            "swagger", "/error", "api-docs");
    @Value("${spring.application.name}")
    private String springApplicationName;

    @Autowired
    private AuthenticationInterceptorCache authenticationInterceptorCache;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null && request.getHeader("api_key") != null) {
            String apiKey  = URLDecoder.decode(request.getHeader("api_key"), "UTF-8");
            authorizationHeader = "Basic " + Base64Utils.encodeToString(apiKey.getBytes());
        }

        UserIdentity.setAuthorizationHeaderToThreadLocal(authorizationHeader);

        if (authorizationHeader != null
//                && authorizationHeader.startsWith("Basic")
                && !request.getRequestURI().contains("login")
                && !request.getRequestURI().contains("resetPassword")
                ) {
            String userName;
            String password = null;
            String encryptedPassword = null;

            if (authorizationHeader.startsWith("Basic")) {
                String base64Credentials = authorizationHeader.substring("Basic".length()).trim();
                String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                        Charset.forName("UTF-8"));
                final String[] usernameAndPassword = credentials.split(":", 2);
                userName = usernameAndPassword[0];
                password = usernameAndPassword[1];
            } else {
                String credentials = new String(Base64.getDecoder().decode(authorizationHeader),
                        Charset.forName("UTF-8"));
                final String[] usernameAndPassword = credentials.split(":", 2);
                userName = usernameAndPassword[0];
                encryptedPassword = usernameAndPassword[1];
            }

            UserIdentity.setLoginUserNameToTreadLocal(userName);

            if (!request.getRequestURI().startsWith("/iam") && !request.getRequestURI().contains("checkPermissionForRequest")
                    && !request.getRequestURI().equals("/error")) {
                ServiceInstance serviceInstance = loadBalancer.choose("iam");

                if (serviceInstance != null) {
                   UserInfo userInfo = authenticationInterceptorCache.findUser(serviceInstance.getUri() + "/iam/checkUserAndPassword", userName,
                            password, encryptedPassword);

                    if (!isPublicUri(request) && !authenticationInterceptorCache.verifyAccess(request.getMethod(), userInfo.getRoleId(),
                            "/" + springApplicationName + request.getRequestURI(),
                            serviceInstance.getUri() + "/permission/checkPermissionForRequest").getIsPermissionValid()) {
                        throw new ForbiddenException("The user is not authorized to perform the operation " + request.getRequestURI());
                    }

                    UserIdentity.setUserToThreadLocal(userInfo);
                } else {
                    throw new ForbiddenException("The user is not authorized to perform the operation " + request.getRequestURI());
                }
            } else {
                UserIdentity.setUserToThreadLocal(null);
            }
        } else if (authorizationHeader == null
                && !isPublicUri(request)) {
            LOGGER.error("user is not authorized for requestURI " + request.getRequestURI());
            throw new ForbiddenException("user is not authorized for requestURI " + request.getRequestURI());
        } else {
            UserIdentity.setLoginUserNameToTreadLocal(null);
            UserIdentity.setAuthorizationHeaderToThreadLocal(authorizationHeader);
            UserIdentity.setUserToThreadLocal(null);
        }

        String languageCode = request.getHeader("accept-language");
        UserIdentity.setLanguageCodeToThreadLocal(languageCode);

        return true;
    }

    public boolean isPublicUri(HttpServletRequest request) {
        for (String publicUri : PUBLIC_URI_LIST) {
            if (request.getRequestURI().contains(publicUri)) {
                return true;
            }
        }

        if (request.getRequestURI().endsWith("/iam") && request.getMethod().equals("POST")) {
            return true;
        }

        return request.getRequestURI().endsWith("/login");
    }


    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

    }
}
