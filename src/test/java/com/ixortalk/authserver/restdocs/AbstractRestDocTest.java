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
package com.ixortalk.authserver.restdocs;

import com.ixortalk.authserver.web.rest.AbstractSpringIntegrationTest;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.restassured.operation.preprocess.UriModifyingOperationPreprocessor;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured.operation.preprocess.RestAssuredPreprocessors.modifyUris;

public abstract class AbstractRestDocTest extends AbstractSpringIntegrationTest {

    public static final HeaderDescriptor AUTHORIZATION_TOKEN_HEADER = headerWithName("Authorization").description("The bearer token needed to authorize this request.");

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    protected RequestSpecification spec;

    @Before
    public void initializeRestDocsSpec() {
        spec = new RequestSpecBuilder()
            .addFilter(documentationConfiguration(this.restDocumentation))
            .build();
    }

    public static UriModifyingOperationPreprocessor staticUris() {
        return modifyUris().scheme("https").host("instance.ixortalk.com").removePort();
    }


}
