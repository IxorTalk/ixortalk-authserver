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

import java.util.Set;

import javax.inject.Inject;
import javax.sql.DataSource;

import com.ixortalk.test.util.Randomizer;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import static com.google.common.collect.Sets.newHashSet;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.ixortalk.test.util.Randomizer.nextString;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

public class ClientResourceIntTest extends AbstractSpringIntegrationTest {

    public static final String THE_NEW_CLIENTS_ID = "theNewClientsId";
    public static final Set<String> ROLES = newHashSet("ROLE_A", "ROLE_B");

    @Inject
    private DataSource dataSource;

    private JdbcClientDetailsService jdbcClientDetailsService;

    @Before
    public void before() {
        jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
    }

    @Test
    public void addClient() throws Exception {
        ClientDetails response =
            given()
                .auth().preemptive().oauth2(adminToken().getValue())
                .accept(JSON)
                .contentType(JSON)
                .request()
                .body(
                    new JSONObject()
                        .put("clientId", THE_NEW_CLIENTS_ID)
                        .put("roles", ROLES.stream().collect(joining(",")))
                        .toString()
                )
                .when()
                .post("/api/clients")
                .then()
                .statusCode(HTTP_OK)
                .extract().as(BaseClientDetails.class);

        ClientDetails clientDetails = jdbcClientDetailsService.loadClientByClientId(THE_NEW_CLIENTS_ID);

        assertThat(clientDetails).isEqualToComparingFieldByField(response);
        assertThat(clientDetails.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsOnlyElementsOf(ROLES);
    }

    @Test
    public void nonAdminUsersCannotAddClients() throws Exception {
        given()
            .auth().preemptive().oauth2(userToken().getValue())
            .accept(JSON)
            .contentType(JSON)
            .request()
            .body(
                new JSONObject()
                    .put("clientId", THE_NEW_CLIENTS_ID)
                    .put("roles", ROLES.stream().collect(joining(",")))
                    .toString()
            )
            .when()
            .post("/api/clients")
            .then()
            .statusCode(HTTP_FORBIDDEN);
    }

    @Test
    public void deleteClient() {
        ClientDetails oAuth2Client = setupOAuth2Client();
        jdbcClientDetailsService.addClientDetails(oAuth2Client);

        given()
            .auth().preemptive().oauth2(adminToken().getValue())
            .request()
            .when()
            .delete("/api/clients/{clientId}", oAuth2Client.getClientId())
            .then()
            .statusCode(HTTP_NO_CONTENT);

        assertThat(jdbcClientDetailsService.listClientDetails().stream().noneMatch(clientDetails -> clientDetails.getClientId().equals(oAuth2Client.getClientId()))).isTrue();
    }

    @Test
    public void nonAdminUsersCannotDeleteClients() {
        ClientDetails oAuth2Client = setupOAuth2Client();
        jdbcClientDetailsService.addClientDetails(oAuth2Client);

        given()
            .auth().preemptive().oauth2(userToken() .getValue())
            .request()
            .when()
            .delete("/api/clients/{clientId}", oAuth2Client.getClientId())
            .then()
            .statusCode(HTTP_FORBIDDEN);

        assertThat(jdbcClientDetailsService.loadClientByClientId(oAuth2Client.getClientId())).isNotNull();
    }

    private BaseClientDetails setupOAuth2Client() {
        return new BaseClientDetails(nextString("oauth2ClientId-"), null, nextString("theScopes-"), nextString("theGrantTypes-"), nextString("theAuthorities-"));
    }
}
