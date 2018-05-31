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
        .factory('JhiConfigurationService', JhiConfigurationService);

    JhiConfigurationService.$inject = ['$filter', '$http'];

    function JhiConfigurationService ($filter, $http) {
        var service = {
            get: get,
            getEnv: getEnv
        };

        return service;

        function get () {
            return $http.get('configprops').then(getConfigPropsComplete);

            function getConfigPropsComplete (response) {
                var properties = [];
                angular.forEach(response.data, function (data) {
                    properties.push(data);
                });
                var orderBy = $filter('orderBy');
                return orderBy(properties, 'prefix');
            }
        }

        function getEnv () {
            return $http.get('env').then(getEnvComplete);

            function getEnvComplete (response) {
                var properties = {};
                angular.forEach(response.data, function (val,key) {
                    var vals = [];
                    angular.forEach(val, function (v,k) {
                        vals.push({ key:k, val:v });
                    });
                    properties[key] = vals;
                });
                return properties;
            }
        }
    }
})();
