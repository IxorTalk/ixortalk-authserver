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

    describe('ActivationController', function() {

        var $scope, $httpBackend, $q; // actual implementations
        var MockAuth, MockStateParams; // mocks
        var createController; // local utility function

        beforeEach(inject(function($injector) {
            $q = $injector.get('$q');
            $scope = $injector.get('$rootScope').$new();
            $httpBackend = $injector.get('$httpBackend');
            MockAuth = jasmine.createSpyObj('MockAuth', ['activateAccount']);
            MockStateParams = jasmine.createSpy('MockStateParams');
            MockStateParams.key = 'ABC123';

            var locals = {
                '$scope': $scope,
                '$stateParams': MockStateParams,
                'Auth': MockAuth
            };
            createController = function() {
                $injector.get('$controller')('ActivationController as vm', locals);
            };
        }));

        it('calls Auth.activateAccount with the key from stateParams', function() {
            // given
            MockAuth.activateAccount.and.returnValue($q.resolve());
            // when
            $scope.$apply(createController);
            // then
            expect(MockAuth.activateAccount).toHaveBeenCalledWith({
                key: 'ABC123'
            });
        });

        it('should set set success to OK upon successful activation', function() {
            // given
            MockAuth.activateAccount.and.returnValue($q.resolve());
            // when
            $scope.$apply(createController);
            // then
            expect($scope.vm.error).toBe(null);
            expect($scope.vm.success).toEqual('OK');
        });

        it('should set set error to ERROR upon activation failure', function() {
            // given
            MockAuth.activateAccount.and.returnValue($q.reject());
            // when
            $scope.$apply(createController);
            // then
            expect($scope.vm.error).toBe('ERROR');
            expect($scope.vm.success).toEqual(null);
        });
    });
});
