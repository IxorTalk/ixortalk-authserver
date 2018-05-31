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
        .controller('JhiMetricsMonitoringModalController', JhiMetricsMonitoringModalController);

    JhiMetricsMonitoringModalController.$inject = ['$uibModalInstance', 'threadDump'];

    function JhiMetricsMonitoringModalController ($uibModalInstance, threadDump) {
        var vm = this;

        vm.cancel = cancel;
        vm.getLabelClass = getLabelClass;
        vm.threadDump = threadDump;
        vm.threadDumpAll = 0;
        vm.threadDumpBlocked = 0;
        vm.threadDumpRunnable = 0;
        vm.threadDumpTimedWaiting = 0;
        vm.threadDumpWaiting = 0;

        angular.forEach(threadDump, function(value) {
            if (value.threadState === 'RUNNABLE') {
                vm.threadDumpRunnable += 1;
            } else if (value.threadState === 'WAITING') {
                vm.threadDumpWaiting += 1;
            } else if (value.threadState === 'TIMED_WAITING') {
                vm.threadDumpTimedWaiting += 1;
            } else if (value.threadState === 'BLOCKED') {
                vm.threadDumpBlocked += 1;
            }
        });

        vm.threadDumpAll = vm.threadDumpRunnable + vm.threadDumpWaiting +
            vm.threadDumpTimedWaiting + vm.threadDumpBlocked;

        function cancel () {
            $uibModalInstance.dismiss('cancel');
        }

        function getLabelClass (threadState) {
            if (threadState === 'RUNNABLE') {
                return 'label-success';
            } else if (threadState === 'WAITING') {
                return 'label-info';
            } else if (threadState === 'TIMED_WAITING') {
                return 'label-warning';
            } else if (threadState === 'BLOCKED') {
                return 'label-danger';
            }
        }
    }
})();
