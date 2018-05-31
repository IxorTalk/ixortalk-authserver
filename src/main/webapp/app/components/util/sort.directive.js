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
        .directive('jhSort', jhSort);

    function jhSort () {
        var directive = {
            restrict: 'A',
            scope: {
                predicate: '=jhSort',
                ascending: '=',
                callback: '&'
            },
            controller: SortController,
            controllerAs: 'vm',
            bindToController: true
        };

        return directive;
    }

    SortController.$inject = ['$scope', '$element'];

    function SortController ($scope, $element) {
        var vm = this;

        vm.applyClass = applyClass;
        vm.resetClasses = resetClasses;
        vm.sort = sort;
        vm.triggerApply = triggerApply;

        $scope.$watchGroup(['vm.predicate', 'vm.ascending'], vm.triggerApply);
        vm.triggerApply();

        function applyClass (element) {
            var thisIcon = element.find('span.glyphicon'),
                sortIcon = 'glyphicon-sort',
                sortAsc = 'glyphicon-sort-by-attributes',
                sortDesc = 'glyphicon-sort-by-attributes-alt',
                remove = sortIcon + ' ' + sortDesc,
                add = sortAsc;
            if (!vm.ascending) {
                remove = sortIcon + ' ' + sortAsc;
                add = sortDesc;
            }
            vm.resetClasses();
            thisIcon.removeClass(remove);
            thisIcon.addClass(add);
        }

        function resetClasses () {
            var allThIcons = $element.find('span.glyphicon'),
                sortIcon = 'glyphicon-sort',
                sortAsc = 'glyphicon-sort-by-attributes',
                sortDesc = 'glyphicon-sort-by-attributes-alt';
            allThIcons.removeClass(sortAsc + ' ' + sortDesc);
            allThIcons.addClass(sortIcon);
        }

        function sort (field) {
            if (field !== vm.predicate) {
                vm.ascending = true;
            } else {
                vm.ascending = !vm.ascending;
            }
            vm.predicate = field;
            $scope.$apply();
            vm.callback();
        }

        function triggerApply (values)  {
            vm.resetClasses();
            if (values && values[0] !== '_score') {
                vm.applyClass($element.find('th[jh-sort-by=\'' + values[0] + '\']'));
            }
        }
    }
})();
