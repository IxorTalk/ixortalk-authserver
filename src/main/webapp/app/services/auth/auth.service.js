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
        .factory('Auth', Auth);

    Auth.$inject = ['$rootScope', '$state', '$sessionStorage', '$q', '$translate', 'Principal', 'AuthServerProvider', 'Account', 'LoginService', 'Register', 'Activate', 'Password', 'PasswordResetInit', 'PasswordResetFinish'];

    function Auth ($rootScope, $state, $sessionStorage, $q, $translate, Principal, AuthServerProvider, Account, LoginService, Register, Activate, Password, PasswordResetInit, PasswordResetFinish) {
        var service = {
            activateAccount: activateAccount,
            authorize: authorize,
            changePassword: changePassword,
            createAccount: createAccount,
            login: login,
            logout: logout,
            resetPasswordFinish: resetPasswordFinish,
            resetPasswordInit: resetPasswordInit,
            updateAccount: updateAccount
        };

        return service;

        function activateAccount (key, callback) {
            var cb = callback || angular.noop;

            return Activate.get(key,
                function (response) {
                    return cb(response);
                },
                function (err) {
                    return cb(err);
                }.bind(this)).$promise;
        }

        function authorize (force) {
            var authReturn = Principal.identity(force).then(authThen);

            return authReturn;

            function authThen () {
                var isAuthenticated = Principal.isAuthenticated();

                // an authenticated user can't access to login and register pages
                if (isAuthenticated && $rootScope.toState.parent === 'account' && ($rootScope.toState.name === 'login' || $rootScope.toState.name === 'register')) {
                    $state.go('home');
                }

                // recover and clear previousState after external login redirect (e.g. oauth2)
                if (isAuthenticated && !$rootScope.fromState.name && $sessionStorage.previousStateName) {
                    var previousStateName = $sessionStorage.previousStateName;
                    var previousStateParams = $sessionStorage.previousStateParams;
                    delete $sessionStorage.previousStateName;
                    delete $sessionStorage.previousStateParams;
                    $state.go(previousStateName, previousStateParams);
                }

                if ($rootScope.toState.data.authorities && $rootScope.toState.data.authorities.length > 0 && !Principal.hasAnyAuthority($rootScope.toState.data.authorities)) {
                    if (isAuthenticated) {
                        // user is signed in but not authorized for desired state
                        $state.go('accessdenied');
                    }
                    else {
                        // user is not authenticated. stow the state they wanted before you
                        // send them to the login service, so you can return them when you're done
                        $sessionStorage.previousStateName = $rootScope.toState.name;
                        $sessionStorage.previousStateParams = $rootScope.toStateParams;

                        // now, send them to the signin state so they can log in
                        $state.go('accessdenied').then(function() {
                            LoginService.open();
                        });
                    }
                }
            }
        }

        function changePassword (newPassword, callback) {
            var cb = callback || angular.noop;

            return Password.save(newPassword, function () {
                return cb();
            }, function (err) {
                return cb(err);
            }).$promise;
        }

        function createAccount (account, callback) {
            var cb = callback || angular.noop;

            return Register.save(account,
                function () {
                    return cb(account);
                },
                function (err) {
                    this.logout();
                    return cb(err);
                }.bind(this)).$promise;
        }

        function login (credentials, callback) {
            var cb = callback || angular.noop;
            var deferred = $q.defer();

            AuthServerProvider.login(credentials)
                .then(loginThen)
                .catch(function (err) {
                    this.logout();
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));

            function loginThen (data) {
                Principal.identity(true).then(function(account) {
                    // After the login the language will be changed to
                    // the language selected by the user during his registration
                    if (account!== null) {
                        $translate.use(account.langKey).then(function () {
                            $translate.refresh();
                        });
                    }
                    deferred.resolve(data);
                });
                return cb();
            }

            return deferred.promise;
        }


        function logout () {
            AuthServerProvider.logout();
            Principal.authenticate(null);

            // Reset state memory
            delete $sessionStorage.previousStateName;
            delete $sessionStorage.previousStateParams;
        }

        function resetPasswordFinish (keyAndPassword, callback) {
            var cb = callback || angular.noop;

            return PasswordResetFinish.save(keyAndPassword, function () {
                return cb();
            }, function (err) {
                return cb(err);
            }).$promise;
        }

        function resetPasswordInit (mail, callback) {
            var cb = callback || angular.noop;

            return PasswordResetInit.save(mail, function() {
                return cb();
            }, function (err) {
                return cb(err);
            }).$promise;
        }

        function updateAccount (account, callback) {
            var cb = callback || angular.noop;

            return Account.save(account,
                function () {
                    return cb(account);
                },
                function (err) {
                    return cb(err);
                }.bind(this)).$promise;
        }
    }
})();
