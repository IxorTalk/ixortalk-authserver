/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.authserver.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.DefaultUserInfoRestTemplateFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration
@EnableResourceServer
public class ResourceServersConfiguration {

    static final int ENABLE_RESOURCE_SERVER_ORDER = 3;

    @Bean
    protected ResourceServerConfiguration ixorTalkAuthServerResources() {

        ResourceServerConfiguration resource = new ResourceServerConfiguration() {
            // Switch off the Spring Boot @Autowired configurers
            public void setConfigurers(List<ResourceServerConfigurer> configurers) {
                super.setConfigurers(configurers);
            }
        };

        resource.setConfigurers(newArrayList(new ResourceServerConfigurerAdapter() {

            @Override
            public void configure(ResourceServerSecurityConfigurer resources) {
                resources.resourceId("ixortalk-authserver");
            }

            @Override
            public void configure(HttpSecurity http) throws Exception {
                http.antMatcher("/user").authorizeRequests().anyRequest().authenticated();
            }
        }));

        resource.setOrder(HIGHEST_PRECEDENCE);

        return resource;
    }

    @Bean
    protected ResourceServerConfiguration ixorTalkAuthorizationServerResources(ResourceServerProperties sso,
                                                                               UserInfoRestTemplateFactory restTemplateFactory,
                                                                               ObjectProvider<AuthoritiesExtractor> authoritiesExtractor,
                                                                               ObjectProvider<PrincipalExtractor> principalExtractor) {

        ResourceServerConfiguration resource = new ResourceServerConfiguration() {
            // Switch off the Spring Boot @Autowired configurers
            public void setConfigurers(List<ResourceServerConfigurer> configurers) {
                super.setConfigurers(configurers);
            }
        };

        resource.setConfigurers(newArrayList(new AuthorizationServerResourceServerConfigurerAdapter(sso, restTemplateFactory, authoritiesExtractor, principalExtractor)));
        resource.setOrder(ENABLE_RESOURCE_SERVER_ORDER - 1);

        return resource;

    }

    private static class AuthorizationServerResourceServerConfigurerAdapter extends ResourceServerConfigurerAdapter {

        private final ResourceServerProperties sso;

        private final OAuth2RestOperations restTemplate;

        private final AuthoritiesExtractor authoritiesExtractor;

        private final PrincipalExtractor principalExtractor;

        AuthorizationServerResourceServerConfigurerAdapter(ResourceServerProperties sso,
                                                           UserInfoRestTemplateFactory restTemplateFactory,
                                                           ObjectProvider<AuthoritiesExtractor> authoritiesExtractor,
                                                           ObjectProvider<PrincipalExtractor> principalExtractor) {
            this.sso = sso;
            this.restTemplate = restTemplateFactory.getUserInfoRestTemplate();
            this.authoritiesExtractor = authoritiesExtractor.getIfAvailable();
            this.principalExtractor = principalExtractor.getIfAvailable();
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId("ixortalk-authorization-server");
            resources.tokenServices(userInfoTokenServices());
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated();
        }

        UserInfoTokenServices userInfoTokenServices() {
            UserInfoTokenServices services = new UserInfoTokenServices(
                this.sso.getUserInfoUri(), this.sso.getClientId());
            services.setRestTemplate(this.restTemplate);
            services.setTokenType(this.sso.getTokenType());
            if (this.authoritiesExtractor != null) {
                services.setAuthoritiesExtractor(this.authoritiesExtractor);
            }
            if (this.principalExtractor != null) {
                services.setPrincipalExtractor(this.principalExtractor);
            }
            return services;
        }
    }

    @Bean
    public UserInfoRestTemplateFactory userInfoRestTemplateFactory(
        ObjectProvider<List<UserInfoRestTemplateCustomizer>> customizers,
        ObjectProvider<OAuth2ProtectedResourceDetails> details,
        ObjectProvider<OAuth2ClientContext> oauth2ClientContext) {
        return new DefaultUserInfoRestTemplateFactory(customizers, details, oauth2ClientContext);
    }
}
