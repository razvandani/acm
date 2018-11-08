package com.gcr.acm.iam;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.gcr.acm.common.auth.AuthenticationInterceptor;
import com.gcr.acm.common.logging.LoggerAspect;
import com.gcr.acm.common.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication(scanBasePackages = "com.gcr.acm")
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableSwagger2
@EntityScan(value = "com.gcr.acm")
public class IdentityAccessManagementApplication extends WebMvcConfigurerAdapter {
    @Autowired
    private PasswordEncoder standardPasswordEncoder;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public static void main(String[] args) {
        LoggerAspect.initializeLogging();

        SpringApplication.run(IdentityAccessManagementApplication.class, args);
    }

    @Bean
    public DataSource dataSource()
            throws
            IOException,
            IllegalBlockSizeException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException {
        Properties dsProps = PropertiesLoaderUtils.loadAllProperties("datasource-" + activeProfile + ".properties");
        dsProps.setProperty("password", encryptionUtil.decrypt(dsProps.getProperty("password")));
        Properties hikariProps = PropertiesLoaderUtils.loadAllProperties("hikari-" + activeProfile + ".properties");
        hikariProps.put("dataSourceProperties", dsProps);
        return new HikariDataSource(new HikariConfig(hikariProps));
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory)
            throws
            IOException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            NoSuchPaddingException {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        tm.setDataSource(dataSource());
        return tm;
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public StandardPasswordEncoder standardPasswordEncoder() {
        return new StandardPasswordEncoder();
    }

    @Bean
    @Profile(value = {"local", "test"})
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.gcr.acm.iam"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    @Profile("production")
    public Docket blockedApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(false);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**");
    }
}
