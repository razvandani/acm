package com.gcr.acm.customerservice.customer;

import com.gcr.acm.common.utils.SuccessObject;
import com.gcr.acm.customerservice.report.CustomerReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

/**
 * Controller for customers.
 *
 * @author Razvan Dani
 */
@RestController
@RequestMapping(value = "/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerReportService customerReportService;

    /**
     * Finds customers.
     *
     * @param searchCustomerCriteria    The search criteria
     * @return                          The list of CustomerInfo objects
     */
    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public List<CustomerInfo> findCustomers(@RequestBody SearchCustomerCriteria searchCustomerCriteria) throws Exception {
        return customerService.findCustomers(searchCustomerCriteria);
    }

    @RequestMapping(value = "/{customerId}/", method = RequestMethod.GET)
    public CustomerInfo getCustomer(@PathVariable BigInteger customerId) throws Exception {
        return customerService.getCustomer(customerId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerInfo createCustomer(@RequestBody CustomerInfo customerInfo) throws Exception {
        return customerService.saveCustomer(customerInfo);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SuccessObject updateCustomer(@RequestBody CustomerInfo customerInfo) throws Exception {
        customerService.saveCustomer(customerInfo);
        return new SuccessObject();
    }

    @RequestMapping(value = "/{customerId}/", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SuccessObject deleteCustomer(@PathVariable BigInteger customerId) throws Exception {
        customerService.deleteCustomer(customerId);
        return new SuccessObject();
    }


    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public String exportCustomers(@RequestBody SearchCustomerCriteria searchCustomerCriteria) throws Exception {
        return customerReportService.getCustomerReportInExcelFormat(searchCustomerCriteria);
    }
}
