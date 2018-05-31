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
package com.ixortalk.authserver.restdocs;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ixortalk.authserver.AuthserverApp;
import com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.restassured.operation.preprocess.UriModifyingOperationPreprocessor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.CLIENT_ID_ADMIN;
import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.CLIENT_SECRET_ADMIN;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static com.jayway.restassured.config.RestAssuredConfig.config;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured.operation.preprocess.RestAssuredPreprocessors.modifyUris;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AuthserverApp.class, OAuth2EmbeddedTestServer.class}, webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_CLASS)
@ActiveProfiles("test")
public class OAuth2RestDocTest {

    @LocalServerPort
    private int port;

    @Value("${server.context-path}")
    protected String contextPath;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    protected RequestSpecification spec;

    @Inject
    protected ObjectMapper objectMapper;

    @Before
    public void initializeRestDocsSpec() {
        RestAssured.port = port;
        RestAssured.basePath = contextPath;
        RestAssured.config = config().objectMapperConfig(objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));

        spec = new RequestSpecBuilder()
            .addFilter(documentationConfiguration(this.restDocumentation))
            .build();
    }

    protected static UriModifyingOperationPreprocessor staticUris() {
        return modifyUris().scheme("https").host("instance.ixortalk.com").removePort();
    }

    @Test
    public void retrievingAccessToken() {
        given(this.spec)
                .parameters("grant_type", "client_credentials")
                .auth()
                .preemptive()
                .basic(CLIENT_ID_ADMIN, CLIENT_SECRET_ADMIN)
                .filter(
                        document("retrieveAccessToken",
                                preprocessRequest(prettyPrint(), staticUris()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName("Authorization").description("Basic auth credentials")
                                ),
                                requestParameters(
                                        parameterWithName("grant_type").description("The type of grant. In this case client_credentials")
                                ),
                                responseFields(
                                        fieldWithPath("access_token").type(STRING).description("The access token needed to access the assetmgmt API"),
                                        fieldWithPath("token_type").type(STRING).description("bearer token"),
                                        fieldWithPath("expires_in").type(NUMBER).description("The expiration time in seconds for the token"),
                                        fieldWithPath("scope").type(STRING).description("The scope associated with the token")
                                )
                        )
                )
                .when()
                .post("/oauth/token")
                .as(OAuth2AccessToken.class);
    }

}
