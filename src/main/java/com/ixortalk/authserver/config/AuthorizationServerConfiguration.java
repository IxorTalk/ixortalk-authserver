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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.ixortalk.authserver.config.ResourceServersConfiguration.ENABLE_RESOURCE_SERVER_ORDER;
import static java.util.stream.Collectors.toSet;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;
import static org.springframework.util.StringUtils.hasText;

@Configuration
@EnableAuthorizationServer
@Order(LOWEST_PRECEDENCE - 2)
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Inject
    private DataSource dataSource;

    @Inject
    private JHipsterProperties jHipsterProperties;

    @Inject
    private IxorTalkProperties ixorTalkProperties;

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Inject
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {

        endpoints
            .tokenStore(tokenStore())
            .authenticationManager(authenticationManager)
            // See https://github.com/spring-projects/spring-security-oauth/issues/140
            .addInterceptor(new HandlerInterceptorAdapter() {
                @Override
                public void postHandle(HttpServletRequest request,
                                       HttpServletResponse response, Object handler,
                                       ModelAndView modelAndView) throws Exception {
                    if (modelAndView != null
                        && modelAndView.getView() instanceof RedirectView) {
                        RedirectView redirect = (RedirectView) modelAndView.getView();
                        String url = redirect.getUrl();
                        if (url.contains("code=") || url.contains("error=")) {
                            HttpSession session = request.getSession(false);
                            if (session != null) {
                                session.invalidate();
                            }
                        }
                    }
                }
            });
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        if (ixorTalkProperties.getOauth().getClients().isUseJdbc()) {
            clients.jdbc(dataSource);
            cleanUpClientsFromConfiguration();
        } else {
            clients.inMemory();

        }
        jHipsterProperties.getSecurity().getAuthentication().getOauthClients().values()
            .stream()
            .forEach(client ->
                clients
                    .and()
                    .withClient(client.getClientid())
                    .scopes(client.getScopes().toArray(new String[]{}))
                    .authorities(client.getAuthorities().toArray(new String[]{}))
                    .authorizedGrantTypes(client.getAuthorizedGrantTypes().toArray(new String[]{}))
                    .autoApprove(client.getAutoApproveScopes().toArray(new String[]{}))
                    .secret(client.getSecret())
                    .accessTokenValiditySeconds(client.getTokenValidityInSeconds())
            );
    }

    public void cleanUpClientsFromConfiguration() {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        Set<String> existingClientIds = jdbcClientDetailsService.listClientDetails().stream().map(ClientDetails::getClientId).collect(toSet());
        jHipsterProperties.getSecurity().getAuthentication().getOauthClients().values()
            .stream()
            .filter(client -> existingClientIds.contains(client.getClientid()))
            .forEach(client -> jdbcClientDetailsService.removeClientDetails(client.getClientid()));
    }

    @Configuration
    @Order(ENABLE_RESOURCE_SERVER_ORDER - 2)
    protected static class LoginConfig extends WebSecurityConfigurerAdapter {

        @Value("${defaultSuccessUrl}")
        private String defaultSuccessUrl;

        @Value("${loginPage}")
        private String loginPage;

        @Inject
        private ManagementServerProperties managementServerProperties;

        @Inject
        private IxorTalkProperties ixorTalkProperties;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry =
                http
                    .formLogin()
                    .loginPage(loginPage)
                    .defaultSuccessUrl(defaultSuccessUrl)
                    .permitAll()
                    .and()
                    .requestMatchers()
                    .antMatchers(requestMatchers())
                    .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/signout"))
                    .logoutSuccessHandler(logoutSuccessHandler())
                    .and()
                    .authorizeRequests()
                    .antMatchers("/").permitAll();

            if (!managementServerProperties.getSecurity().isEnabled() && hasText(managementServerProperties.getContextPath())) {
                registry = registry.antMatchers(managementServerProperties.getContextPath() + "/**").permitAll();
            }

            registry
                .anyRequest()
                .authenticated();
            // @formatter:on
        }

        private LogoutSuccessHandler logoutSuccessHandler() {
            SimpleUrlLogoutSuccessHandler logoutSuccessHandler =  new SimpleUrlLogoutSuccessHandler();
            logoutSuccessHandler.setDefaultTargetUrl(ixorTalkProperties.getLogout().getDefaultRedirectUri());
            logoutSuccessHandler.setTargetUrlParameter(ixorTalkProperties.getLogout().getRedirectUriParamName());
            return  logoutSuccessHandler;
        }

        private String[] requestMatchers() {
            List<String> requestMatchers = newArrayList("/login", "/signout", "/reset", "/", "/oauth/authorize", "/oauth/confirm_access");
            if (!managementServerProperties.getSecurity().isEnabled() && hasText(managementServerProperties.getContextPath())) {
                requestMatchers.add(managementServerProperties.getContextPath() + "/**");
            }
            return requestMatchers.toArray(new String[0]);
        }

    }
}
