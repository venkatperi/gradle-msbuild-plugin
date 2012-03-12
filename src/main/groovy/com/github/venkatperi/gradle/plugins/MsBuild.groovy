package com.github.venkatperi.gradle.plugins

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
class MsBuild {
    static final String ARCH = 'arch'
    static final String X86 = 'x86'
    static final String VC10 = 'vc10'
    static final String MT = 'mt'
    static final String MTD = 'mtd'
    static final String MD = 'md'
    static final String MDD = 'mdd'
    static final String CLR = 'clr'
    static final String CLR_PURE = 'clrpure'
    static final String DLL = 'dll'
    static final String LIB = 'lib'
    static final String CON = 'con'
    static final String DBG = 'dbg'
    static final String REL = 'rel'
    static final String STD_CALL = 'StdCall'
    static final String CDECL = 'Cdecl'
    static final String FAST_CALL = 'FastCall'
    static final String MULTI_THREADED = 'MultiThreaded'
    static final String MULTI_THREADED_DEBUG = 'MultiThreadedDebug'
    static final String MULTI_THREADED_DLL = 'MultiThreadedDLL'
    static final String MULTI_THREADED_DEBUG_DLL = 'MultiThreadedDebugDLL'
    static final String APPLICATION = 'Application'
    static final String DYNAMIC_LIBARY = 'DynamicLibary'
    static final String STATIC_LIBRARY = 'StaticLibrary'
    static final String WINDOWS = 'Windows'
    static final String CONSOLE = 'Console'
    static final String WIN32 = 'Win32'
    static final String WIN64 = 'Win64'

    def static Call = [sc: STD_CALL, cc: CDECL, fc: FAST_CALL]

    def static Compiler = [vc10:"Visual Studio 10", vc9:"Visual Studio 9"]

    def static Crt = [mt: MULTI_THREADED,
            mtd: MULTI_THREADED_DEBUG,
            md: MULTI_THREADED_DLL,
            mdd: MULTI_THREADED_DEBUG_DLL]

    def static Build = [dbg: DEBUG, rel: RELEASE]

    def static Arch = ['x86': WIN32, 'x64': WIN64]

    def static Type = ['exe': APPLICATION, 'dll': DYNAMIC_LIBARY, 'lib': STATIC_LIBRARY, 'con': APPLICATION]

    def static Subsystem = ['exe': WINDOWS, 'dll': WINDOWS, 'lib': WINDOWS, 'con': CONSOLE]

    static final String DEBUG = 'debug'
    static final String RELEASE = 'release'
}
