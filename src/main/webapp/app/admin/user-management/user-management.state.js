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
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-management', {
            parent: 'admin',
            url: '/user-management',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'user-management.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/user-management/user-management.html',
                    controller: 'UserManagementController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user-management');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-management-detail', {
            parent: 'admin',
            url: '/user/:login',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'user-management.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/user-management/user-management-detail.html',
                    controller: 'UserManagementDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user-management');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-management.new', {
            parent: 'user-management',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/user-management/user-management-dialog.html',
                    controller: 'UserManagementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null, login: null, firstName: null, lastName: null, email: null,
                                activated: true, langKey: null, createdBy: null, createdDate: null,
                                lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
                                resetKey: null, authorities: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-management', null, { reload: true });
                }, function() {
                    $state.go('user-management');
                });
            }]
        })
        .state('user-management.edit', {
            parent: 'user-management',
            url: '/{login}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/user-management/user-management-dialog.html',
                    controller: 'UserManagementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['User', function(User) {
                            return User.get({login : $stateParams.login});
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-management', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-management.delete', {
            parent: 'user-management',
            url: '/{login}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/admin/user-management/user-management-delete-dialog.html',
                    controller: 'UserManagementDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['User', function(User) {
                            return User.get({login : $stateParams.login});
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-management', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }
})();
