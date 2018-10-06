package com.gcr.acm.iam.user;

/**
 * Contains a thread local with UserInfo associated with login users.
 *
 * @author Razvan Dani
 */
public class UserIdentity {

    private static ThreadLocal<UserInfo> userInfoThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> authorizationHeaderThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> loginUserNameThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> languageCodeThreadLocal = new ThreadLocal<>();

    public static void setUserToThreadLocal(UserInfo userInfo) {
        userInfoThreadLocal.set(userInfo);
    }

    public static UserInfo getLoginUser() {
        return userInfoThreadLocal.get();
    }

    public static void setAuthorizationHeaderToThreadLocal(String authorizationHeader) {
        authorizationHeaderThreadLocal.set(authorizationHeader);
    }

    public static String getAuthorizationHeader() {
        return authorizationHeaderThreadLocal.get();
    }

    public static void setLoginUserNameToTreadLocal(String userName) {
        loginUserNameThreadLocal.set(userName);
    }

    public static String getLoginUserName() {
        return loginUserNameThreadLocal.get();
    }

    public static void setLanguageCodeToThreadLocal(String languageCode) {
        languageCodeThreadLocal.set(languageCode);
    }

    public static String getLanguageCode() {
        return languageCodeThreadLocal.get();
    }
}
