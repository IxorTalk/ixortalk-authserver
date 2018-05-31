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
package com.ixortalk.authserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.inject.Inject;

@Configuration
public class ThymeleafConfiguration {

    private final Logger log = LoggerFactory.getLogger(ThymeleafConfiguration.class);

    @Inject
    private JHipsterProperties jHipsterProperties;

    @Bean
    @Description("Thymeleaf template resolver serving HTML 5 emails")
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix(jHipsterProperties.getMail().getTemplatePrefix());
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode("HTML5");
        emailTemplateResolver.setCharacterEncoding(org.apache.commons.lang3.CharEncoding.UTF_8);
        emailTemplateResolver.setOrder(1);
        return emailTemplateResolver;
    }

    @Bean
    @Description("Thymeleaf template resolver serving HTML 5 emails")
    public FileTemplateResolver fileTemplateResolver() {
        FileTemplateResolver emailTemplateResolver = new FileTemplateResolver();
        emailTemplateResolver.setPrefix(jHipsterProperties.getMail().getTemplatePrefix());
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode("HTML5");
        emailTemplateResolver.setCharacterEncoding(org.apache.commons.lang3.CharEncoding.UTF_8);
        emailTemplateResolver.setOrder(1);
        return emailTemplateResolver;
    }

    @Bean
    public MessageSource messageSource(Environment environment) {
        PropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "spring.messages.");
        String baseName = propertyResolver.getProperty("basename");
        final ReloadableResourceBundleMessageSource messageSource;
        messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasename(baseName);
        return messageSource;
    }
}
