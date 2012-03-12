package com.github.venkatperi.gradle.plugins

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Created by IntelliJ IDEA.
 * User: vperi
 * Date: 3/6/12
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
class VcxProjTask extends DefaultTask {

    static final String MICROSOFT_CPP_PROPS = '$(VCTargetsPath)/Microsoft.Cpp.props'
    static final String EXTENSION_SETTINGS = 'ExtensionSettings'
    static final String CONFIG_PLATFORM_EQ_DBG_WIN32 = '\'$(Configuration)|$(Platform)\'==\'Debug|Win32\''
    static final String CONFIG_PLATFORM_EQ_REL_WIN32 = '\'$(Configuration)|$(Platform)\'==\'Release|Win32\''
    static final String MICROSOFT_CPP_DEFAULT_PROPS = '$(VCTargetsPath)/Microsoft.Cpp.Default.props'
    static final String GLOBALS = 'Globals'
    static final String CONFIGURATION = 'Configuration'
    static final String PROPERTY_SHEETS = 'PropertySheets'
    static final String LOCAL_APP_DATA_PLATFORM = 'LocalAppDataPlatform'
    static final String USER_MACROS = 'UserMacros'
    static final String WIN32 = 'Win32'
    static final String RELEASE = 'Release'
    static final String REL_WIN32 = 'Release|Win32'
    static final String DBG_WIN32 = 'Debug|Win32'
    static final String DEBUG = 'Debug'
    static final String PROJECT_CONFIGURATIONS = 'ProjectConfigurations'
    static final String BUILD = 'Build'
    static final String VERSION = '4.0'
    static final String SCHEMA = 'http://schemas.microsoft.com/developer/msbuild/2003'
    static final String USER_PROPS = 'exists(\'$(UserRootDir)/Microsoft.Cpp.$(Platform).user.props\')'
    static final String USER_PLATFORM_PROPS = '$(UserRootDir)/Microsoft.Cpp.$(Platform).user.props'
    static final String MICROSOFT_CPP_TARGETS = '$(VCTargetsPath)/Microsoft.Cpp.targets'
    static final String EXTENSION_TARGETS = 'ExtensionTargets'
    static final String DEFAULT_EXTENSION = '.vcxproj'
    static final String FILTER_ALL = '**/*.'
    static final String SEMI_COLON = ';'
    static final String LIBRARIES = 'libraries'
    static final String ALL_RC = '**/*.rc'
    static final String CREATE = 'Create'
    static final String STDAFX = "stdafx"
    static final String EMPTY = ''
    private static final String LIBS = 'libs'

    def config = project.extensions.msproject
    def name = config.name == null ? project.name : config.name
    def n = name + DEFAULT_EXTENSION

    @OutputFile
    def projFile = new File(n)

    def srcDirs = new File(config.srcDir)

    List incDirs = [config.srcDir] + config.incDirs + Defaults.baseIncDirs

    def incDirFile = incDirs.collect { new File(it) }

    List libDirs = config.libDirs + Defaults.baseLibDirs
    def libDirFile = libDirs.collect { new File(it) }

    List cxxflags = config.cxxFlags + Defaults.defines + (project.type == MsBuild.DLL ? Defaults.winDllDefines : []) + (project.type == MsBuild.LIB ? Defaults.winLibDefines : [])

    List libs = config.libs + Defaults.libs

    def libFiles = libs.collect { new File(it) }

    @InputFiles
    def allInputFiles = incDirFile + libDirFile + libFiles

    @TaskAction
    public void vcxproj() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        try {
            def l = project.tasks.getByName(LIBS)  //see if cars plugin is available
            incDirs.addAll project.libraries.incDirs
            libDirs.addAll project.libraries.libDirs
            cxxflags.addAll project.libraries.cxxflags
            libs.addAll project.libraries.libs
        }
        catch (e) {
        }


        def GlobalConfig = config.global.config
        def DebugConfig = config.debug.config
        def ReleaseConfig = config.release.config

