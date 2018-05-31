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
(function() {
    'use strict';

    angular
        .module('authserverApp')
        .factory('DateUtils', DateUtils);

    DateUtils.$inject = ['$filter'];

    function DateUtils ($filter) {

        var service = {
            convertDateTimeFromServer : convertDateTimeFromServer,
            convertLocalDateFromServer : convertLocalDateFromServer,
            convertLocalDateToServer : convertLocalDateToServer,
            dateformat : dateformat
        };

        return service;

        function convertDateTimeFromServer (date) {
            if (date) {
                return new Date(date);
            } else {
                return null;
            }
        }

        function convertLocalDateFromServer (date) {
            if (date) {
                var dateString = date.split('-');
                return new Date(dateString[0], dateString[1] - 1, dateString[2]);
            }
            return null;
        }

        function convertLocalDateToServer (date) {
            if (date) {
                return $filter('date')(date, 'yyyy-MM-dd');
            } else {
                return null;
            }
        }

        function dateformat () {
            return 'yyyy-MM-dd';
        }
    }

})();
