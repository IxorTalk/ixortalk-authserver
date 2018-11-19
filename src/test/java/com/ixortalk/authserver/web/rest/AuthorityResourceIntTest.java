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

import com.ixortalk.authserver.repository.AuthorityRepository;
import com.ixortalk.authserver.web.rest.dto.AuthorityDTO;
import org.junit.After;
import org.junit.Test;

import javax.inject.Inject;

import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthorityResourceIntTest extends AbstractSpringIntegrationTest {

    public static final String ROLE_TEST = "ROLE_TEST";

    @Inject
    private AuthorityRepository authorityRepository;

    @After
    public void after() {
        authorityRepository.findOneByName(ROLE_TEST).ifPresent(authority -> authorityRepository.delete(authority));
    }

    @Test
    public void normalUsersCannotRetrieveAuthorities() {
        given()
            .auth().oauth2(userToken().getValue())
            .accept(JSON)
            .contentType(JSON)
            .when()
            .get("/api/authorities")
            .then()
            .statusCode(HTTP_FORBIDDEN);
    }

    @Test
    public void adminUsersCanRetrieveAuthorities() {
        AuthorityDTO[] authorityDTOs =
            given()
                .auth().oauth2(adminToken().getValue())
                .accept(JSON)
                .contentType(JSON)
                .when()
                .get("/api/authorities")
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .response()
                .as(AuthorityDTO[].class);

        assertThat(authorityDTOs).hasSize(4).extracting(AuthorityDTO::getName).containsOnly("ROLE_ADMIN", "ROLE_USER", "ROLE_CUSTOM1", "ROLE_CUSTOM2");
    }

    @Test
    public void addAuthority_asUser() {
        given()
            .auth().oauth2(userToken().getValue())
            .accept(JSON)
            .contentType(JSON)
            .when()
            .body("{\"name\": \"" + ROLE_TEST + "\"}")
            .post("/api/authorities")
            .then()
            .statusCode(HTTP_FORBIDDEN);

        assertThat(authorityRepository.findOneByName(ROLE_TEST)).isNotPresent();
    }

    @Test
    public void addAuthority_asAdmin() {
        given()
            .auth().oauth2(adminToken().getValue())
            .accept(JSON)
            .contentType(JSON)
            .when()
            .body("{\"name\": \"" + ROLE_TEST + "\"}")
            .post("/api/authorities")
            .then()
            .statusCode(HTTP_CREATED);

        assertThat(authorityRepository.findOneByName(ROLE_TEST)).isPresent();
    }
}
