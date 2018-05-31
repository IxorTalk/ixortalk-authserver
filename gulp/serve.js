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
var gulp = require('gulp'),
    util = require('./utils'),
    url = require('url'),
    browserSync = require('browser-sync'),
    proxy = require('proxy-middleware');

var config = require('./config');

module.exports = function () {
    var baseUri = config.uri + config.apiPort;
    // Routes to proxy to the backend. Routes ending with a / will setup
    // a redirect so that if accessed without a trailing slash, will
    // redirect. This is required for some endpoints for proxy-middleware
    // to correctly handle them.
    var proxyRoutes = [
        '/'
    ];

    var requireTrailingSlash = proxyRoutes.filter(function (r) {
        return util.endsWith(r, '/');
    }).map(function (r) {
        // Strip trailing slash so we can use the route to match requests
        // with non trailing slash
        return r.substr(0, r.length - 1);
    });

    var proxies = [
        // Ensure trailing slash in routes that require it
        function (req, res, next) {
            requireTrailingSlash.forEach(function (route){
                if (url.parse(req.url).path === route) {
                    res.statusCode = 301;
                    res.setHeader('Location', route + '/');
                    res.end();
                }
            });

            next();
        }
    ]
    .concat(
        // Build a list of proxies for routes: [route1_proxy, route2_proxy, ...]
        proxyRoutes.map(function (r) {
            var options = url.parse(baseUri + r);
            options.route = r;
            options.preserveHost = true;
            return proxy(options);
        }));

    browserSync({
        open: true,
        port: config.port,
        server: {
            baseDir: config.app,
            middleware: proxies
        }
    });

    gulp.start('watch');
}
