package com.gcr.acm.customerservice.customer;

import com.gcr.acm.common.exception.NotFoundException;
import com.gcr.acm.common.utils.Utilities;
import com.gcr.acm.common.utils.ValidationUtils;
import com.gcr.acm.customerservice.commissiontype.CommissionTypeEAO;
import com.gcr.acm.customerservice.external.IdentityAccessManagementServiceAdapter;
import com.gcr.acm.customerservice.report.CustomerReportService;
import com.gcr.acm.iam.user.UserIdentity;
import com.gcr.acm.iam.user.UserInfo;
import com.gcr.acm.restclient.RestClient;
import com.gcr.acm.restclient.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gcr.acm.common.utils.ValidationUtils.validateAtLeastOneIsSet;
import static com.gcr.acm.common.utils.ValidationUtils.validateRequiredObject;

/**
 * Service for customers.
 *
 * @author Razvan Dani
 */
@Service
public class CustomerService {
    @Autowired
    private CustomerEAO customerEAO;

    @Autowired
    private CommissionTypeEAO commissionTypeEAO;

    @Autowired
    private CustomerReportService customerReportService;

    @Autowired
    private IdentityAccessManagementServiceAdapter identityAccessManagementServiceAdapter;

    @Autowired
    private RestClient restClient;

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    /**
     * Saves a customer.
     *
     * @param customerInfo The customer info
     * @return The saved customer info
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CustomerInfo saveCustomer(CustomerInfo customerInfo)
            throws Exception {
        validateCustomer(customerInfo);

        CustomerEntity customerEntity = populateCustomerEntity(customerInfo);

        return getCustomerInfo(customerEAO.saveCustomer(customerEntity));
    }

    public CustomerInfo getCustomerInfo(CustomerEntity customerEntity)
            throws Exception {
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setId(customerEntity.getId().toString());
        customerInfo.setContractNumber(customerEntity.getContractNumber());
        customerInfo.setPhoneNumber(customerEntity.getPhoneNumber());
        customerInfo.setFirstName(customerEntity.getFirstName());
        customerInfo.setLastName(customerEntity.getLastName());
        customerInfo.setProductType(customerEntity.getProductType());
        customerInfo.setContractType(customerEntity.getContractType());
        customerInfo.setCommissionSubcategory(customerEntity.getCommissionSubcategory());
        customerInfo.setCountyId(customerEntity.getCountyId());
        customerInfo.setIsActive(customerEntity.getIsActive());
        customerInfo.setLocation(customerEntity.getLocation());
        customerInfo.setStreet(customerEntity.getStreet());
        customerInfo.setStreetNumber(customerEntity.getStreetNumber());
        customerInfo.setContractDate(customerEntity.getContractDate());
        customerInfo.setAgentId(customerEntity.getAgentId().toString());
        customerInfo.setAgentName(customerEntity.getAgentName());
        customerInfo.setStartDeliveryDate(customerEntity.getStartDeliveryDate());
        customerInfo.setStatus(customerEntity.getStatus());
        customerInfo.setCommission(customerEntity.getCommission());

        if (customerEntity.getCountyEntity() != null) {
            customerInfo.setCountyName(customerEntity.getCountyEntity().getName());
        }

        return customerInfo;
    }

    private CustomerEntity populateCustomerEntity(CustomerInfo customerInfo) {
        CustomerEntity customerEntity;

        if (customerInfo.getId() != null) {
            customerEntity = customerEAO.getCustomer(new BigInteger(customerInfo.getId()));

            if (customerEntity == null) {
                throw new NotFoundException("customer does not exist");
            }
        } else {
            customerEntity = new CustomerEntity();
        }

        if (UserIdentity.getLoginUser().isSuperUser()) {
            customerEntity.setStatus(customerInfo.getStatus());
        } else if (UserIdentity.getLoginUser().isAgent() && customerInfo.getId() == null) {
            customerEntity.setStatus(CustomerInfo.STATUS_NOT_DELIVERED_TO_ADMIN);
        }

        customerEntity.setContractNumber(customerInfo.getContractNumber());
        customerEntity.setFirstName(customerInfo.getFirstName());
        customerEntity.setLastName(customerInfo.getLastName());
        customerEntity.setProductType(customerInfo.getProductType());
        customerEntity.setContractType(customerInfo.getContractType());
        customerEntity.setCommissionSubcategory(customerInfo.getCommissionSubcategory());

//        CommissionTypeEntity commissionTypeEntity = commissionTypeEAO.getCommissionSubcategory(customerInfo.getCommissionSubcategory());
        customerEntity.setCommission(customerInfo.getCommission());

        customerEntity.setCountyId(customerInfo.getCountyId());
        customerEntity.setIsActive(customerInfo.getIsActive());
        customerEntity.setLocation(customerInfo.getLocation());
        customerEntity.setStreet(customerInfo.getStreet());
        customerEntity.setStreetNumber(customerInfo.getStreetNumber());
        customerEntity.setContractDate(customerInfo.getContractDate());
        customerEntity.setAgentId(new BigInteger(customerInfo.getAgentId()));
        customerEntity.setPhoneNumber(customerInfo.getPhoneNumber());
        customerEntity.setStartDeliveryDate(customerInfo.getStartDeliveryDate());

        UserInfo userInfo = identityAccessManagementServiceAdapter.getUser(customerEntity.getAgentId());
        customerEntity.setAgentName(userInfo.getFirstName() + " " + userInfo.getLastName());

        return customerEntity;
    }

    private void validateCustomer(CustomerInfo customerInfo) {
        validateRequiredObject(customerInfo, "customerInfo");

        if (UserIdentity.getLoginUser().isSuperUser()) {
            validateRequiredObject(customerInfo.getStatus(), "status");
        }

        validateRequiredObject(customerInfo.getContractNumber(), "contractNumber", 10);
        validateRequiredObject(customerInfo.getFirstName(), "firstName", 50);
        validateRequiredObject(customerInfo.getLastName(), "lastName", 50);
        validateRequiredObject(customerInfo.getProductType(), "productType");
        validateRequiredObject(customerInfo.getContractType(), "contractType");
        validateRequiredObject(customerInfo.getCommissionSubcategory(), "commissionSubcategory");
        validateRequiredObject(customerInfo.getCountyId(), "countyId");
        validateRequiredObject(customerInfo.getLocation(), "location");
        validateRequiredObject(customerInfo.getStreet(), "street");
        validateRequiredObject(customerInfo.getStreetNumber(), "streetNumber");
        validateRequiredObject(customerInfo.getContractDate(), "contractDate");
        validateRequiredObject(customerInfo.getIsActive(), "isActive");
        validateRequiredObject(customerInfo.getAgentId(), "agentId");
        validateRequiredObject(customerInfo.getPhoneNumber(), "phoneNumber");
        validateRequiredObject(customerInfo.getStartDeliveryDate(), "startDeliveryDate");

        if (customerInfo.getStatus().equals(CustomerInfo.STATUS_DELIVERED_TO_ADMIN)
                && UserIdentity.getLoginUser().isSuperUser()) {
            validateRequiredObject(customerInfo.getCommission(), "commission");
        }
    }

    /**
     * Finds customers for specified search criteria.
     *
     * @param searchCustomerCriteria The SearchCustomersCriteria object
     * @return The List of CustomerInfo objects
     */
    @Transactional(readOnly = true)
    public List<CustomerInfo> findCustomers(SearchCustomerCriteria searchCustomerCriteria)
            throws Exception {
        validateFindCustomers(searchCustomerCriteria);

        List<CustomerInfo> customerInfoList = new ArrayList<>();
        CustomerEntitySearchCriteria customerEntitySearchCriteria = constructCustomerEntitySearchCriteria(searchCustomerCriteria);

        List<CustomerEntity> customerEntityList = customerEAO.findCustomers(customerEntitySearchCriteria);
        List<BigInteger> userIdList = new ArrayList<>();

        for (CustomerEntity customerEntity : customerEntityList) {
            CustomerInfo customerInfo = getCustomerInfo(customerEntity);
            customerInfoList.add(customerInfo);
        }

        return customerInfoList;
    }

