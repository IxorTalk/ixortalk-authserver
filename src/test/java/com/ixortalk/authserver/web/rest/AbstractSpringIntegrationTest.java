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

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.ixortalk.authserver.AuthserverApp;
import com.ixortalk.authserver.config.IxorTalkProperties;
import com.ixortalk.authserver.web.rest.dto.UserDTO;
import com.ixortalk.aws.s3.library.config.AwsS3Template;
import com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RedirectConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.google.common.collect.Sets.newHashSet;
import static com.ixortalk.authserver.domain.Authority.newAuthority;
import static com.ixortalk.authserver.domain.UserTestBuilder.aUser;
import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.CLIENT_ID_ADMIN;
import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.CLIENT_ID_USER;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.authorizationHeader;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static com.jayway.restassured.config.RestAssuredConfig.config;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AuthserverApp.class, OAuth2EmbeddedTestServer.class}, webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_CLASS)
@ActiveProfiles("test")
public class AbstractSpringIntegrationTest {

    @LocalServerPort
    private int port;

    @Value("${server.context-path}")
    protected String contextPath;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    protected IxorTalkProperties ixorTalkProperties;

    @Mock(answer = RETURNS_DEEP_STUBS)
    protected S3Object s3Object;

    @Rule
    public WireMockRule userInfoWireMockRule = new WireMockRule(65002);

    @Before
    public void setUpRestAssured() throws JsonProcessingException {
        RestAssured.port = port;
        RestAssured.basePath = contextPath;
        RestAssured.config =
            config()
                .objectMapperConfig(objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper))
                .redirect(RedirectConfig.redirectConfig().followRedirects(false));

        userInfoWireMockRule.stubFor(
            get(urlEqualTo("/user"))
                .withHeader(AUTHORIZATION, equalTo(authorizationHeader(adminToken())))
                .willReturn(okJson(objectMapper.writeValueAsString(new UserDTO(
                    aUser()
                        .withLogin(CLIENT_ID_ADMIN)
                        .withAuthorities(newHashSet(newAuthority().withName("ROLE_ADMIN").build()))
                        .build())))));

        userInfoWireMockRule.stubFor(
            get(urlEqualTo("/user"))
                .withHeader(AUTHORIZATION, equalTo(authorizationHeader(userToken())))
                .willReturn(okJson(objectMapper.writeValueAsString(
                    aUser()
                        .withLogin(CLIENT_ID_USER)
                        .withAuthorities(newHashSet(newAuthority().withName("ROLE_USER").build()))
                        .build()))));
    }

    protected void mockGetFromS3(AwsS3Template awsS3Template, String bucketName, String key, byte[] bytes, String contentType) {
        when(awsS3Template.get(bucketName, key)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(new ByteArrayInputStream(bytes), null));
        when(s3Object.getObjectMetadata().getContentType()).thenReturn(contentType);
    }

    protected void mockPutInS3OnlyExpectingBytes(AwsS3Template awsS3Template, byte[] expectedBytes) throws IOException {
        doThrow(new IllegalArgumentException("Not the expected bytes to save")).when(awsS3Template).save(any(), any(), argThat(multipartFile -> bytesNotEqual(multipartFile, expectedBytes)));
    }

    private boolean bytesNotEqual(MultipartFile multipartFile, byte[] photoBytes) {
        try {
            return !Arrays.equals(multipartFile.getBytes(), photoBytes);
        } catch (IOException e) {
            fail(e.getMessage());
            return false;
        }
    }

    public void verifySaveInS3(AwsS3Template awsS3Template, String bucketName, String key) throws IOException {
        ArgumentCaptor<String> profilePictureLinkCaptor = ArgumentCaptor.forClass(String.class);
        verify(awsS3Template).save(eq(bucketName), profilePictureLinkCaptor.capture(), shouldBeCheckedByWhenBecauseTempFileAlreadyDeletedAtTimeOfVerify());
        assertThat(profilePictureLinkCaptor.getValue()).isNotNull().isEqualTo(key);
    }

    private static MultipartFile shouldBeCheckedByWhenBecauseTempFileAlreadyDeletedAtTimeOfVerify() {
        return notNull();
    }
}
