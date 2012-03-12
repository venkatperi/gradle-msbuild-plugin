package com.github.venkatperi.gradle.plugins

/**
 * Created by IntelliJ IDEA.
 * User: vperi
 * Date: 3/6/12
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
enum _ProjectType {

    /**
     * Win32 Applicatoin
     */

    Application,

    /**
     * Win32 DLL
     */
    DynamicLibrary,


    /**
     * Win32 Static Linked Library
     */
    StaticLibrary,

    /**
     * Win32 Console Application
     */
    Console
}