    private CustomerEntitySearchCriteria constructCustomerEntitySearchCriteria(SearchCustomerCriteria searchCustomerCriteria) {
        CustomerEntitySearchCriteria customerEntitySearchCriteria = new CustomerEntitySearchCriteria();

        if (searchCustomerCriteria!= null) {
            if (!Utilities.isEmptyOrNull(searchCustomerCriteria.getFirstNameStartsWith())) {
                customerEntitySearchCriteria.setFirstNameStartsWith(searchCustomerCriteria.getFirstNameStartsWith() + "%");
            }

            if (!Utilities.isEmptyOrNull(searchCustomerCriteria.getLastNameStartsWith())) {
                customerEntitySearchCriteria.setLastNameStartsWith(searchCustomerCriteria.getLastNameStartsWith() + "%");
            }

            if (searchCustomerCriteria.getCountyId() != null) {
                customerEntitySearchCriteria.setCountyId(searchCustomerCriteria.getCountyId());
            }

            if (!Utilities.isEmptyOrNull(searchCustomerCriteria.getLocationStartsWith())) {
                customerEntitySearchCriteria.setLocationStartsWith(searchCustomerCriteria.getLocationStartsWith() + "%");
            }

            if (searchCustomerCriteria.getIsActive() != null) {
                customerEntitySearchCriteria.setIsActive(searchCustomerCriteria.getIsActive());
            }

            if (searchCustomerCriteria.getAgentId() != null) {
                customerEntitySearchCriteria.setAgentId(new BigInteger(searchCustomerCriteria.getAgentId()));
            }

            customerEntitySearchCriteria.setStartResultIndex(searchCustomerCriteria.getStartIndex());
            customerEntitySearchCriteria.setMaxResults(searchCustomerCriteria.getPageSize());
        }

        return customerEntitySearchCriteria;
    }

