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

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.TestPropertySource;

import static com.ixortalk.authserver.restdocs.AbstractRestDocTest.AUTHORIZATION_TOKEN_HEADER;
import static com.ixortalk.authserver.restdocs.AbstractRestDocTest.staticUris;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.jayway.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@TestPropertySource(properties = {
    "ixortalk.logout.default-redirect-uri: someLogin",
    "ixortalk.logout.redirect-uri-param-name: aRedirectUri"
})
public class LogoutWithRedirectParameter_IntegrationAndRestDocTest extends AbstractSpringIntegrationTest {

    @Value("${ixortalk.logout.default-redirect-uri}")
    private String login;

    @Value("${ixortalk.logout.redirect-uri-param-name}")
    private String redirectUriParam;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Test
    public void logout_withRedirectParam() {
        RequestSpecification spec = new RequestSpecBuilder()
            .addFilter(documentationConfiguration(this.restDocumentation))
            .build();

        String location =
            given(spec)
                .auth().preemptive().oauth2(adminToken().getValue())
                .when()
                .filter(
                    document("logout-with-redirect/ok",
                        preprocessRequest(staticUris(), prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(AUTHORIZATION_TOKEN_HEADER),
                        requestParameters(
                            parameterWithName(redirectUriParam).description("After a successful logout, the header will be redirected to the specified location.")
                        )
                    )
                )
                .param(redirectUriParam, "foo://bar/redirect")
                .get("/signout")
                .then()
                .statusCode(HTTP_MOVED_TEMP)
                .extract().header("Location");

        assertThat(location).isEqualTo("foo://bar/redirect");
    }

    @Test
    public void logout_withEmptyRedirectParam() {
        String location =
            given()
                .auth().preemptive().oauth2(adminToken().getValue())
                .when()
                .param(redirectUriParam, "")
                .get("/signout")
                .then()
                .statusCode(HTTP_MOVED_TEMP)
                .extract().header("Location");

        assertThat(location).endsWith("/uaa/"+login);
    }

    @Test
    public void logout_withNullRedirectParam() {
        String location =
            given()
                .auth().preemptive().oauth2(adminToken().getValue())
                .when()
                .param(redirectUriParam)
                .get("/signout")
                .then()
                .statusCode(HTTP_MOVED_TEMP)
                .extract().header("Location");

        assertThat(location).endsWith("/uaa/"+login);
    }
}


