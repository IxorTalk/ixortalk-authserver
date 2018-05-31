/*
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
(function(){
    'use strict';

    angular
        .module('authserverApp')
        .factory('ParseLinks', ParseLinks);

    function ParseLinks () {

        var service = {
            parse : parse
        };

        return service;

        function parse (header) {
            if (header.length === 0) {
                throw new Error('input must not be of zero length');
            }

            // Split parts by comma
            var parts = header.split(',');
            var links = {};
            // Parse each part into a named link
            angular.forEach(parts, function (p) {
                var section = p.split(';');
                if (section.length !== 2) {
                    throw new Error('section could not be split on ";"');
                }
                var url = section[0].replace(/<(.*)>/, '$1').trim();
                var queryString = {};
                url.replace(
                    new RegExp('([^?=&]+)(=([^&]*))?', 'g'),
                    function($0, $1, $2, $3) { queryString[$1] = $3; }
                );
                var page = queryString.page;
                if( angular.isString(page) ) {
                    page = parseInt(page);
                }
                var name = section[1].replace(/rel="(.*)"/, '$1').trim();
                links[name] = page;
            });

            return links;
        }
    }
})();