        xml.Project(DefaultTargets: BUILD, ToolsVersion: VERSION, xmlns: SCHEMA) {

            ItemGroup(Label: PROJECT_CONFIGURATIONS) {

                ProjectConfiguration(Include: DBG_WIN32) {
                    Configuration(DEBUG)
                    Platform(WIN32)
                }

                ProjectConfiguration(Include: REL_WIN32) {
                    Configuration(RELEASE)
                    Platform(WIN32)
                }
            }

            PropertyGroup(Label: GLOBALS) {
                ConfigurationType(MsBuild.Type[project.type])
                CallingConvention(MsBuild.Call[project.call])
                TargetName(config.outputName)
                mkp.yieldUnescaped(GlobalConfig[MSBuildExt.Config.Type.Compile])
            }

            Import(Project: MICROSOFT_CPP_DEFAULT_PROPS)

            PropertyGroup(Condition: CONFIG_PLATFORM_EQ_DBG_WIN32, Label: CONFIGURATION) {
                mkp.yieldUnescaped(DebugConfig[MSBuildExt.Config.Type.Configuration])
                mkp.yieldUnescaped(GlobalConfig[MSBuildExt.Config.Type.Configuration])
            }

            PropertyGroup(Condition: CONFIG_PLATFORM_EQ_REL_WIN32, Label: CONFIGURATION) {
                mkp.yieldUnescaped(ReleaseConfig[MSBuildExt.Config.Type.Configuration])
                mkp.yieldUnescaped(GlobalConfig[MSBuildExt.Config.Type.Configuration])
            }

            Import(Project: MICROSOFT_CPP_PROPS)

            ImportGroup(Label: EXTENSION_SETTINGS, EMPTY)

            ImportGroup(Condition: CONFIG_PLATFORM_EQ_DBG_WIN32, Label: PROPERTY_SHEETS) {
                Import(Condition: USER_PROPS, Label: LOCAL_APP_DATA_PLATFORM, Project: USER_PLATFORM_PROPS)
            }

            ImportGroup(Condition: CONFIG_PLATFORM_EQ_REL_WIN32, Label: PROPERTY_SHEETS) {
                Import(Condition: USER_PROPS, Label: LOCAL_APP_DATA_PLATFORM, Project: USER_PLATFORM_PROPS)
            }

            PropertyGroup(Label: USER_MACROS)

            PropertyGroup(Condition: CONFIG_PLATFORM_EQ_DBG_WIN32) {
                IntDir(config.intDir)
                OutDir(config.outDir)

                mkp.yieldUnescaped(DebugConfig[MSBuildExt.Config.Type.General])

                IncludePath {mkp.yieldUnescaped((config.debug.incDirs + incDirs).sum { x -> x + SEMI_COLON })}
                LibraryPath {mkp.yieldUnescaped((config.debug.libDirs + libDirs).sum { x -> x + SEMI_COLON })}
            }

            PropertyGroup(Condition: CONFIG_PLATFORM_EQ_REL_WIN32) {
                OutDir(config.outDir)
                IntDir(config.intDir)

                mkp.yieldUnescaped(ReleaseConfig[MSBuildExt.Config.Type.General])

                IncludePath {mkp.yieldUnescaped((config.release.incDirs + incDirs).sum { x -> x + SEMI_COLON })}
                LibraryPath {mkp.yieldUnescaped((config.release.libDirs + libDirs).sum { x -> x + SEMI_COLON })}
            }

            ItemDefinitionGroup(Condition: CONFIG_PLATFORM_EQ_DBG_WIN32) {
                ClCompile() {

                    RuntimeLibrary(MsBuild.Crt[project.crt])
                    mkp.yieldUnescaped(DebugConfig[MSBuildExt.Config.Type.Compile])

                    PreprocessorDefinitions {
                        mkp.yieldUnescaped((config.debug.defines
                                + Defaults.debugDefines + cxxflags).sum { x -> x + SEMI_COLON })
                    }
                }

                Link() {
                    mkp.yieldUnescaped(DebugConfig[MSBuildExt.Config.Type.Link])
                    SubSystem(MsBuild.Subsystem[project.type])
                    AdditionalDependencies {mkp.yieldUnescaped((config.debug.libs + libs).sum { x -> x + SEMI_COLON })}
                }
            }

            ItemDefinitionGroup(Condition: CONFIG_PLATFORM_EQ_REL_WIN32) {
                ClCompile() {
                    RuntimeLibrary(MsBuild.Crt[project.crt])

                    mkp.yieldUnescaped(ReleaseConfig[MSBuildExt.Config.Type.Compile])

                    PreprocessorDefinitions {
                        mkp.yieldUnescaped((config.release.defines
                                + cxxflags
                                + Defaults.releaseDefines).sum { x -> x + SEMI_COLON })
                    }
                }

                Link() {
                    mkp.yieldUnescaped(ReleaseConfig[MSBuildExt.Config.Type.Link])
                    SubSystem(MsBuild.Subsystem[project.type])

                    AdditionalDependencies {
                        mkp.yieldUnescaped((config.release.libs + libs).sum { x -> x + SEMI_COLON })
                    }
                }
            }

            ItemGroup() {
                project.fileTree(dir: config.srcDir)
                        .include(config.hFiles.collect { FILTER_ALL + it })
                        .collect { project.relativePath(it) }
                        .sort()
                        .each {
                    ClInclude(Include: it)
                }
            }

            ItemGroup() {
                project.fileTree(dir: config.srcDir)
                        .include((config.cFiles + config.cppFiles).collect { FILTER_ALL + it })
                        .collect { project.relativePath(it) }
                        .sort()
                        .each {
                    if (it.contains(STDAFX)) {
                        ClCompile(Include: it)
                                {
                                    PrecompiledHeader(CREATE)
                                }
                    }
                    else {
                        ClCompile(Include: it)
                    }
                }
            }

            ItemGroup() {
                project.fileTree(dir: config.resDir)
                        .include(ALL_RC)
                        .collect { project.relativePath(it) }
                        .sort()
                        .each {
                    ResourceCompile(Include: it)
                }

                project.fileTree(dir: config.resDir)
                        .exclude(ALL_RC)
                        .collect { project.relativePath(it) }
                        .sort()
                        .each {
                    None(Include: it)
                }

            }

            Import(Project: MICROSOFT_CPP_TARGETS)

            ImportGroup(Label: EXTENSION_TARGETS, EMPTY)
        }

        projFile.write(writer.toString())
    }
}
