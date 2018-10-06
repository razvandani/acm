package com.gcr.acm.customerservice.commission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/commission")
public class CommissionController {

	@Autowired
	private CommissionService commissionService;

	/**
	 * Calculates the commission.
	 *
	 * @param searchCommissionCriteria  The search criteria
	 * @return                          The CommissionInfo
	 */
	@RequestMapping(value = "/calculate", method = RequestMethod.POST)
	public CommissionInfo calculateCommission(@RequestBody SearchCommissionCriteria searchCommissionCriteria) throws Exception {
		return commissionService.calculateCommission(searchCommissionCriteria);
	}

}
