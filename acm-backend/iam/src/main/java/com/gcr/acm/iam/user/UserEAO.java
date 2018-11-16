package com.gcr.acm.iam.user;

import com.gcr.acm.common.utils.BigIntegerIdInfo;
import com.gcr.acm.common.utils.Utilities;
import com.gcr.acm.jpaframework.EntityAccessObjectBase;
import com.gcr.acm.jpaframework.JpaQueryBuilder;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity access object for RM_USER table.
 *
 * @author Razvan Dani
 */
@Component
public class UserEAO extends EntityAccessObjectBase {

    /**
     * Finds and returns the UserEntity for the specified username.
     *
     * @param userName          The username
     * @return                  The UserEntity
     */
    public UserEntity getUser(String userName) {
        UserEntity userEntity = null;

        UserEntitySearchCriteria userEntitySearchCriteria = new UserEntitySearchCriteria();
        userEntitySearchCriteria.setUserName(userName);
        userEntitySearchCriteria.setSearchOnlyActive(false);

        List<UserEntity> userEntityList = findUsers(userEntitySearchCriteria);

        if (userEntityList.size() == 1) {
            userEntity = userEntityList.get(0);
        }

        return userEntity;
    }

    /**
     * Finds and returns the UserEntity for the specified email.
     *
     * @param email             The email
     * @return                  The UserEntity
     */
    public UserEntity getUserByEmail(String email) {
        UserEntity userEntity = null;

        UserEntitySearchCriteria userEntitySearchCriteria = new UserEntitySearchCriteria();
        userEntitySearchCriteria.setEmail(email);
        userEntitySearchCriteria.setSearchOnlyActive(false);

        List<UserEntity> userEntityList = findUsers(userEntitySearchCriteria);

        if (userEntityList.size() == 1) {
            userEntity = userEntityList.get(0);
        }

        return userEntity;
    }

    /**
     * Finds the users for the specified search criteria.
     *
     * @param searchCriteria    The UserEntitySearchCriteria
     * @return                  The List of UserEntity objects
     */
    public List<UserEntity> findUsers(UserEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = constructFindUsersBuilder(searchCriteria);

        return findEntities(queryBuilder, searchCriteria);
    }

    private JpaQueryBuilder constructFindUsersBuilder(UserEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = new JpaQueryBuilder("UserEntity", "u");

        populateQueryBuilderConditionsAndJoins(searchCriteria, queryBuilder);

        return queryBuilder;
    }

    private void populateQueryBuilderConditionsAndJoins(UserEntitySearchCriteria searchCriteria, JpaQueryBuilder queryBuilder) {
        if (searchCriteria.getUserName() != null) {
            queryBuilder.addCondition("u.username = :userName");
        }

        if (searchCriteria.getEmail() != null) {
            queryBuilder.addCondition("u.email = :email");
        }

        if (searchCriteria.getUserIdList() != null) {
            queryBuilder.addCondition("u.userId IN (:userIdList)");
        }

        if (searchCriteria.getRoleIdList() != null) {
            queryBuilder.addCondition("u.roleId IN (:roleIdList)");
        }

        if (searchCriteria.getSearchOnlyActive()) {
            queryBuilder.addCondition("u.isActive = true");
        }

        if (!Utilities.isEmptyOrNull(searchCriteria.getFirstName())) {
            queryBuilder.addCondition("u.firstName = :firstName");
        }

        if (!Utilities.isEmptyOrNull(searchCriteria.getLastName())) {
            queryBuilder.addCondition("u.lastName = :lastName");
        }

        if (searchCriteria.getStatusIdList() != null) {
            queryBuilder.addCondition("u.status IN (:statusIdList)");
        }
    }

    /**
     * Saves the user entity.
     *
     * @param userEntity    The UserEntity
     * @return              The stored UserEntity
     */
    public UserEntity saveUser(UserEntity userEntity) {
        return storeEntity(userEntity);
    }

    public Set<BigInteger> findUserIds(UserEntitySearchCriteria userEntitySearchCriteria) {
        Set<BigInteger> userIdSet = new LinkedHashSet<>();

        JpaQueryBuilder queryBuilder = new JpaQueryBuilder(BigIntegerIdInfo.class);
        queryBuilder.addFromStatement("UserEntity u");
        queryBuilder.addSelectStatement("u.userId AS id");

        populateQueryBuilderConditionsAndJoins(userEntitySearchCriteria, queryBuilder);

        List<BigIntegerIdInfo> idInfoList = findObjects(queryBuilder, userEntitySearchCriteria);

        for (BigIntegerIdInfo idInfo : idInfoList) {
            userIdSet.add(idInfo.getId());
        }

        return userIdSet;
    }

    /**
     * Gets the UserEntity for the specified user id.
     *
     * @param userId    The user id
     * @return          The UserEntity
     */
    public UserEntity getUser(BigInteger userId) {
        return getEntity(UserEntity.class, userId);
    }

    public List<UserInfo> findUserStatusesAndFailedValidationReasons(List<BigInteger> userIdList) {
        UserEntitySearchCriteria userEntitySearchCriteria = new UserEntitySearchCriteria();
        userEntitySearchCriteria.setUserIdList(userIdList);

        JpaQueryBuilder queryBuilder = new JpaQueryBuilder(UserInfo.class);
        queryBuilder.addFromStatement("UserEntity u");
        queryBuilder.addSelectStatement("u.userId AS userId, u.status, u.failedVerificationReason, u.firstName, u.lastName, u.email");

        populateQueryBuilderConditionsAndJoins(userEntitySearchCriteria, queryBuilder);

        return findObjects(queryBuilder, userEntitySearchCriteria);
    }
}
