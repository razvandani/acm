package com.gcr.acm.common.adaptor;

import com.gcr.acm.common.utils.EncryptionUtil;
import com.gcr.acm.restclient.exception.UnavailableServiceInstanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.util.Base64Utils;

/**
 * Adapter class for services
 *
 * @author Cristina.Jurcovan
 * @since OSS MVP
 */
public abstract class BaseServiceAdapter {

	private static final Logger log = LoggerFactory.getLogger(BaseServiceAdapter.class);

	@Autowired
	private LoadBalancerClient loadBalancer;

	@Autowired
	private EncryptionUtil encryptionUtil;

	@Value("${scheduledtasks.superuser.username}")
	private String scheduledTasksSuperuserUsername;
	@Value("${scheduledtasks.superuser.password}")
	private String scheduledTasksSuperuserPassword;

	private String authorizationHeader;


	public ServiceInstance chooseService(String serviceName) {
		ServiceInstance serviceInstance = loadBalancer.choose(serviceName);

		if (serviceInstance == null) {
			log.error("Service {} is Unavailable!", serviceName);
			throw new UnavailableServiceInstanceException(serviceName);
		}
		return serviceInstance;
	}

	/**
	 * Method that retrieves authorization header for SuperUser (username:password) in Base64
	 * @return Authorization header for SuperUser
	 */
	public String getSuperUserAuthorizationHeader() {
		if (authorizationHeader == null) {
			String userAndPassword = null;
			try {
				userAndPassword = scheduledTasksSuperuserUsername + ":" + encryptionUtil.decrypt(scheduledTasksSuperuserPassword);
			} catch (Exception e) {
				log.error("Exception in getAuthorizationHeader", e);
			}

			assert userAndPassword != null;
			authorizationHeader = "Basic " + Base64Utils.encodeToString(userAndPassword.getBytes());
		}

		return authorizationHeader;
	}


}
