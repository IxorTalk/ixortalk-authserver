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

import com.ixortalk.authserver.domain.Authority;
import com.ixortalk.authserver.service.UserService;
import com.ixortalk.authserver.web.rest.dto.ManagedUserDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.security.Principal;
import java.util.Set;

@RestController
public class UserEndpoint {

    @Inject
    private UserService userService;

    @RequestMapping("/user")
    public UserInfo user(Principal principal) {
        return userService.getUserWithAuthoritiesByLogin(principal.getName())
            .map(user ->
                new UserInfo(
                    user.getLogin(),
                    user.getAuthorities(),
                    new ManagedUserDTO(user, userService.constructProfilePictureUrl(user))))
            .orElseThrow(() -> new IllegalArgumentException("User not found!"));
    }
}

class UserInfo {

    private String name;
    private Set<Authority> authorities;
    private ManagedUserDTO userInfo;

    public UserInfo(String name, Set<Authority> authorities, ManagedUserDTO userInfo) {
        this.name = name;
        this.authorities = authorities;
        this.userInfo = userInfo;
    }

    public String getName() {
        return name;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public ManagedUserDTO getUserInfo() {
        return userInfo;
    }
}


