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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.List;

@Configuration
public class MockOAuth2ContextConfig {

    @Bean
    public OAuth2ClientContext oAuth2ClientContext() {
        return new OAuth2ClientContext() {
            @Override
            public OAuth2AccessToken getAccessToken() {
                return null;
            }

            @Override
            public void setAccessToken(OAuth2AccessToken accessToken) {

            }

            @Override
            public AccessTokenRequest getAccessTokenRequest() {
                return null;
            }

            @Override
            public void setPreservedState(String stateKey, Object preservedState) {

            }

            @Override
            public Object removePreservedState(String stateKey) {
                return null;
            }
        };
    }

    @Bean
    public OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails() {
        return new OAuth2ProtectedResourceDetails() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getClientId() {
                return null;
            }

            @Override
            public String getAccessTokenUri() {
                return null;
            }

            @Override
            public boolean isScoped() {
                return false;
            }

            @Override
            public List<String> getScope() {
                return null;
            }

            @Override
            public boolean isAuthenticationRequired() {
                return false;
            }

            @Override
            public String getClientSecret() {
                return null;
            }

            @Override
            public AuthenticationScheme getClientAuthenticationScheme() {
                return null;
            }

            @Override
            public String getGrantType() {
                return null;
            }

            @Override
            public AuthenticationScheme getAuthenticationScheme() {
                return null;
            }

            @Override
            public String getTokenName() {
                return null;
            }

            @Override
            public boolean isClientOnly() {
                return false;
            }
        };
    }
}
