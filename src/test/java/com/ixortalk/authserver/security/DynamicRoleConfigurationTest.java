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
package com.ixortalk.authserver.security;


import java.util.List;

import javax.inject.Inject;

import com.ixortalk.authserver.AuthserverApp;
import com.ixortalk.authserver.domain.Authority;
import com.ixortalk.authserver.repository.AuthorityRepository;
import com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AuthserverApp.class, OAuth2EmbeddedTestServer.class}, webEnvironment = RANDOM_PORT)
public class DynamicRoleConfigurationTest {

    @Inject
    private AuthorityRepository authorityRepository;

    @Test
    public void dynamicRolesArePersistedProperly() {
        List<Authority> authorities = authorityRepository.findAll();

        assertThat(authorities)
            .hasSize(4)
            .extracting(Authority::getName)
            .containsOnly("ROLE_ADMIN", "ROLE_USER", "ROLE_CUSTOM1", "ROLE_CUSTOM2");
    }
}
