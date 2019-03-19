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

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.jayway.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {
    "ixortalk.logout.default-redirect-uri: someLogin"
})
public class LogoutWithoutParameter_IntegrationTest extends AbstractSpringIntegrationTest {

    @Value("${ixortalk.logout.default-redirect-uri}")
    private String login;

    @Test
    public void logout_withoutRedirectParam() {
        String location =
            given()
                .auth().preemptive().oauth2(adminToken().getValue())
                .when()
                .get("/signout")
                .then()
                .statusCode(HTTP_MOVED_TEMP)
                .extract().header("Location");

        assertThat(location).endsWith("/uaa/"+login);
    }
}


