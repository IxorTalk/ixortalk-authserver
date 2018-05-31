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
package com.ixortalk.authserver.service.feign;

import java.util.Map;

public class SendMailVO {

    private String toEmail;
    private String languageTag;
    private String subjectI18NKey;
    private String templateName;
    private Map<String, Object> additionalVariables;

    public SendMailVO(String toEmail, String languageTag, String subjectI18NKey, String templateName, Map<String, Object> additionalVariables) {
        this.toEmail = toEmail;
        this.languageTag = languageTag;
        this.subjectI18NKey = subjectI18NKey;
        this.templateName = templateName;
        this.additionalVariables = additionalVariables;
    }

    public String getToEmail() {
        return toEmail;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public String getSubjectI18NKey() {
        return subjectI18NKey;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Map<String, Object> getAdditionalVariables() {
        return additionalVariables;
    }
}
