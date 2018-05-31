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
(function () {
    'use strict';

    angular
        .module('authserverApp')
        .factory('PaginationUtil', PaginationUtil);

    function PaginationUtil () {

        var service = {
            parseAscending : parseAscending,
            parsePage : parsePage,
            parsePredicate : parsePredicate
        };

        return service;

        function parseAscending (sort) {
            var sortArray = sort.split(',');
            if (sortArray.length > 1){
                return sort.split(',').slice(-1)[0] === 'asc';
            } else {
                // default to true if no sort defined
                return true;
            }
        }

        // query params are strings, and need to be parsed
        function parsePage (page) {
            return parseInt(page);
        }

        // sort can be in the format `id,asc` or `id`
        function parsePredicate (sort) {
            var sortArray = sort.split(',');
            if (sortArray.length > 1){
                sortArray.pop();
            }
            return sortArray.join(',');
        }
    }
})();
