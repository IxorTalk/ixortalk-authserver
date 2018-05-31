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
var fs = require('fs'),
    gulp = require('gulp'),
    lazypipe = require('lazypipe'),
    footer = require('gulp-footer'),
    sourcemaps = require('gulp-sourcemaps'),
    rev = require('gulp-rev'),
    htmlmin = require('gulp-htmlmin'),
    ngAnnotate = require('gulp-ng-annotate'),
    prefix = require('gulp-autoprefixer'),
    cssnano = require('gulp-cssnano'),
    uglify = require('gulp-uglify'),
    useref = require("gulp-useref"),
    revReplace = require("gulp-rev-replace")
    plumber = require('gulp-plumber'),
    gulpIf = require('gulp-if'),
    handleErrors = require('./handleErrors');

var config = require('./config');

var initTask = lazypipe()
    .pipe(sourcemaps.init)
    .pipe(footer, ';');
var jsTask = lazypipe()
    .pipe(ngAnnotate)
    .pipe(uglify);
var cssTask = lazypipe()
    .pipe(prefix)
    .pipe(cssnano);

module.exports = function() {
    var templates = fs.readFileSync(config.tmp + '/templates.js');
    var manifest = gulp.src(config.revManifest);

    return gulp.src([config.app + '**/*.html',
        '!' + config.app + 'app/**/*.html',
        '!' + config.app + 'swagger-ui/**/*',
        '!' + config.bower + '**/*.html'])
        .pipe(plumber({errorHandler: handleErrors}))
        //init sourcemaps and prepend semicolon
        .pipe(useref({}, initTask))
        //append html templates
        .pipe(gulpIf('**/app.js', footer(templates)))
        .pipe(gulpIf('*.js', jsTask()))
        .pipe(gulpIf('*.css', cssTask()))
        .pipe(gulpIf('*.html', htmlmin({collapseWhitespace: true})))
        .pipe(gulpIf('**/*.!(html)', rev()))
        .pipe(revReplace({manifest}))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(config.dist));
}
