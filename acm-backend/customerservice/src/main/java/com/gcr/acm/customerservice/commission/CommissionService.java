package com.gcr.acm.customerservice.commission;

import com.gcr.acm.customerservice.customer.CustomerEAO;
import com.gcr.acm.customerservice.customer.CustomerEntitySearchCriteria;
import com.gcr.acm.customerservice.customer.CustomerInfo;
import com.gcr.acm.iam.user.UserIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.gcr.acm.common.utils.ValidationUtils.validateRequiredObject;

/**
 * Service for commission management.
 *
 * @author Razvan Dani
 */
@Service
public class CommissionService {

    @Value("${customer.commission}")
	private BigDecimal customerCommission;

	@Autowired
	private CustomerEAO customerEAO;

	@Transactional(readOnly = true)
	public CommissionInfo calculateCommission(SearchCommissionCriteria searchCommissionCriteria) {
		validateSearchCommissionCriteria(searchCommissionCriteria);

		CustomerEntitySearchCriteria customerEntitySearchCriteria = new CustomerEntitySearchCriteria();
		customerEntitySearchCriteria.setStartDate(searchCommissionCriteria.getStartDate());
		customerEntitySearchCriteria.setEndDate(searchCommissionCriteria.getEndDate());
		customerEntitySearchCriteria.setStatus(CustomerInfo.STATUS_APPROVED);

		if (UserIdentity.getLoginUser().isAgent()) {
			customerEntitySearchCriteria.setAgentId(new BigInteger(UserIdentity.getLoginUser().getUserId()));
		} else if (UserIdentity.getLoginUser().isSuperUser() && searchCommissionCriteria.getAgentId() != null) {
			customerEntitySearchCriteria.setAgentId(new BigInteger(searchCommissionCriteria.getAgentId()));
		}

		BigDecimal commissionTotal = customerEAO.calculateCommission(customerEntitySearchCriteria);

		CommissionInfo commissionInfo = new CommissionInfo();
		commissionInfo.setCommissionTotal(calculatePercentageFromTotalCommision(commissionTotal != null ? commissionTotal : new BigDecimal(0)));

		return commissionInfo;
	}

	private void validateSearchCommissionCriteria(SearchCommissionCriteria searchCommissionCriteria) {
		validateRequiredObject(searchCommissionCriteria.getStartDate(), "startDate");
		validateRequiredObject(searchCommissionCriteria.getEndDate(), "endDate");
	}

	private BigDecimal calculatePercentageFromTotalCommision(BigDecimal totalCommision) {
		return totalCommision.multiply(customerCommission).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
	}
}
