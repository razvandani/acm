package com.gcr.acm.customerservice.customer;

import com.gcr.acm.common.exception.NotFoundException;
import com.gcr.acm.common.utils.Utilities;
import com.gcr.acm.common.utils.ValidationUtils;
import com.gcr.acm.customerservice.commission.AgentCommissionEntity;
import com.gcr.acm.customerservice.commission.CommissionEAO;
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

import java.math.BigDecimal;
import javax.validation.ValidationException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gcr.acm.common.utils.ValidationUtils.validateAtLeastOneIsSet;
import static com.gcr.acm.common.utils.ValidationUtils.validateRequiredObject;

import javax.validation.ValidationException;

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
    private CommissionEAO commissionEAO;

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
    public CustomerInfo saveCustomer(CustomerInfo customerInfo) {
        validateCustomer(customerInfo);

        if (customerInfo.getId() != null && (UserIdentity.getLoginUser().isAgent()
                || UserIdentity.getLoginUser().isOperator())) {
            throw new UnauthorizedException("unauthorized");
        }

        if (customerInfo.getId() == null && UserIdentity.getLoginUser().isModerator()) {
            throw new UnauthorizedException("unauthorized");
        }

        CustomerEntity customerEntity = populateCustomerEntity(customerInfo);

        return getCustomerInfo(customerEAO.saveCustomer(customerEntity));
    }

    public CustomerInfo getCustomerInfo(CustomerEntity customerEntity) {
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setId(customerEntity.getId().toString());
        customerInfo.setContractNumber(customerEntity.getContractNumber());
        customerInfo.setPhoneNumber(customerEntity.getPhoneNumber());
        customerInfo.setFirstName(customerEntity.getFirstName());
        customerInfo.setLastName(customerEntity.getLastName());
        customerInfo.setProductType(customerEntity.getProductType());
        customerInfo.setCommissionType(customerEntity.getCommissionType());
        customerInfo.setCommissionSubcategory(customerEntity.getCommissionSubcategory());
        customerInfo.setCountyId(customerEntity.getCountyId());
        customerInfo.setLocation(customerEntity.getLocation());
        customerInfo.setStreet(customerEntity.getStreet());
        customerInfo.setStreetNumber(customerEntity.getStreetNumber());
        customerInfo.setFlat(customerEntity.getFlat());
        customerInfo.setStairNumber(customerEntity.getStairNumber());
        customerInfo.setApartmentNumber(customerEntity.getApartmentNumber());
        customerInfo.setContractDate(customerEntity.getContractDate());
        customerInfo.setAgentId(customerEntity.getAgentId().toString());

        if (!UserIdentity.getLoginUser().isModerator()) {
            customerInfo.setAgentName(customerEntity.getAgentName());
        }

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

        if ((UserIdentity.getLoginUser().isSuperUser() || UserIdentity.getLoginUser().isModerator())
                && customerInfo.getStatus() != null) {
            customerEntity.setStatus(customerInfo.getStatus());
        } else if (customerInfo.getId() == null) {
            customerEntity.setStatus(CustomerInfo.STATUS_ACTIVE);
        }


        if (!UserIdentity.getLoginUser().isModerator()) {
            customerEntity.setContractNumber(customerInfo.getContractNumber());
            customerEntity.setFirstName(customerInfo.getFirstName());
            customerEntity.setLastName(customerInfo.getLastName());
            customerEntity.setProductType(customerInfo.getProductType());
            customerEntity.setCommissionType(customerInfo.getCommissionType());
            customerEntity.setCommissionSubcategory(customerInfo.getCommissionSubcategory());

            if (customerInfo.getId() == null) {
                customerEntity.setCommission(calculateCommission(customerInfo));
            } else if (UserIdentity.getLoginUser().isSuperUser() && customerInfo.getCommission() != null) {
                customerEntity.setCommission(customerInfo.getCommission());
            }

            customerEntity.setCountyId(customerInfo.getCountyId());
            customerEntity.setLocation(customerInfo.getLocation());
            customerEntity.setStreet(customerInfo.getStreet());
            customerEntity.setStreetNumber(customerInfo.getStreetNumber());
            customerEntity.setFlat(customerInfo.getFlat());
            customerEntity.setStairNumber(customerInfo.getStairNumber());
            customerEntity.setApartmentNumber(customerInfo.getApartmentNumber());
            customerEntity.setContractDate(customerInfo.getContractDate());

            if (UserIdentity.getLoginUser().isSuperUser()) {
                customerEntity.setAgentId(new BigInteger(customerInfo.getAgentId()));
            } else if (UserIdentity.getLoginUser().isAgent()) {
                customerEntity.setAgentId(new BigInteger(UserIdentity.getLoginUser().getUserId()));
            }

            customerEntity.setPhoneNumber(customerInfo.getPhoneNumber());
            customerEntity.setStartDeliveryDate(customerInfo.getStartDeliveryDate());

            UserInfo userInfo = identityAccessManagementServiceAdapter.getUser(customerEntity.getAgentId());
            customerEntity.setAgentName(userInfo.getFirstName() + " " + userInfo.getLastName());
        }

        return customerEntity;
    }

    private BigDecimal calculateCommission(CustomerInfo customerInfo) {
        AgentCommissionEntity agentCommissionEntity = null;

        if (customerInfo.getProductType().equals(CustomerInfo.PRODUCT_TYPE_NATURAL_GAS)) {
            agentCommissionEntity =
                    commissionEAO.getAgentCommissionForNaturalGas(new BigInteger(customerInfo.getAgentId()),
                            2, customerInfo.getCommissionSubcategory()); // QUICK-FIX: in order to get and set comissions for B1 - B4
        } else if (customerInfo.getProductType().equals(CustomerInfo.PRODUCT_TYPE_ELECTRIC_ENERGY)) {
            agentCommissionEntity =
                    commissionEAO.getAgentCommissionForElectricCurrent(new BigInteger(customerInfo.getAgentId()),
                            customerInfo.getCommissionSubcategory());
        }

        BigDecimal commission;

        if (agentCommissionEntity != null) {
            commission = agentCommissionEntity.getCommissionValue();
        } else {
            throw new ValidationException("commission not found");
        }

        return commission;
    }

    private void validateCustomer(CustomerInfo customerInfo) {
        validateRequiredObject(customerInfo, "customerInfo");

        if (UserIdentity.getLoginUser().isSuperUser() || UserIdentity.getLoginUser().isModerator()) {
            validateRequiredObject(customerInfo.getStatus(), "status");
        }

        if (!UserIdentity.getLoginUser().isModerator()) {
            validateRequiredObject(customerInfo.getContractNumber(), "contractNumber", 10);
            validateRequiredObject(customerInfo.getFirstName(), "firstName", 50);
            validateRequiredObject(customerInfo.getLastName(), "lastName", 50);
            validateRequiredObject(customerInfo.getProductType(), "productType");
            validateRequiredObject(customerInfo.getCommissionType(), "commissionType");

            if (customerInfo.getProductType().equals(CustomerInfo.PRODUCT_TYPE_NATURAL_GAS)) {
                if (!customerInfo.getCommissionType().equals(CustomerInfo.COMMISSION_TYPE_FLUX)) {
                    throw new ValidationException("Invalid contract type");
                }
            } else if (customerInfo.getProductType().equals(CustomerInfo.PRODUCT_TYPE_ELECTRIC_ENERGY)) {
                if (customerInfo.getCommissionType().equals(CustomerInfo.COMMISSION_TYPE_FLUX)) {
                    throw new ValidationException("Invalid contract type");
                }
            }

            validateRequiredObject(customerInfo.getCommissionSubcategory(), "commissionSubcategory");
            validateRequiredObject(customerInfo.getCountyId(), "countyId");
            validateRequiredObject(customerInfo.getLocation(), "location");
            validateRequiredObject(customerInfo.getStreet(), "street");
            validateRequiredObject(customerInfo.getStreetNumber(), "streetNumber");
            validateRequiredObject(customerInfo.getContractDate(), "contractDate");

            if (UserIdentity.getLoginUser().isSuperUser()) {
                validateRequiredObject(customerInfo.getAgentId(), "agentId");
            }

            validateRequiredObject(customerInfo.getPhoneNumber(), "phoneNumber");
            validateRequiredObject(customerInfo.getStartDeliveryDate(), "startDeliveryDate");
        }
    }

    /**
     * Finds customers for specified search criteria.
     *
     * @param searchCustomerCriteria The SearchCustomersCriteria object
     * @return The List of CustomerInfo objects
     */
    @Transactional(readOnly = true)
    public List<CustomerInfo> findCustomers(SearchCustomerCriteria searchCustomerCriteria) {
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

        if (searchCustomerCriteria != null) {
            if (searchCustomerCriteria.getStatus() != null) {
                customerEntitySearchCriteria.setStatus(searchCustomerCriteria.getStatus());
            }

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

            if (!UserIdentity.getLoginUser().isAgent() && searchCustomerCriteria.getAgentId() != null) {
                customerEntitySearchCriteria.setAgentId(new BigInteger(searchCustomerCriteria.getAgentId()));
            } else if (UserIdentity.getLoginUser().isAgent()) {
                customerEntitySearchCriteria.setAgentId(new BigInteger(UserIdentity.getLoginUser().getUserId()));
            }

            customerEntitySearchCriteria.setStartDate(searchCustomerCriteria.getStartDate());
            customerEntitySearchCriteria.setEndDate(searchCustomerCriteria.getEndDate());
            customerEntitySearchCriteria.setDeliveryStartDate(searchCustomerCriteria.getDeliveryStartDate());
            customerEntitySearchCriteria.setDeliveryEndDate(searchCustomerCriteria.getDeliveryEndDate());
            customerEntitySearchCriteria.setProductType(searchCustomerCriteria.getProductType());
            customerEntitySearchCriteria.setContractNumber(searchCustomerCriteria.getContractNumber());

            if (searchCustomerCriteria.getStreet() != null) {
                customerEntitySearchCriteria.setStreet("%" + searchCustomerCriteria.getStreet() + "%");
            }

            customerEntitySearchCriteria.setStartResultIndex(searchCustomerCriteria.getStartIndex());
            customerEntitySearchCriteria.setMaxResults(searchCustomerCriteria.getPageSize());
        }

        return customerEntitySearchCriteria;
    }

    private void validateFindCustomers(SearchCustomerCriteria searchCustomerCriteria) {
        validateRequiredObject(searchCustomerCriteria, "searchCustomerCriteria");
//        validateLoginUserCustomer(searchCustomerCriteria.getAgentId());
        validateAtLeastOneIsSet(Arrays.asList(searchCustomerCriteria.getAgentId(), searchCustomerCriteria.getCountyId(),
                searchCustomerCriteria.getFirstNameStartsWith(), searchCustomerCriteria.getLastNameStartsWith(),
                searchCustomerCriteria.getLocationStartsWith(), searchCustomerCriteria.getStartDate(), searchCustomerCriteria.getEndDate(),
                searchCustomerCriteria.getDeliveryStartDate(), searchCustomerCriteria.getDeliveryEndDate()),
                "At least one search criteria must be set");
    }

//    private void validateLoginUserCustomer(String agentId) {
//        UserInfo loginUser = UserIdentity.getLoginUser();
//
//        if (loginUser != null && loginUser.isAgent()) {
//            validateRequiredObject(agentId, "agentId");
//
//            if (!agentId.equals(loginUser.getUserId())) {
//                throw new UnauthorizedException("unauthorized");
//            }
//        } else if (loginUser == null || (!loginUser.isSuperUser())) {
//            throw new UnauthorizedException("unauthorized");
//        }
//    }

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

        if (loginUser.isAgent() && !customerEntity.getAgentId().toString().equals(loginUser.getUserId())) {
            throw new UnauthorizedException("unauthorized");
        }

        return getCustomerInfo(customerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteCustomer(BigInteger customerId) {
        customerEAO.deleteCustomer(customerId);
    }
}
