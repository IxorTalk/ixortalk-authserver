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
        .provider('AlertService', AlertService);

    function AlertService () {
        this.toast = false;
        /*jshint validthis: true */
        this.$get = getService;

        this.showAsToast = function(isToast) {
            this.toast = isToast;
        };

        getService.$inject = ['$timeout', '$sce', '$translate'];

        function getService ($timeout, $sce,$translate) {
            var toast = this.toast,
                alertId = 0, // unique id for each alert. Starts from 0.
                alerts = [],
                timeout = 5000; // default timeout

            return {
                factory: factory,
                isToast: isToast,
                add: addAlert,
                closeAlert: closeAlert,
                closeAlertByIndex: closeAlertByIndex,
                clear: clear,
                get: get,
                success: success,
                error: error,
                info: info,
                warning : warning
            };

            function isToast() {
                return toast;
            }

            function clear() {
                alerts = [];
            }

            function get() {
                return alerts;
            }

            function success(msg, params, position) {
                return this.add({
                    type: 'success',
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function error(msg, params, position) {
                return this.add({
                    type: 'danger',
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function warning(msg, params, position) {
                return this.add({
                    type: 'warning',
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function info(msg, params, position) {
                return this.add({
                    type: 'info',
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function factory(alertOptions) {
                var alert = {
                    type: alertOptions.type,
                    msg: $sce.trustAsHtml(alertOptions.msg),
                    id: alertOptions.alertId,
                    timeout: alertOptions.timeout,
                    toast: alertOptions.toast,
                    position: alertOptions.position ? alertOptions.position : 'top right',
                    scoped: alertOptions.scoped,
                    close: function (alerts) {
                        return closeAlert(this.id, alerts);
                    }
                };
                if(!alert.scoped) {
                    alerts.push(alert);
                }
                return alert;
            }

            function addAlert(alertOptions, extAlerts) {
                alertOptions.alertId = alertId++;
                alertOptions.msg = $translate.instant(alertOptions.msg, alertOptions.params);
                var that = this;
                var alert = this.factory(alertOptions);
                if (alertOptions.timeout && alertOptions.timeout > 0) {
                    $timeout(function () {
                        that.closeAlert(alertOptions.alertId, extAlerts);
                    }, alertOptions.timeout);
                }
                return alert;
            }

            function closeAlert(id, extAlerts) {
                var thisAlerts = extAlerts ? extAlerts : alerts;
                return closeAlertByIndex(thisAlerts.map(function(e) { return e.id; }).indexOf(id), thisAlerts);
            }

            function closeAlertByIndex(index, thisAlerts) {
                return thisAlerts.splice(index, 1);
            }
        }
    }
})();
