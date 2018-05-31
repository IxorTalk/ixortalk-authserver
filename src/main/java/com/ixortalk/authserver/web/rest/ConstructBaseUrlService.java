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
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import static com.ixortalk.authserver.config.IxorTalkProperties.Loadbalancer.isDefaultPort;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Named
@EnableConfigurationProperties(IxorTalkProperties.class)
public class ConstructBaseUrlService {

    @Inject
    private IxorTalkProperties ixorTalkProperties;

    public String constructBaseUrl(HttpServletRequest request) {
        if (!isEmpty(ixorTalkProperties.getLoadbalancer().getExternal().getUrl())) {
            return ixorTalkProperties.getLoadbalancer().getExternal().getUrlWithoutStandardPorts() + ixorTalkProperties.getMicroservice("authserver").getContextPath();
        }
        StringBuilder baseUrlBuilder =
            new StringBuilder()
                .append(request.getScheme())
                .append("://")
                .append(request.getServerName());
        if (!isDefaultPort(request.getScheme(), request.getServerPort())) {
            baseUrlBuilder
                .append(":")
                .append(request.getServerPort());
        }
        baseUrlBuilder.append(request.getContextPath());
        return baseUrlBuilder.toString();
    }
}
