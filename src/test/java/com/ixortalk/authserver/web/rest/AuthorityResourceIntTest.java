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

import com.ixortalk.authserver.web.rest.dto.AuthorityDTO;
import org.junit.Test;

import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class AuthorityResourceIntTest extends AbstractSpringIntegrationTest {

    @Test
    public void normalUsersCannotRetrieveAuthorities() throws Exception {
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
    public void adminUsersCanRetrieveAuthorities() throws Exception {
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
}
