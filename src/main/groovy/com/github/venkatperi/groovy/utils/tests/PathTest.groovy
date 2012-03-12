package com.github.venkatperi.groovy.utils.tests

import com.github.venkatperi.groovy.utils.Path

/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Venkat Peri 
 */
class PathTest extends GroovyTestCase {
    void testDiv() {

        def a = new Path('a/b/c')
        def b = new Path('1/2/3')


        def x = a / b
        def y = new Path('a/b/c/1/2/3')
        assert x.path == y.path
        assert x == y
    }

    void testToString() {
        def a = new Path('a/b/c')
        String x = a
        assert x == a.path
        println a
    }

}
