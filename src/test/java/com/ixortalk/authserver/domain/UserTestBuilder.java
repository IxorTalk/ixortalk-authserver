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
package com.ixortalk.authserver.domain;

import java.time.ZonedDateTime;
import java.util.Set;

import static com.google.common.base.Strings.padStart;
import static com.google.common.collect.Sets.newHashSet;
import static com.ixortalk.test.util.Randomizer.nextString;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class UserTestBuilder {

    private String login = nextString("user-");
    private String password = padStart(nextString("password-"), 60, ' ');
    private String firstName;
    private String lastName;
    private String email = nextString("user@domain.com");
    private boolean activated = true;
    private String langKey;
    private String activationKey;
    private String resetKey;
    private ZonedDateTime resetDate = null;
    private Set<Authority> authorities = newHashSet();
    private String profilePictureKey = null;

    private UserTestBuilder() {}

    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }

    public User build() {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setActivated(activated);
        user.setLangKey(langKey);
        user.setActivationKey(activationKey);
        user.setResetKey(resetKey);
        user.setResetDate(resetDate);
        user.setAuthorities(authorities);
        setField(user, "profilePictureKey", profilePictureKey);
        return user;
    }

    public UserTestBuilder withLogin(String login) {
        this.login = login;
        return this;
    }

    public UserTestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserTestBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder withActivated(boolean activated) {
        this.activated = activated;
        return this;
    }

    public UserTestBuilder withLangKey(String langKey) {
        this.langKey = langKey;
        return this;
    }

    public UserTestBuilder withActivationKey(String activationKey) {
        this.activationKey = activationKey;
        return this;
    }

    public UserTestBuilder withResetKey(String resetKey) {
        this.resetKey = resetKey;
        return this;
    }

    public UserTestBuilder withResetDate(ZonedDateTime resetDate) {
        this.resetDate = resetDate;
        return this;
    }

    public UserTestBuilder withAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
        return this;
    }

    public UserTestBuilder withProfilePictureKey(String profilePictureKey) {
        this.profilePictureKey = profilePictureKey;
        return this;
    }
}
