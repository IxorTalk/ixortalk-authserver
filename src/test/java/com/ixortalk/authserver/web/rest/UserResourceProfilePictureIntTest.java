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

import java.io.IOException;

import javax.inject.Inject;

import com.ixortalk.authserver.domain.User;
import com.ixortalk.authserver.repository.UserRepository;
import com.ixortalk.aws.s3.library.config.AwsS3Template;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static com.ixortalk.authserver.domain.UserTestBuilder.aUser;
import static com.ixortalk.test.oauth2.OAuth2EmbeddedTestServer.CLIENT_ID_USER;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.adminToken;
import static com.ixortalk.test.oauth2.OAuth2TestTokens.userToken;
import static com.ixortalk.test.util.Randomizer.nextString;
import static com.jayway.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@TestPropertySource(properties = {"com.ixortalk.s3.default-bucket: default-bucket-test-config"})
public class UserResourceProfilePictureIntTest extends AbstractSpringIntegrationTest {

    public static final byte[] BINARY_CONTENT = nextString("binaryContent").getBytes();
    public static final String PHOTO_CONTENT_TYPE = nextString("photocontent/type");

    @MockBean
    protected AwsS3Template awsS3Template;

    @Inject
    private UserRepository userRepository;

    private User user;

    @Before
    public void before() {
        user = userRepository.save(aUser().build());
    }

    @After
    public void after() {
        userRepository.delete(user);
        userRepository.findOneByLogin(CLIENT_ID_USER).ifPresent(user -> userRepository.delete(user));
    }

    @Test
    public void getProfilePicture() {
        mockGetFromS3(awsS3Template, user.getProfilePictureKey(), BINARY_CONTENT, PHOTO_CONTENT_TYPE);

        byte[] binaryResponse =
            given()
                .auth().preemptive().oauth2(adminToken().getValue())
                .when()
                .get("/api/users/{login}/profile-picture", user.getLogin())
                .then()
                .statusCode(HTTP_OK)
                .header(CONTENT_TYPE, PHOTO_CONTENT_TYPE)
                .extract().response().asByteArray();

        assertThat(binaryResponse).isEqualTo(BINARY_CONTENT);
    }

    @Test
    public void getProfilePicture_AsUser_FromOtherUser() {
        mockGetFromS3(awsS3Template, user.getProfilePictureKey(), BINARY_CONTENT, PHOTO_CONTENT_TYPE);

        byte[] binaryResponse =
            given()
                .auth().preemptive().oauth2(userToken().getValue())
                .when()
                .get("/api/users/{login}/profile-picture", user.getLogin())
                .then()
                .statusCode(HTTP_OK)
                .header(CONTENT_TYPE, PHOTO_CONTENT_TYPE)
                .extract().response().asByteArray();

        assertThat(binaryResponse).isEqualTo(BINARY_CONTENT);
    }

    @Test
    public void getProfilePicture_notFound() {
        given()
            .auth().preemptive().oauth2(adminToken().getValue())
            .when()
            .get("/api/users/{login}/profile-picture", "nonExisting")
            .then()
            .statusCode(HTTP_NOT_FOUND);
    }

    @Test
    public void getProfilePicture_noProfilePictureKey() {
        doThrow(new IllegalArgumentException("Key cannot be null")).when(awsS3Template).get(null);

        given()
            .auth().preemptive().oauth2(adminToken().getValue())
            .when()
            .get("/api/users/{login}/profile-picture", user.getLogin())
            .then()
            .statusCode(HTTP_NOT_FOUND);
    }

    @Test
    public void setProfilePicture_asAdmin() throws IOException {
        mockPutInS3OnlyExpectingBytes(awsS3Template, BINARY_CONTENT);

        given()
            .auth().preemptive().oauth2(adminToken().getValue())
            .contentType(MULTIPART_FORM_DATA_VALUE)
            .multiPart("file", "file", BINARY_CONTENT)
            .when()
            .post("/api/users/{login}/profile-picture", user.getLogin())
            .then()
            .statusCode(HTTP_OK);

        verifySaveInS3(awsS3Template, userRepository.findOneByLogin(user.getLogin()).map(User::getProfilePictureKey).orElseThrow(() -> new IllegalStateException("User " + user.getLogin() + " should exist at this point!")));

    }

    @Test
    public void setProfilePicture_asUser_WithAccess() throws IOException {
        user = userRepository.save(aUser().withLogin(CLIENT_ID_USER).build());

        mockPutInS3OnlyExpectingBytes(awsS3Template, BINARY_CONTENT);

        given()
            .auth().preemptive().oauth2(userToken().getValue())
            .contentType(MULTIPART_FORM_DATA_VALUE)
            .multiPart("file", "file", BINARY_CONTENT)
            .when()
            .post("/api/users/{login}/profile-picture", user.getLogin())
            .then()
            .statusCode(HTTP_OK);

        verifySaveInS3(awsS3Template, userRepository.findOneByLogin(user.getLogin()).map(User::getProfilePictureKey).orElseThrow(() -> new IllegalStateException("User " + user.getLogin() + " should exist at this point!")));
    }

    @Test
    public void setProfilePicture_asUser_NoAccess() {
        given()
            .auth().preemptive().oauth2(userToken().getValue())
            .contentType(MULTIPART_FORM_DATA_VALUE)
            .multiPart("file", "file", BINARY_CONTENT)
            .when()
            .post("/api/users/{login}/profile-picture", user.getLogin())
            .then()
            .statusCode(HTTP_FORBIDDEN);

        verifyZeroInteractions(awsS3Template);
    }
}
