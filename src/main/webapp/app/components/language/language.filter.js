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
        .filter('findLanguageFromKey', findLanguageFromKey);

    function findLanguageFromKey() {
        return findLanguageFromKeyFilter;

        function findLanguageFromKeyFilter(lang) {
            return {
                'ca': 'Català',
                'cs': 'Český',
                'da': 'Dansk',
                'de': 'Deutsch',
                'el': 'Ελληνικά',
                'en': 'English',
                'es': 'Español',
                'fr': 'Français',
                'gl': 'Galego',
                'hu': 'Magyar',
                'hi': 'हिंदी',
                'it': 'Italiano',
                'ja': '日本語',
                'ko': '한국어',
                'mr': 'मराठी',
                'nl': 'Nederlands',
                'pl': 'Polski',
                'pt-br': 'Português (Brasil)',
                'pt-pt': 'Português',
                'ro': 'Română',
                'ru': 'Русский',
                'sk': 'Slovenský',
                'sv': 'Svenska',
                'ta': 'தமிழ்',
                'tr': 'Türkçe',
                'zh-cn': '中文（简体）',
                'zh-tw': '繁體中文'
            }[lang];
        }
    }
})();
