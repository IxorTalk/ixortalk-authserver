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
/* globals $ */
(function() {
    'use strict';

    angular
        .module('authserverApp')
        .directive('passwordStrengthBar', passwordStrengthBar);

    function passwordStrengthBar () {
        var directive = {
            replace: true,
            restrict: 'E',
            template: '<div id="strength">' +
                '<small translate="global.messages.validate.newpassword.strength">Password strength:</small>' +
                '<ul id="strengthBar">' +
                '<li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li>' +
                '</ul>' +
                '</div>',
            scope: {
                passwordToCheck: '='
            },
            link: linkFunc
        };

        return directive;

        /* private helper methods*/

        function linkFunc(scope, iElement) {
            var strength = {
                colors: ['#F00', '#F90', '#FF0', '#9F0', '#0F0'],
                mesureStrength: function (p) {

                    var _force = 0;
                    var _regex = /[$-/:-?{-~!"^_`\[\]]/g; // "

                    var _lowerLetters = /[a-z]+/.test(p);
                    var _upperLetters = /[A-Z]+/.test(p);
                    var _numbers = /[0-9]+/.test(p);
                    var _symbols = _regex.test(p);

                    var _flags = [_lowerLetters, _upperLetters, _numbers, _symbols];
                    var _passedMatches = $.grep(_flags, function (el) {
                        return el === true;
                    }).length;

                    _force += 2 * p.length + ((p.length >= 10) ? 1 : 0);
                    _force += _passedMatches * 10;

                    // penality (short password)
                    _force = (p.length <= 6) ? Math.min(_force, 10) : _force;

                    // penality (poor variety of characters)
                    _force = (_passedMatches === 1) ? Math.min(_force, 10) : _force;
                    _force = (_passedMatches === 2) ? Math.min(_force, 20) : _force;
                    _force = (_passedMatches === 3) ? Math.min(_force, 40) : _force;

                    return _force;

                },
                getColor: function (s) {

                    var idx = 0;
                    if (s <= 10) {
                        idx = 0;
                    }
                    else if (s <= 20) {
                        idx = 1;
                    }
                    else if (s <= 30) {
                        idx = 2;
                    }
                    else if (s <= 40) {
                        idx = 3;
                    }
                    else {
                        idx = 4;
                    }

                    return { idx: idx + 1, col: this.colors[idx] };
                }
            };
            scope.$watch('passwordToCheck', function (password) {
                if (password) {
                    var c = strength.getColor(strength.mesureStrength(password));
                    iElement.removeClass('ng-hide');
                    iElement.find('ul').children('li')
                        .css({ 'background-color': '#DDD' })
                        .slice(0, c.idx)
                        .css({ 'background-color': c.col });
                }
            });
        }
    }
})();
