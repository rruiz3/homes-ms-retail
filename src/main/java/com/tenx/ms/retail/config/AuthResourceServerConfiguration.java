package com.tenx.ms.retail.config;


import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.config.ResourceServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
@Profile("!" + Profiles.TEST_NOAUTH)
@SuppressWarnings({ "PMD.SignatureDeclareThrowsException" })
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthResourceServerConfiguration extends ResourceServerConfiguration{

   @Override
   public void configure(HttpSecurity http) throws Exception {
      http.csrf().disable().authorizeRequests().antMatchers("/**").authenticated();
   }

   @Override
   public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
      resources.resourceId(null).tokenStore(tokenStore());
   }
}
