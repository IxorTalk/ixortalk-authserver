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

import org.junit.Test;

import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.CLIENT_ID_ADMIN;
import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.CLIENT_SECRET_ADMIN;
import static com.jayway.restassured.RestAssured.given;
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

public class OAuth2RestDocTest extends AbstractRestDocTest {

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
                .post("/oauth/token");
    }
}
