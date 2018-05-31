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

    describe('ResetFinishController', function() {

        var $scope, $q; // actual implementations
        var MockStateParams, MockTimeout, MockAuth; // mocks
        var createController; // local utility function

        beforeEach(inject(function($injector) {
            $q = $injector.get('$q');
            $scope = $injector.get('$rootScope').$new();
            MockStateParams = jasmine.createSpy('MockStateParams');

            MockTimeout = jasmine.createSpy('MockTimeout');
            MockAuth = jasmine.createSpyObj('MockAuth', ['resetPasswordInit']);

            var locals = {
                '$scope': $scope,
                '$stateParams': MockStateParams,
                '$timeout': MockTimeout,
                'Auth': MockAuth
            };
            createController = function() {
                return $injector.get('$controller')('ResetFinishController as vm', locals);
            };
        }));

        it('should define its initial state', function() {
            // given
            MockStateParams.key = 'XYZPDQ';
            createController();

            // then
            expect($scope.vm.keyMissing).toBeFalsy();

            expect($scope.vm.doNotMatch).toBeNull();
            expect($scope.vm.resetAccount).toEqual({});
        });

        it('registers a timeout handler set set focus', function() {
            // given
            var MockAngular = jasmine.createSpyObj('MockAngular', ['element']);
            var MockElement = jasmine.createSpyObj('MockElement', ['focus']);
            MockAngular.element.and.returnValue(MockElement);
            MockTimeout.and.callFake(function(callback) {
                withMockedAngular(MockAngular, callback)();
            });
            createController();

            // then
            expect(MockTimeout).toHaveBeenCalledWith(jasmine.any(Function));
            expect(MockAngular.element).toHaveBeenCalledWith('#password');
            expect(MockElement.focus).toHaveBeenCalled();
        });

    });
});
