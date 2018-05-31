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
        .controller('UserManagementController', UserManagementController);

    UserManagementController.$inject = ['Principal', 'User', 'ParseLinks', 'paginationConstants', 'JhiLanguageService'];

    function UserManagementController(Principal, User, ParseLinks, paginationConstants, JhiLanguageService) {
        var vm = this;

        vm.clear = clear;
        vm.currentAccount = null;
        vm.languages = null;
        vm.links = null;
        vm.loadAll = loadAll;
        vm.loadPage = loadPage;
        vm.page = 1;
        vm.setActive = setActive;
        vm.totalItems = null;
        vm.users = [];


        vm.loadAll();


        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });

        Principal.identity().then(function(account) {
            vm.currentAccount = account;
        });


        function loadAll () {
            User.query({page: vm.page - 1, size: paginationConstants.itemsPerPage}, function (result, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');

                //hide anonymous user from user management: it's a required user for Spring Security
                for(var i in result) {
                    if(result[i]['login'] === 'anonymoususer') {
                        result.splice(i,1);
                    }
                }
                vm.users = result;
            });
        }

        function loadPage (page) {
            vm.page = page;
            vm.loadAll();
        }

        function setActive (user, isActivated) {
            user.activated = isActivated;
            User.update(user, function () {
                vm.loadAll();
                vm.clear();
            });
        }

        function clear () {
            vm.user = {
                id: null, login: null, firstName: null, lastName: null, email: null,
                activated: null, langKey: null, createdBy: null, createdDate: null,
                lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
                resetKey: null, authorities: null
            };
            vm.editForm.$setPristine();
            vm.editForm.$setUntouched();
        }
    }
})();
