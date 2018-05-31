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
package com.ixortalk.authserver.web.rest.errors;

/**
 * Custom, parameterized exception, which can be translated on the client side.
 * For example:
 *
 * <pre>
 * throw new CustomParameterizedException(&quot;myCustomError&quot;, &quot;hello&quot;, &quot;world&quot;);
 * </pre>
 *
 * Can be translated with:
 *
 * <pre>
 * "error.myCustomError" :  "The server says {{params[0]}} to {{params[1]}}"
 * </pre>
 */
public class CustomParameterizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String message;
    private final String[] params;

    public CustomParameterizedException(String message, String... params) {
        super(message);
        this.message = message;
        this.params = params;
    }

    public ParameterizedErrorDTO getErrorDTO() {
        return new ParameterizedErrorDTO(message, params);
    }

}
