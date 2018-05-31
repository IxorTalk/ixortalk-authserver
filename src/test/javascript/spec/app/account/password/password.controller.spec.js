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

    describe('PasswordController', function() {

        var $scope, $httpBackend, $q;
        var MockAuth;
        var createController;

        beforeEach(inject(function($injector) {
            $scope = $injector.get('$rootScope').$new();
            $q = $injector.get('$q');
            $httpBackend = $injector.get('$httpBackend');

            MockAuth = jasmine.createSpyObj('MockAuth', ['changePassword']);
            var locals = {
                '$scope': $scope,
                'Auth': MockAuth
            };
            createController = function() {
                $injector.get('$controller')('PasswordController as vm', locals);
            }
        }));

        it('should show error if passwords do not match', function() {
            //GIVEN
            createController();
            $scope.vm.password = 'password1';
            $scope.vm.confirmPassword = 'password2';
            //WHEN
            $scope.vm.changePassword();
            //THEN
            expect($scope.vm.doNotMatch).toBe('ERROR');
            expect($scope.vm.error).toBeNull();
            expect($scope.vm.success).toBeNull();
        });
        it('should call Auth.changePassword when passwords match', function() {
            //GIVEN
            MockAuth.changePassword.and.returnValue($q.resolve());
            createController();
            $scope.vm.password = $scope.vm.confirmPassword = 'myPassword';

            //WHEN
            $scope.$apply($scope.vm.changePassword);

            //THEN
            expect(MockAuth.changePassword).toHaveBeenCalledWith('myPassword');
        });

        it('should set success to OK upon success', function() {
            //GIVEN
            MockAuth.changePassword.and.returnValue($q.resolve());
            createController();
            $scope.vm.password = $scope.vm.confirmPassword = 'myPassword';

            //WHEN
            $scope.$apply($scope.vm.changePassword);

            //THEN
            expect($scope.vm.doNotMatch).toBeNull();
            expect($scope.vm.error).toBeNull();
            expect($scope.vm.success).toBe('OK');
        });

        it('should notify of error if change password fails', function() {
            //GIVEN
            MockAuth.changePassword.and.returnValue($q.reject());
            createController();
            $scope.vm.password = $scope.vm.confirmPassword = 'myPassword';

            //WHEN
            $scope.$apply($scope.vm.changePassword);

            //THEN
            expect($scope.vm.doNotMatch).toBeNull();
            expect($scope.vm.success).toBeNull();
            expect($scope.vm.error).toBe('ERROR');
        });
    });
});
