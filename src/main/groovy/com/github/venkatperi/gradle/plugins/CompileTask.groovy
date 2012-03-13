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
 * @author Venkat Peri. RemoteReality Corp. 
 */

package com.github.venkatperi.gradle.plugins

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.StopActionException

class CompileTask extends DefaultTask {
    static final String MSBUILD_TOOLS_PATH = 'MSBuildToolsPath'
    static final String MSBUILD_EXE = 'msbuild.exe'
    private static final String MSBUILD_40 = "SOFTWARE\\Microsoft\\MSBuild\\ToolsVersions\\4.0"
    private static final String MSBUILD_35 = "SOFTWARE\\Microsoft\\MSBuild\\ToolsVersions\\3.5"
    private static final String MSBUILD_20 = "SOFTWARE\\Microsoft\\MSBuild\\ToolsVersions\\2.0"
    private static final String ERROR_MSBUILD_NOT_FOUND = "Could not find a suitable version of MSBUILD. Please install .NET 4.0 or higher."

    def buildConfig = project.buildConfig
    def config = project.extensions.msproject
    def name = config.name == null ? project.name : config.name
    def projFile = name + VcxProjTask.DEFAULT_EXTENSION

    String msbuildDir
    String msbuildExe

    CompileTask() {
        inputs.dir config.srcDir
        outputs.dir config.outDir
    }

    /**
     * find latest versin of MSDEV
     * @return
     */
    private def findMsdev() {
        def keys = [MSBUILD_40]

        keys.find { x ->
            try {
                def v = WinRegistry.readString(
                        WinRegistry.HKEY_LOCAL_MACHINE,        //HKEY
                        x,                                       //Key
                        MSBUILD_TOOLS_PATH);                   //ValueName

                if (v != null) {
                    msbuildDir = v
                    msbuildExe = v + MSBUILD_EXE
                }
            }
            catch (e) {
            }
        }

        if (msbuildExe == null) {
            throw new StopActionException(ERROR_MSBUILD_NOT_FOUND)
        }
    }

    /**
     * tasks's action
     * @return
     */
    @TaskAction
    def build() {
        findMsdev()

        project.exec {
            executable = msbuildExe
            args = [projFile, "/p:Configuration=" + MsBuild.Build[buildConfig]] + config.additionalArgs
        }
    }
}
