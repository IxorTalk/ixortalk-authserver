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

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.ixortalk.authserver.web.rest.dto.ManagedUserDTO;

import java.security.Principal;

public class EnhancedPrincipal {

    private Principal principal;

    private UserInfo userInfo;

    private EnhancedPrincipal(Principal principal, UserInfo userInfo) {
        this.principal = principal;
        this.userInfo = userInfo;
    }

    public static EnhancedPrincipal enhancedPrincipal(Principal principal, ManagedUserDTO managedUserDTO, String profilePictureUrl) {
        return new EnhancedPrincipal(principal, new UserInfo(managedUserDTO, profilePictureUrl));
    }

    @JsonUnwrapped
    public Principal getPrincipal() {
        return principal;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}

class UserInfo {

    private ManagedUserDTO managedUserDTO;
    private String profilePictureUrl;

    public UserInfo(ManagedUserDTO managedUserDTO, String profilePictureUrl) {
        this.managedUserDTO = managedUserDTO;
        this.profilePictureUrl = profilePictureUrl;
    }

    @JsonUnwrapped
    public ManagedUserDTO getManagedUserDTO() {
        return managedUserDTO;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
