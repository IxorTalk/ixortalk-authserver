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

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ixortalk.authserver.AuthserverApp;
import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.ixortalk.test.oauth2.OAuth2TestTokens.getAccessToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static com.jayway.restassured.config.RestAssuredConfig.config;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthserverApp.class, webEnvironment = RANDOM_PORT)
public class OAuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Inject
    private ObjectMapper objectMapper;

    @Before
    public void before() {
        RestAssured.port = port;
        RestAssured.basePath = "/";
        RestAssured.config = config().objectMapperConfig(objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
    }

    @Test
    public void adminClientId_AdminSecured() {
        given()
            .auth().oauth2(adminToken())
            .accept(JSON)
            .contentType(JSON)
            .when()
            .get("/test-resource/adminSecured")
            .then()
            .statusCode(HTTP_OK);
    }

    @Test
    public void userClientId_AdminSecured() {
        given()
            .auth().oauth2(userToken())
            .accept(JSON)
            .contentType(JSON)
            .when()
            .get("/test-resource/adminSecured")
            .then()
            .statusCode(HTTP_FORBIDDEN);
    }

    @Test
    public void adminClientId_UserSecured() {
        given()
            .auth().oauth2(adminToken())
            .accept(JSON)
            .contentType(JSON)
            .when()
            .get("/test-resource/userSecured")
            .then()
            .statusCode(HTTP_OK);
    }

    @Test
    public void userClientId_UserSecured() {
        given()
            .auth().oauth2(userToken())
            .accept(JSON)
            .contentType(JSON)
            .when()
            .get("/test-resource/userSecured")
            .then()
            .statusCode(HTTP_OK);
    }

    private static String adminToken() {
        return getAccessToken("testAdminClientId", "testAdminClientSecret").getValue();
    }

    private static String userToken() {
        return getAccessToken("testUserClientId", "testUserClientSecret").getValue();
    }
}
