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
        .controller('AuditsController', AuditsController);

    AuditsController.$inject = ['$filter', 'AuditsService', 'ParseLinks'];

    function AuditsController ($filter, AuditsService, ParseLinks) {
        var vm = this;

        vm.audits = null;
        vm.fromDate = null;
        vm.links = null;
        vm.loadPage = loadPage;
        vm.onChangeDate = onChangeDate;
        vm.page = 1;
        vm.previousMonth = previousMonth;
        vm.toDate = null;
        vm.today = today;
        vm.totalItems = null;

        vm.today();
        vm.previousMonth();
        vm.onChangeDate();

        function onChangeDate () {
            var dateFormat = 'yyyy-MM-dd';
            var fromDate = $filter('date')(vm.fromDate, dateFormat);
            var toDate = $filter('date')(vm.toDate, dateFormat);

            AuditsService.query({page: vm.page -1, size: 20, fromDate: fromDate, toDate: toDate}, function(result, headers){
                vm.audits = result;
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
            });
        }

        // Date picker configuration
        function today () {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            vm.toDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1);
        }

        function previousMonth () {
            var fromDate = new Date();
            if (fromDate.getMonth() === 0) {
                fromDate = new Date(fromDate.getFullYear() - 1, 11, fromDate.getDate());
            } else {
                fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
            }

            vm.fromDate = fromDate;
        }

        function loadPage (page) {
            vm.page = page;
            vm.onChangeDate();
        }
    }
})();
