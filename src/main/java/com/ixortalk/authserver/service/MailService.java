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
package com.ixortalk.authserver.service;

import com.ixortalk.authserver.domain.User;
import com.ixortalk.authserver.service.feign.MailingService;
import com.ixortalk.authserver.service.feign.SendMailVO;
import com.ixortalk.authserver.web.rest.ConstructBaseUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.newHashMap;

@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";
    private static final String PLATFORM_URL = "platformUrl";
    private static final String AUTHSERVER_URL = "authserverUrl";

    @Inject
    private MailingService mailingService;

    @Inject
    private ConstructBaseUrlService constructBaseUrlService;

    @Async
    public void sendActivationEmail(User user, Optional<String> specifiedBaseUrl) {
        log.debug("Sending activation e-mail to '{}'", user.getEmail());

        Map<String, Object> additionalVariables = newHashMap();
        additionalVariables.put(USER, user);
        additionalVariables.put(PLATFORM_URL, constructBaseUrlService.constructPlatformUrl());
        additionalVariables.put(AUTHSERVER_URL, specifiedBaseUrl.orElse(constructBaseUrlService.constructAuthServerUrl()));

        mailingService.send(new SendMailVO(
            user.getEmail(),
            user.getLangKey(),
            "email.activation.title",
            "activationEmail",
            additionalVariables
        ));
    }

    @Async
    public void sendCreationEmail(User user, Optional<String> specifiedBaseUrl) {
        log.debug("Sending creation e-mail to '{}'", user.getEmail());

        Map<String, Object> additionalVariables = newHashMap();
        additionalVariables.put(USER, user);
        additionalVariables.put(PLATFORM_URL, constructBaseUrlService.constructPlatformUrl());
        additionalVariables.put(AUTHSERVER_URL, specifiedBaseUrl.orElse(constructBaseUrlService.constructAuthServerUrl()));

        mailingService.send(new SendMailVO(
            user.getEmail(),
            user.getLangKey(),
            "email.activation.title",
            "creationEmail",
            additionalVariables
        ));
    }

    @Async
    public void sendPasswordResetMail(User user) {
        log.debug("Sending password reset e-mail to '{}'", user.getEmail());

        Map<String, Object> additionalVariables = newHashMap();
        additionalVariables.put(USER, user);
        additionalVariables.put(PLATFORM_URL, constructBaseUrlService.constructPlatformUrl());
        additionalVariables.put(AUTHSERVER_URL, constructBaseUrlService.constructAuthServerUrl());

        mailingService.send(new SendMailVO(
            user.getEmail(),
            user.getLangKey(),
            "email.reset.title",
            "passwordResetEmail",
            additionalVariables
        ));
    }

}
