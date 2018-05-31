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
'use strict';

describe('Controller Tests', function() {

    beforeEach(mockApiAccountCall);
    beforeEach(mockI18nCalls);

    describe('SettingsController', function() {

        var $scope, $q; // actual implementations
        var MockPrincipal, MockAuth; // mocks
        var createController; // local utility functions

        beforeEach(inject(function($injector) {
            $q = $injector.get('$q');
            $scope = $injector.get("$rootScope").$new();
            MockAuth = jasmine.createSpyObj('MockAuth', ['updateAccount']);
            MockPrincipal = jasmine.createSpyObj('MockPrincipal', ['identity']);
            var locals = {
                '$scope': $scope,
                'Principal': MockPrincipal,
                'Auth': MockAuth
            };
            createController = function() {
                $injector.get('$controller')('SettingsController as vm', locals);
            }
        }));

        it('should send the current identity upon save', function() {
            //GIVEN
            var accountValues = {
                firstName: "John",
                lastName: "Doe",

                activated: true,
                email: "john.doe@mail.com",
                langKey: "en",
                login: "john"
            };
            MockPrincipal.identity.and.returnValue($q.resolve(accountValues));
            MockAuth.updateAccount.and.returnValue($q.resolve());
            $scope.$apply(createController);

            //WHEN
            $scope.vm.save();

            //THEN
            expect(MockPrincipal.identity).toHaveBeenCalled();
            expect(MockAuth.updateAccount).toHaveBeenCalledWith(accountValues);
            expect($scope.vm.settingsAccount).toEqual(accountValues);
        });

        it('should notify of success upon successful save', function() {
            //GIVEN
            var accountValues = {
                firstName: "John",
                lastName: "Doe"
            };
            MockPrincipal.identity.and.returnValue($q.resolve(accountValues));
            MockAuth.updateAccount.and.returnValue($q.resolve());
            createController();

            //WHEN
            $scope.$apply($scope.vm.save);

            //THEN
            expect($scope.vm.error).toBeNull();
            expect($scope.vm.success).toBe('OK');
        });

        it('should notify of error upon failed save', function() {
            //GIVEN
            MockPrincipal.identity.and.returnValue($q.resolve({}));
            MockAuth.updateAccount.and.returnValue($q.reject());
            createController();

            //WHEN
            $scope.$apply($scope.vm.save);

            //THEN
            expect($scope.vm.error).toEqual('ERROR');
            expect($scope.vm.success).toBeNull();
        });
    });
});
