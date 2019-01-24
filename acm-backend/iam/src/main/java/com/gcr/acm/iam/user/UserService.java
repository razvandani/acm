package com.gcr.acm.iam.user;

import com.gcr.acm.common.exception.NotFoundException;
import com.gcr.acm.common.utils.Utilities;
import com.gcr.acm.customerservice.commission.AgentCommissionEntity;
import com.gcr.acm.email.EmailSenderService;
import com.gcr.acm.iam.permission.PermissionEAO;
import com.gcr.acm.methodcache.CacheComponent;
import com.gcr.acm.methodcache.MethodCache;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.math.BigInteger;
import java.util.*;

import static com.gcr.acm.common.utils.ValidationUtils.*;

/**
 * User service.
 *
 * @author Razvan Dani
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static final Integer COMMISSION_TYPE_FIX = 1;
    public static final Integer COMMISSION_TYPE_EGO = 2;
    public static final Integer COMMISSION_TYPE_FLEX = 3;
    public static final Integer COMMISSION_TYPE_FLUX = 4;

    @Autowired
    private PasswordEncoder standardPasswordEncoder;

    @Autowired
    private PermissionEAO permissionEAO;

    @Autowired
    private UserEAO userEAO;

    @Autowired
    private CacheComponent methodCacheComponent;

    @Autowired
    private EmailSenderService emailSenderService;

    @Value("${resetPassword.url}")
    private String resetPasswordUrl;

    public static final Integer TOKEN_ACTIVE_NUMBER_OF_DAYS = 2;

    /**
     * Gets the user info for the specified user name.
     *
     * @param username The user name
     * @return The UserInfo
     */
    @Transactional(readOnly = true)
    @MethodCache(resultObjectCacheKeywords = {"username"}, expirationSeconds = 60 * 60 * 24)
    public UserInfo getUserByUserName(String username) {
        validateRequiredObject(username, "username");

        return getUserInfo(userEAO.getUser(username), false);
    }

    /**
     * Gets the user info for the specified id.
     *
     * @param userId The user id
     * @return The UserInfo
     */
    @Transactional(readOnly = true)
    @MethodCache(resultObjectCacheKeywords = {"username"}, expirationSeconds = 60 * 60 * 24)
    public UserInfo getUser(BigInteger userId) {
        validateRequiredObject(userId, "userId");

        UserInfo loginUserInfo = getUserByUserName(UserIdentity.getLoginUserName());

        if (!loginUserInfo.isSuperUser() && !loginUserInfo.getUserId().equals(userId.toString())) {
            throw new ValidationException("login user can only access his own profile");
        }

        return getUserInfo(userEAO.getUser(userId), true);
    }

    private UserInfo getUserInfo(UserEntity userEntity, boolean loadAgentCommissions) {
        if (userEntity == null) {
            throw new EntityNotFoundException("user not found");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userEntity.getUserId().toString());
        userInfo.setUsername(userEntity.getUsername());
        userInfo.setFirstName(userEntity.getFirstName());
        userInfo.setLastName(userEntity.getLastName());

        userInfo.setRoleId(userEntity.getRoleId());
        userInfo.setIsActive(userEntity.getIsActive());
        userInfo.setIsLocked(userEntity.getIsLocked());
        userInfo.setEmail(userEntity.getEmail());
        userInfo.setCreatedDttm(userEntity.getCreatedDttm());
        userInfo.setPhoneNumber(userEntity.getPhoneNumber());

        if (loadAgentCommissions) {
            userInfo.setAgentCommissionInfoList(getAgentCommissionInfoList(userEntity.getAgentCommissionEntitySet()));
        }

        return userInfo;
    }

    private List<UserInfo.AgentCommissionInfo> getAgentCommissionInfoList(Set<AgentCommissionEntity> agentCommissionEntitySet) {
        List<UserInfo.AgentCommissionInfo> agentCommissionInfoList = new ArrayList<>();

        for (AgentCommissionEntity agentCommissionEntity : agentCommissionEntitySet) {
            UserInfo.AgentCommissionInfo agentCommissionInfo = new UserInfo.AgentCommissionInfo();
            agentCommissionInfo.setId(agentCommissionEntity.getId());
            agentCommissionInfo.setCommissionType(agentCommissionEntity.getCommissionType());
            agentCommissionInfo.setCommissionSubcategory(agentCommissionEntity.getCommissionSubcategory());
            agentCommissionInfo.setCommissionSubcategoryStart(agentCommissionEntity.getCommissionSubcategoryStart());
            agentCommissionInfo.setCommissionSubcategoryEnd(agentCommissionEntity.getCommissionSubcategoryEnd());
            agentCommissionInfo.setCommissionValue(agentCommissionEntity.getCommissionValue());

            agentCommissionInfoList.add(agentCommissionInfo);
        }

        return agentCommissionInfoList;
    }

    /**
     * Saves a user.
     *
     * @param userInfo The UserInfo
     * @return The saved UserInfo
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserInfo saveUser(UserInfo userInfo) {
        boolean shouldSendSelfRegistrationEmail = false;
        String activationToken = null;
        String tempPassword = null;

        validateUser(userInfo);

        UserEntity userEntity = populateUserEntity(userInfo);
        evictMethodCacheForUserName(userInfo.getUsername());

        return getUserInfo(userEAO.saveUser(userEntity), false);
    }

    private String generateTempPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return RandomStringUtils.random(15, characters);
    }

    private String generateActivationToken() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return RandomStringUtils.random(15, characters);
    }

    private void validateUser(UserInfo userInfo) {
        validateRequiredObject(userInfo.getUsername(), "username", 45);
        UserInfo loginUserInfo = null;

        if (userInfo.getUserId() == null) {
            validateRequiredObject(userInfo.getPassword(), "password");
        }

        if (!Utilities.isEmptyOrNull(userInfo.getPassword())) {
            validateRangeLength(userInfo.getPassword(), "password", 8, 50);
        }

        validateRequiredObject(userInfo.getEmail(), "email", 45);
        validateRequiredObject(userInfo.getFirstName(), "firstName", 45);
        validateRequiredObject(userInfo.getLastName(), "lastName", 45);
        validateMaxLength(userInfo.getAgentAbbreviation(), "agentAbbreviation", 10);
        validateRequiredObject(userInfo.getPhoneNumber(), "phoneNumber", 20);
        validateRequiredObject(userInfo.getRoleId(), "roleId");

        validateCommissions(userInfo.getAgentCommissionInfoList());

        if (UserIdentity.getLoginUserName() != null) {
            loginUserInfo = getUserByUserName(UserIdentity.getLoginUserName());

            if (!loginUserInfo.isSuperUser()) {
                if (!loginUserInfo.getUserId().equals(userInfo.getUserId())) {
                    throw new ValidationException("Cannot create and edit another user");
                }

                if (userInfo.getRoleId() != null && !loginUserInfo.getRoleId().equals(userInfo.getRoleId())) {
                    throw new ValidationException("not authorized to assign this roleId");
                }
            }
        } else if (userInfo.getUserId() != null) {
            throw new ValidationException("not authorized to edit");
        }

        if (loginUserInfo == null) {
            throw new ValidationException("invalid roleId");
        }
    }

    private void validateCommissions(List<UserInfo.AgentCommissionInfo> agentCommissionInfoList) {
        if (agentCommissionInfoList != null) {
            for (UserInfo.AgentCommissionInfo agentCommissionInfo : agentCommissionInfoList) {
                validateRequiredObject(agentCommissionInfo, "agentCommissionInfo");
//                validateRequiredObject(agentCommissionInfo.getCommissionType(), "commissionType");

                if (agentCommissionInfo.getCommissionType().equals(COMMISSION_TYPE_FLUX)) {
                    validateRequiredObject(agentCommissionInfo.getCommissionSubcategory(), "commissionSubcategory");
                } else {
                    validateRequiredObject(agentCommissionInfo.getCommissionSubcategoryStart(),
                            "commissionSubcategoryStart");
                    validateRequiredObject(agentCommissionInfo.getCommissionSubcategoryEnd(), "commissionSubcategoryEnd");

                    if (agentCommissionInfo.getCommissionSubcategoryStart().compareTo(agentCommissionInfo.getCommissionSubcategoryEnd()) > 0) {
                        throw new ValidationException(
                                "commissionSubcategoryStart cannot be after commissionSubcategoryEnd");
                    }
                }

                validateRequiredObject(agentCommissionInfo.getCommissionValue(), "commissionValue");
            }
        }
    }

    private void validateUserPassword(UserInfo userInfo) {
        validateRequiredObject(userInfo, "userInfo");
        validateRequiredObject(userInfo.getUsername(), "username");
        validateMutuallyExclusiveObjects(userInfo.getPassword(), "password", userInfo.getEncryptedPassword(), "encryptedPassword");
    }

    private UserEntity populateUserEntity(UserInfo userInfo) {
        UserEntity userEntity;
        UserInfo loginUserInfo = UserIdentity.getLoginUserName() != null ? getUserByUserName(UserIdentity.getLoginUserName()) : null;

        if (userInfo.getUserId() != null) {
            userEntity = userEAO.getUser(new BigInteger(userInfo.getUserId()));

            if (userEntity == null) {
                throw new ValidationException("User does not exist");
            }

            if (loginUserInfo == null) {
                throw new ValidationException("Anon users cannot edit existing users");
            }

            if (userEntity.getIsLocked() && userInfo.getIsLocked() != null && !userInfo.getIsLocked() && !loginUserInfo.isSuperUser()) {
                throw new ValidationException("Cannot unlock yourself");
            }

            if (!userEntity.getIsActive() && userInfo.getIsActive() != null && userInfo.getIsActive() && !loginUserInfo.isSuperUser()) {
                throw new ValidationException("Cannot reactivate yourself");
            }
        } else {
            if (userEAO.getUser(userInfo.getUsername()) != null) {
                throw new ValidationException("User name already exists");
            }

            userEntity = new UserEntity();
            userEntity.setCreatedDttm(new Date());
            userEntity.setRoleId(loginUserInfo != null && loginUserInfo.isSuperUser() && userInfo.getRoleId() != null ? userInfo.getRoleId() : UserInfo.ROLE_ID_AGENT);
        }

        if (userInfo.getPassword() != null) {
            userEntity.setPassword(standardPasswordEncoder.encode(userInfo.getPassword()));
        }

        userEntity.setUsername(userInfo.getUsername());
        userEntity.setFirstName(userInfo.getFirstName());
        userEntity.setLastName(userInfo.getLastName());

        if (userInfo.getRoleId() != null && permissionEAO.getRole(userInfo.getRoleId()) == null) {
            throw new ValidationException("roleId does not exist");
        }

        if (userInfo.getIsActive() != null) {
            userEntity.setIsActive(userInfo.getIsActive());
        } else if (userEntity.getUserId() == null) {
            userEntity.setIsActive(true);
        }

        userEntity.setPhoneNumber(userInfo.getPhoneNumber());
        userEntity.setIsLocked(userInfo.getIsLocked() != null ? userInfo.getIsLocked() : userEntity.getIsLocked());

        if (!userInfo.getEmail().equalsIgnoreCase(userEntity.getEmail())
                && userEAO.getUserByEmail(userInfo.getEmail()) != null) {
            throw new ValidationException("Email already exists");
        }

        userEntity.setEmail(userInfo.getEmail());
        userEntity.setCreatedDttm(userInfo.getCreatedDttm());

        populateAgentCommissionEntityList(userInfo, userEntity);

        return userEntity;
    }

    private void populateAgentCommissionEntityList(UserInfo userInfo, UserEntity userEntity) {
        if (userInfo.getAgentCommissionInfoList() != null) {
            Set<AgentCommissionEntity> agentCommissionEntitySet = userEntity.getAgentCommissionEntitySet();

            if (agentCommissionEntitySet == null) {
                agentCommissionEntitySet = new HashSet<>();
                userEntity.setAgentCommissionEntitySet(agentCommissionEntitySet);
            }

            // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
            Map<Integer, UserInfo.AgentCommissionInfo> agentCommissionInfoByIdMap = new HashMap<>();

            if (userInfo.getAgentCommissionInfoList() != null) {
                for (UserInfo.AgentCommissionInfo agentCommissionInfo : userInfo.getAgentCommissionInfoList()) {
                    agentCommissionInfoByIdMap.put(agentCommissionInfo.getId(), agentCommissionInfo);
                }
            }

            Map<Integer, AgentCommissionEntity> agentCommissionEntityByIdMap = new HashMap<>();

            for (AgentCommissionEntity agentCommissionEntity : agentCommissionEntitySet) {
                agentCommissionEntityByIdMap.put(agentCommissionEntity.getId(), agentCommissionEntity);
            }

            // add or update AgentCommissionEntity's
            if (userInfo.getAgentCommissionInfoList() != null) {
                for (UserInfo.AgentCommissionInfo agentCommissionInfo : userInfo.getAgentCommissionInfoList()) {
                    AgentCommissionEntity agentCommissionEntity = agentCommissionEntityByIdMap.get(agentCommissionInfo.getId());

                    if (agentCommissionEntity == null) {
                        agentCommissionEntity = new AgentCommissionEntity();
                        agentCommissionEntitySet.add(agentCommissionEntity);
                    }

                    agentCommissionEntity.setCommissionType(agentCommissionInfo.getCommissionType());
                    agentCommissionEntity.setCommissionSubcategory(agentCommissionInfo.getCommissionSubcategory());
                    agentCommissionEntity.setCommissionSubcategoryStart(agentCommissionInfo.getCommissionSubcategoryStart());
                    agentCommissionEntity.setCommissionSubcategoryEnd(agentCommissionInfo.getCommissionSubcategoryEnd());
                    agentCommissionEntity.setCommissionValue(agentCommissionInfo.getCommissionValue());
                    agentCommissionEntity.setAgentEntity(userEntity);
                }
            }

            // remove relevant entities
            agentCommissionEntitySet.removeIf(agentCommissionEntity -> !agentCommissionInfoByIdMap.containsKey(agentCommissionEntity.getId()));
        }
    }

    @Transactional(readOnly = true)
    @MethodCache(resultObjectCacheKeywords = {"username"}, expirationSeconds = 60 * 60 * 24)
    public UserInfo checkUserAndPassword(UserInfo userPwd) {
        validateUserPassword(userPwd);

        UserEntity userEntity = userEAO.getUser(userPwd.getUsername());

        if (isValidUserAndPassword(userPwd, userEntity)) {
            return getUserInfo(userEntity, false);
        } else {
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserInfo login(UserInfo userPwd) {
        validateUserPassword(userPwd);

        UserEntity userEntity = userEAO.getUser(userPwd.getUsername());

        if (!isValidUserAndPassword(userPwd, userEntity)) {
            if (userEntity == null) {
                throw new ValidationException("Bad Credentials");
            } else {
                UserInfo userInfo = getUserInfo(userEntity, false);

                if (userInfo.getIsLocked()) {
                    userInfo.setErrorMessage("Account is locked");
                } else {
                    userInfo.setErrorMessage("Bad Credentials");
                }

                return userInfo;
            }
        }

        Date lastLoginTimestamp = new Date();

        userEAO.saveUser(userEntity);

        UserInfo userInfo = getUserInfo(userEntity, false);

        if (userEntity.getPassword() != null) {
            userInfo.setLoginToken(Base64Utils.encodeToString((userEntity.getUsername() + ":" + userEntity.getPassword()).getBytes()));
        }

        return userInfo;
    }

    private boolean isValidUserAndPassword(UserInfo userPwd, UserEntity userEntity) {
        boolean isValidUserAndPassword = false;

        if (userPwd.getPassword() != null) {
            isValidUserAndPassword = (userEntity != null && userEntity.getIsActive()
                    && !userEntity.getRoleEntity().getIsDeleted()
                    && standardPasswordEncoder.matches(userPwd.getPassword(), userEntity.getPassword()));
        } else if (userPwd.getEncryptedPassword() != null) {
            isValidUserAndPassword = userEntity.getPassword().equals(userPwd.getEncryptedPassword());
        }

        return isValidUserAndPassword;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void logout(UserInfo userPwd) {
        validateLogout(userPwd);

        UserEntity userEntity = userEAO.getUser(userPwd.getUsername());

        if (userEntity == null || userEntity.getPassword() == null
                || !standardPasswordEncoder.matches(userPwd.getPassword(), userEntity.getPassword())) {
            throw new ValidationException("Logout not allowed");
        }

    }

    private void validateLogout(UserInfo userInfo) {
        validateRequiredObject(userInfo.getUsername(), "userName");
        validateRequiredObject(userInfo.getPassword(), "password");
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserInfo unlockUser(UserInfo userInfo) {
        validateRequiredObject(userInfo.getUsername(), "userName");

        UserEntity userEntity = userEAO.getUser(userInfo.getUsername());
        userEntity.setIsLocked(false);

        userEAO.saveUser(userEntity);

        return getUserInfo(userEntity, false);
    }

    private RoleInfo getRoleInfo(RoleEntity roleEntity) {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setRoleId(roleEntity.getRoleId());
        roleInfo.setRoleName(roleEntity.getRoleName());
        roleInfo.setRoleDesc(roleEntity.getRoleDesc());
        roleInfo.setIsDeleted(roleEntity.getIsDeleted());
        roleInfo.setIsPredefinedRole(roleEntity.getIsPredefinedRole());

        return roleInfo;
    }

    private RoleEntity populateRoleEntity(RoleInfo roleInfo) {
        RoleEntity roleEntity;

        if (roleInfo.getRoleId() != null) {
            roleEntity = permissionEAO.getRole(roleInfo.getRoleId());

            if (roleEntity == null) {
                throw new ValidationException("Role does not exist");
            }
        } else {
            roleEntity = new RoleEntity();
            roleEntity.setIsPredefinedRole(false);
        }

        roleEntity.setRoleName(roleInfo.getRoleName());
        roleEntity.setRoleDesc(roleInfo.getRoleDesc());

        roleEntity.setIsDeleted(false);

        return roleEntity;
    }

    /**
     * Gets a role.
     *
     * @param roleId The role id
     * @return The RoleInfo
     */
    @Transactional(readOnly = true)
    public RoleInfo getRole(Integer roleId) {
        return getRoleInfo(permissionEAO.getRole(roleId));
    }

    /**
     * Finds users for specified search criteria.
     *
     * @param searchUserCriteria The SearchUsersCriteria object
     * @return The List of UserInfo objects
     */
    @Transactional(readOnly = true)
    public List<UserInfo> findUsers(SearchUserCriteria searchUserCriteria) {
        if (UserIdentity.getLoginUserName() == null) {
            throw new ValidationException("unauthorized");
        }

        UserInfo loginUserInfo = getUserByUserName(UserIdentity.getLoginUserName());

        if (!loginUserInfo.isSuperUser() && !loginUserInfo.isAgent()) {
            throw new ValidationException("invalid user role");
        }

//        if (Utilities.isEmptyOrNull(searchUserCriteria.getUsername())
//                && Utilities.isEmptyOrNull(searchUserCriteria.getEmail())
//                && Utilities.isEmptyOrNull(searchUserCriteria.getFirstName())
//                && Utilities.isEmptyOrNull(searchUserCriteria.getLastName())
//                && searchUserCriteria.getUserIdList() == null) {
//            throw new ValidationException("either username or email or firstName or lastName or userIdList is required");
//        }

        List<UserInfo> userInfoList = new ArrayList<>();
        UserEntitySearchCriteria userEntitySearchCriteria = new UserEntitySearchCriteria();

        if (loginUserInfo.isPartner()) {
            userEntitySearchCriteria.setPartnerId(new BigInteger(UserIdentity.getLoginUser().getUserId()));
        }

        if (searchUserCriteria.getUserIdList() != null) {
            List<BigInteger> userIdList = new ArrayList<>();

            for (String userIdString : searchUserCriteria.getUserIdList()) {
                userIdList.add(new BigInteger(userIdString));
            }

            userEntitySearchCriteria.setUserIdList(userIdList);
        }

        if (!Utilities.isEmptyOrNull(searchUserCriteria.getUsername())) {
            userEntitySearchCriteria.setUserName(searchUserCriteria.getUsername());
        }

        if (!Utilities.isEmptyOrNull(searchUserCriteria.getEmail())) {
            userEntitySearchCriteria.setEmail(searchUserCriteria.getEmail());
        }

        userEntitySearchCriteria.setSearchOnlyActive(searchUserCriteria.getSearchOnlyActive());

        if (!Utilities.isEmptyOrNull(searchUserCriteria.getFirstName())) {
            userEntitySearchCriteria.setFirstName(searchUserCriteria.getFirstName());
        }

        if (!Utilities.isEmptyOrNull(searchUserCriteria.getLastName())) {
            userEntitySearchCriteria.setLastName(searchUserCriteria.getLastName());
        }

        userEntitySearchCriteria.setMaxResults(searchUserCriteria.getPageSize());
        userEntitySearchCriteria.setStartResultIndex(searchUserCriteria.getStartIndex());

        if (UserInfo.ROLE_ID_AGENT.equals(searchUserCriteria.getRoleId())) {
            userEntitySearchCriteria.setRoleIdList(Arrays.asList(UserInfo.ROLE_ID_SUPER_USER, UserInfo.ROLE_ID_AGENT));
        } else if (searchUserCriteria.getRoleId() != null) {
            userEntitySearchCriteria.setRoleIdList(Collections.singletonList(searchUserCriteria.getRoleId()));
        }

        List<UserEntity> userEntityList = userEAO.findUsers(userEntitySearchCriteria);

        for (UserEntity userEntity : userEntityList) {
            UserInfo userInfo = getUserInfo(userEntity, false);
            userInfoList.add(userInfo);
        }

        return userInfoList;
    }

    private void evictMethodCacheForUserName(String username) {
        methodCacheComponent.evictMethodCacheForKeyword(UserService.class, "login", "username", username);
        methodCacheComponent.evictMethodCacheForKeyword(UserService.class, "checkUserAndPassword", "username", username);
        methodCacheComponent.evictMethodCacheForKeyword(UserService.class, "getUser", "username", username);
        methodCacheComponent.evictMethodCacheForKeyword(UserService.class, "getUserByUserName", "username", username);
        methodCacheComponent.evictMethodCacheForKeyword("AuthenticationInterceptorCache",
                "findUser", "username", username);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteUser(BigInteger userId) {
        validateRequiredObject(userId, "userId");

        UserEntity userEntity = userEAO.getUser(userId);

        if (userEntity == null) {
            throw new NotFoundException("user does not exist");
        }

        userEntity.setIsActive(false);
        String deletedString = "deleted_" + userEntity.getUserId();
        userEntity.setFirstName(deletedString);
        userEntity.setLastName(deletedString);

        String deletedUuid = "del_" + UUID.randomUUID().toString();
        String userName = userEntity.getUsername();
        userEntity.setUsername(deletedUuid);
        userEntity.setEmail(deletedUuid);

        evictMethodCacheForUserName(userName);

        userEAO.saveUser(userEntity);
    }

    private void validateChangeStatus(UserInfo userInfo) {
        validateRequiredObject(userInfo, "userInfo");
        validateRequiredObject(userInfo.getUserId(), "userId");
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void changePassword(ChangePasswordInfo changePasswordInfo) {
        validateChangePassword(changePasswordInfo);

        UserEntity userEntity = userEAO.getUserByEmail(changePasswordInfo.getEmail());

        if (userEntity == null || !changePasswordInfo.getResetPasswordToken().equals(userEntity.getResetPasswordToken())) {
            throw new NotFoundException("user not found");
        }

        userEntity.setPassword(standardPasswordEncoder.encode(changePasswordInfo.getPassword()));
        userEntity.setResetPasswordToken(null);
        userEAO.saveUser(userEntity);
    }

    private void validatePassword(UserInfo userInfo) {
        validateRequiredObject(userInfo, "userInfo");
        validateRequiredObject(userInfo.getUserId(), "userId");
        validateRequiredObject(userInfo.getPassword(), "password", 8, 50);
    }

    @Transactional(readOnly = true)
    @MethodCache(resultObjectCacheKeywords = {"username"}, expirationSeconds = 60 * 60 * 24)
    public void checkUserIdAndPassword(UserInfo userPwd) {
        validatePassword(userPwd);

        UserEntity userEntity = userEAO.getUser(new BigInteger(userPwd.getUserId()));

        if (userEntity == null || BooleanUtils.isTrue(userEntity.getIsLocked())) {
            throw new ValidationException("user is not activated");
        }

        if (userEntity.getPassword() == null || !standardPasswordEncoder.matches(userPwd.getPassword(), userEntity.getPassword())) {
            throw new ValidationException("password is not valid");
        }
    }

    private void validateChangePassword(ChangePasswordInfo changePasswordInfo) {
        validateRequiredObject(changePasswordInfo, "changePasswordInfo");
        validateRequiredObject(changePasswordInfo.getEmail(), "userName");
        validateRequiredObject(changePasswordInfo.getResetPasswordToken(), "resetPasswordToken");
        validateRequiredObject(changePasswordInfo.getPassword(), "password", 8, 50);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResetPasswordResponseInfo resetPassword(ResetPasswordInfo resetPasswordInfo)
            throws MessagingException {
        validateResetPassword(resetPasswordInfo);

        UserEntity userEntity = userEAO.getUserByEmail(resetPasswordInfo.getEmail());

        if (userEntity == null) {
            throw new ValidationException("user not found");
        }

        userEntity.setResetPasswordToken(generateActivationToken());
        userEAO.saveUser(userEntity);

        sendResetPasswordEmail(resetPasswordInfo.getEmail(), userEntity.getEmail(), userEntity.getResetPasswordToken());

        ResetPasswordResponseInfo resetPasswordResponseInfo = new ResetPasswordResponseInfo();
        resetPasswordResponseInfo.setResetPasswordToken(userEntity.getResetPasswordToken());

        return resetPasswordResponseInfo;
    }

    private void sendResetPasswordEmail(String userName, String email, String resetPasswordToken)
            throws MessagingException {
        String subject = "Cerere de resetare a parolei pentru Pixel Green Energy.";
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Ati primit o cerere de resetare a parolei.<br><br>");
        bodyBuilder.append("Dati click pe acest link pentru a introduce noua parola:<br><br>");
        bodyBuilder.append( resetPasswordUrl + "/" + userName + "/" + resetPasswordToken);

        emailSenderService.sendEmail(email, subject, bodyBuilder.toString());
    }

    private void validateResetPassword(ResetPasswordInfo resetPasswordInfo) {
        validateRequiredObject(resetPasswordInfo, "resetPasswordInfo");
        validateRequiredObject(resetPasswordInfo.getEmail(), "userName");
    }
}
