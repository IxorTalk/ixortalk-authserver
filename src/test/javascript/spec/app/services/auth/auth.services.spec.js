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

describe('Service Tests', function () {
    beforeEach(mockApiAccountCall);
    beforeEach(mockI18nCalls);
    beforeEach(mockScriptsCalls);

    describe('Auth', function () {
        var $httpBackend, localStorageService, sessionStorageService, authService, spiedAuthServerProvider;

        beforeEach(inject(function($injector, $localStorage, $sessionStorage, Auth, AuthServerProvider) {
            $httpBackend = $injector.get('$httpBackend');
            localStorageService = $localStorage;
            sessionStorageService = $sessionStorage;
            authService = Auth;
            spiedAuthServerProvider = AuthServerProvider;
            $httpBackend.expectPOST(/api\/logout\?cacheBuster=\d+/).respond(200, '');
        }));
        //make sure no expectations were missed in your tests.
        //(e.g. expectGET or expectPOST)
        afterEach(function() {
            $httpBackend.verifyNoOutstandingExpectation();
            $httpBackend.verifyNoOutstandingRequest();
        });
        it('should call backend on logout then call authServerProvider.logout', function(){
            //GIVEN
            //Set spy
            spyOn(spiedAuthServerProvider, 'logout').and.callThrough();

            //WHEN
            authService.logout();
            //flush the backend to "execute" the request to do the expectedGET assertion.
            $httpBackend.flush();

            //THEN
            expect(spiedAuthServerProvider.logout).toHaveBeenCalled();
            expect(localStorageService.authenticationToken).toBe(undefined);
            expect(sessionStorageService.authenticationToken).toBe(undefined);
        });
    });
});
