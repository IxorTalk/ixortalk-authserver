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
package com.ixortalk.authserver.config.locale;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.util.WebUtils;

/**
 * Angular cookie saved the locale with a double quote (%22en%22).
 * So the default CookieLocaleResolver#StringUtils.parseLocaleString(localePart)
 * is not able to parse the locale.
 *
 * This class will check if a double quote has been added, if so it will remove it.
 */
public class AngularCookieLocaleResolver extends CookieLocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        parseLocaleCookieIfNecessary(request);
        return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

    @Override
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        parseLocaleCookieIfNecessary(request);
        return new TimeZoneAwareLocaleContext() {
            @Override
            public Locale getLocale() {
                return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
            }

            @Override
            public TimeZone getTimeZone() {
                return (TimeZone) request.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
            }
        };
    }

    @Override
    public void addCookie(HttpServletResponse response, String cookieValue) {
        // Mandatory cookie modification for angular to support the locale switching on the server side.
        cookieValue = "%22" + cookieValue + "%22";
        super.addCookie(response, cookieValue);
    }

    private void parseLocaleCookieIfNecessary(HttpServletRequest request) {
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
            // Retrieve and parse cookie value.
            Cookie cookie = WebUtils.getCookie(request, getCookieName());
            Locale locale = null;
            TimeZone timeZone = null;
            if (cookie != null) {
                String value = cookie.getValue();

                // Remove the double quote
                value = StringUtils.replace(value, "%22", "");

                String localePart = value;
                String timeZonePart = null;
                int spaceIndex = localePart.indexOf(' ');
                if (spaceIndex != -1) {
                    localePart = value.substring(0, spaceIndex);
                    timeZonePart = value.substring(spaceIndex + 1);
                }
                locale = (!"-".equals(localePart) ? StringUtils.parseLocaleString(localePart.replace('-', '_')) : null);
                if (timeZonePart != null) {
                    timeZone = StringUtils.parseTimeZoneString(timeZonePart);
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Parsed cookie value [" + cookie.getValue() + "] into locale '" + locale +
                        "'" + (timeZone != null ? " and time zone '" + timeZone.getID() + "'" : ""));
                }
            }
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME,
                (locale != null ? locale: determineDefaultLocale(request)));

            request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
                (timeZone != null ? timeZone : determineDefaultTimeZone(request)));
        }
    }
}
