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

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ixortalk.authserver.AuthserverApp;
import com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer;
import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static com.jayway.restassured.config.RestAssuredConfig.config;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AuthserverApp.class, OAuth2EmbeddedTestServer.class}, webEnvironment = RANDOM_PORT)
public class AbstractRestAssuredIntTest {

    @LocalServerPort
    private int port;

    @Inject
    private ObjectMapper objectMapper;

    @Before
    public void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "/";
        RestAssured.config = config().objectMapperConfig(objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
    }
}
