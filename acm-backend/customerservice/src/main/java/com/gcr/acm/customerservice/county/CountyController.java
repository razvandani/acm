package com.gcr.acm.customerservice.county;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/county")
public class CountyController {

    @Autowired
    private CountyService countyService;

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public List<CountyInfo> findCounties(@RequestBody CountySearchCriteria countySearchCriteria) throws Exception {
        return countyService.findCounties(countySearchCriteria);
    }
}
