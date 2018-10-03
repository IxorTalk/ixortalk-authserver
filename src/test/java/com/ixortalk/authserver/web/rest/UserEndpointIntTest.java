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

import com.ixortalk.authserver.config.IxorTalkProperties;
import com.ixortalk.authserver.domain.User;
import com.ixortalk.authserver.repository.UserRepository;
import com.jayway.restassured.path.json.JsonPath;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.inject.Inject;

import java.util.Map;

import static com.ixortalk.test.oauth2.OAuth2TestTokens.getPasswordGrantAccessToken;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class UserEndpointIntTest extends AbstractSpringIntegrationTest {

    public static final String USER_ID = "user";

    @Inject
    private UserRepository userRepository;

    @Inject
    private IxorTalkProperties ixorTalkProperties;

    @Test
    public void getUser() {
        JsonPath jsonPath =
            given()
                .auth().oauth2(getPasswordGrantAccessToken(USER_ID, "user").getValue())
                .accept(JSON)
                .when()
                .get("/user")
                .then()
                .statusCode(HTTP_OK)
                .extract().jsonPath();

        assertThat(jsonPath.getObject(".", Map.class).keySet()).containsOnly("name", "authorities", "userInfo");
        assertThat(jsonPath.getString("name")).isEqualTo(USER_ID);
    }

    @Test
    public void getUser_noProfilePictureKey() {
        User user = userRepository.findOneByLogin(USER_ID).get();
        ReflectionTestUtils.setField(user, "profilePictureKey", null);
        userRepository.save(user);

        JsonPath jsonPath =
            given()
                .auth().oauth2(getPasswordGrantAccessToken(USER_ID, "user").getValue())
                .accept(JSON)
                .when()
                .get("/user")
                .then()
                .statusCode(HTTP_OK)
                .extract().jsonPath();

        assertThat(jsonPath.getString("userInfo.profilePictureUrl")).isNull();
    }

    @Test
    public void getUser_withProfilePictureKey() {
        User user = userRepository.findOneByLogin(USER_ID).get();
        user.generateProfilePictureKey();
        userRepository.save(user);

        JsonPath jsonPath =
            given()
                .auth().oauth2(getPasswordGrantAccessToken(USER_ID, "user").getValue())
                .accept(JSON)
                .when()
                .get("/user")
                .then()
                .statusCode(HTTP_OK)
                .extract().jsonPath();

        assertThat(jsonPath.getString("userInfo.profilePictureUrl")).isEqualTo(ixorTalkProperties.getLoadbalancer().getExternal().getUrlWithoutStandardPorts() + ixorTalkProperties.getMicroservice("authserver").getContextPath() + "/api/profile-pictures/" + user.getProfilePictureKey());
    }
}
