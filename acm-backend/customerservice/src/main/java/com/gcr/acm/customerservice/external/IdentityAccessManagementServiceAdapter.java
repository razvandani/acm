package com.gcr.acm.customerservice.external;

import com.gcr.acm.common.adaptor.BaseServiceAdapter;
import com.gcr.acm.common.utils.Utilities;
import com.gcr.acm.iam.user.SearchUserCriteria;
import com.gcr.acm.iam.user.UserIdentity;
import com.gcr.acm.iam.user.UserInfo;
import com.gcr.acm.iam.user.UserInfoListResponse;
import com.gcr.acm.restclient.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**Adapter for identity access management service
 *
 * @author Sinziana Chis
 * @since Febr 2018
 */
@Component
public class IdentityAccessManagementServiceAdapter extends BaseServiceAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(IdentityAccessManagementServiceAdapter.class);
	public static final String SERVICE_NAME = "iam-oss";

	@Autowired
	private RestClient restClient;

	public UserInfo getUserInfoForJob(BigInteger userId) throws Exception{
		return restClient.get(chooseService(SERVICE_NAME).getUri() + "/iam-oss/getUserInfoForJob/" + userId , UserInfo.class);
	}

	public UserInfo getUser(BigInteger userId) throws Exception {
		String authorizationHeader = UserIdentity.getAuthorizationHeader();
		UserInfo userInfo;

		if (Utilities.isEmptyOrNull(authorizationHeader)) {
			Map<String, String> headersMap = new HashMap<>();
			headersMap.put("Authorization", getSuperUserAuthorizationHeader());

			userInfo = restClient.get(chooseService(SERVICE_NAME).getUri() + "/iam-oss/" + userId + "/", UserInfo.class, headersMap);
		} else {
			userInfo = restClient.get(chooseService(SERVICE_NAME).getUri() + "/iam-oss/" + userId + "/", UserInfo.class);
		}

		return userInfo;
	}

	public List<UserInfo> findUsers(SearchUserCriteria searchUserCriteria) {
		return restClient.post(chooseService(SERVICE_NAME).getUri() + "/iam-oss/find", searchUserCriteria,
				UserInfoListResponse.class, null).getUserInfoList();
	}
}