    private void validateFindCustomers(SearchCustomerCriteria searchCustomerCriteria) {
        validateRequiredObject(searchCustomerCriteria, "searchCustomerCriteria");
        validateLoginUserCustomer(searchCustomerCriteria.getAgentId());
        validateAtLeastOneIsSet(Arrays.asList(searchCustomerCriteria.getAgentId(), searchCustomerCriteria.getCountyId(),
                searchCustomerCriteria.getFirstNameStartsWith(), searchCustomerCriteria.getLastNameStartsWith(),
                searchCustomerCriteria.getLocationStartsWith()),
                "At least one search criteria must be set");
    }

    private void validateLoginUserCustomer(String agentId) {
        UserInfo loginUser = UserIdentity.getLoginUser();

        if (loginUser != null && loginUser.isAgent()) {
            validateRequiredObject(agentId, "agentId");

            if (!agentId.equals(loginUser.getUserId())) {
                throw new UnauthorizedException("unauthorized");
            }
        } else if (loginUser == null || (!loginUser.isSuperUser())) {
            throw new UnauthorizedException("unauthorized");
        }
    }

    /**
     * Gets the customer info for the specified id.
     *
     * @param customerId The customer id
     * @return The CustomerInfo
     */
    @Transactional(readOnly = true)
    public CustomerInfo getCustomer(BigInteger customerId)
            throws Exception {
        ValidationUtils.validateRequiredObject(customerId, "customerId");
        UserInfo loginUser = UserIdentity.getLoginUser();

        CustomerEntity customerEntity = customerEAO.getCustomer(customerId);

        if (customerEntity == null) {
            throw new NotFoundException("customer not found");
        }

        if (loginUser.isAgent() &&! customerEntity.getAgentId().toString().equals(loginUser.getUserId())) {
            throw new UnauthorizedException("unauthorized");
        }

        if (!loginUser.isAgent() && !loginUser.isSuperUser()) {
            throw new UnauthorizedException("unauthorized");
        }

        return getCustomerInfo(customerEntity);
    }
}
