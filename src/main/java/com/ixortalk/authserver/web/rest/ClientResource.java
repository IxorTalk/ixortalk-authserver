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
package com.ixortalk.authserver.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;

import com.ixortalk.authserver.web.rest.dto.CreateClientDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.collect.Lists.newArrayList;
import static com.ixortalk.authserver.security.AuthoritiesConstants.ADMIN;
import static com.ixortalk.authserver.service.util.RandomUtil.generatePassword;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api")
public class ClientResource {

    private static final List<String> SCOPES = newArrayList("openid", "read", "write");

    @Inject
    private DataSource dataSource;

    private JdbcClientDetailsService jdbcClientDetailsService;

    @PostConstruct
    public void postConstruct() {
        jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
    }

    @RequestMapping(value = "/clients", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Secured(ADMIN)
    public ResponseEntity<ClientDetails> add(@RequestBody CreateClientDTO createClientDTO) {
        BaseClientDetails clientDetails = new BaseClientDetails(createClientDTO.getClientId(), null, SCOPES.stream().collect(joining(",")), "password,refresh_token,authorization_code,implicit,client_credentials", createClientDTO.getRoles());
        clientDetails.setClientSecret(generatePassword());
        clientDetails.setAccessTokenValiditySeconds(86400);
        clientDetails.setAutoApproveScopes(SCOPES);
        jdbcClientDetailsService.addClientDetails(clientDetails);
        return ok(clientDetails);
    }

    @RequestMapping(value = "/clients/{clientId}", method = DELETE)
    @Secured(ADMIN)
    public ResponseEntity<ClientDetails> delete(@PathVariable String clientId) {
        jdbcClientDetailsService.removeClientDetails(clientId);
        return noContent().build();
    }
}
