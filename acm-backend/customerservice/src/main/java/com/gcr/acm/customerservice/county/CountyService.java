package com.gcr.acm.customerservice.county;

import com.gcr.acm.common.utils.Utilities;
import com.gcr.acm.methodcache.MethodCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.gcr.acm.common.utils.ValidationUtils.validateRequiredObject;

@Service public class CountyService {
	@Autowired private CountyEAO countyEAO;

	private CountyInfo getCountyInfo(CountyEntity countyEntity) {
		CountyInfo countyInfo = new CountyInfo();
		countyInfo.setId(countyEntity.getId());
		countyInfo.setName(countyEntity.getName());

		return countyInfo;
	}

	/**
	 * Finds employmentTypes by the specified search criteria.
	 *
	 * @return The List of EmploymentTypeInfo objects
	 */
	@Transactional(readOnly = true)
	@MethodCache(resultObjectCacheKeywords = {}, expirationSeconds = 60 * 60 * 24, localMemoryExpirationSeconds = 600)
	public List<CountyInfo> findCounties(CountySearchCriteria countySearchCriteria) {
		validateRequiredObject(countySearchCriteria, "countySearchCriteria");

		List<CountyInfo> countyInfoList = new ArrayList<>();
		CountyEntitySearchCriteria entitySearchCriteria = getCountyEntitySearchCriteria(countySearchCriteria);

		List<CountyEntity> countyEntityList = countyEAO.findCounties(entitySearchCriteria);

		for (CountyEntity countyEntity : countyEntityList) {
			countyInfoList.add(getCountyInfo(countyEntity));
		}

		return countyInfoList;
	}

	private CountyEntitySearchCriteria getCountyEntitySearchCriteria(CountySearchCriteria countySearchCriteria) {
		CountyEntitySearchCriteria countyEntitySearchCriteria = new CountyEntitySearchCriteria();
		countyEntitySearchCriteria.setMaxResults(countySearchCriteria.getPageSize());
		countyEntitySearchCriteria.setStartResultIndex(countySearchCriteria.getStartIndex());

		if (!Utilities.isEmptyOrNull(countySearchCriteria.getNameStartsWith())) {
			countyEntitySearchCriteria.setNameStartsWith(countySearchCriteria.getNameStartsWith() + "%");
		}

		return countyEntitySearchCriteria;
	}
}
