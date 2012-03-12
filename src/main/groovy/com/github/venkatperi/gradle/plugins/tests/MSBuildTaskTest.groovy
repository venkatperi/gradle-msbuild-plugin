package com.github.venkatperi.gradle.plugins.tests

import com.github.venkatperi.gradle.plugins.MSBuildPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

// START SNIPPET test-task
class MSBuildTaskTest {
    @Test
    public void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.buildConfig = 'Debug'
        project.apply 'plugin': 'maven'

//        project.apply 'plugin': CarPlugin

        project.usePlugin MSBuildPlugin
        def config = project.extensions.msproject
        config.global.general {
            ProjectGuid('xxx')
            Keyword('Win32Proj')
            RootNamespace(project.name)
        }


        config.global.configuration {
            PlatformToolset('v100')
            //CharacterSet('Unicode')
        }

        config.global.compile {
            //PrecompiledHeader('Use')
            CallingConvention('Cdecl')
        }


        config.debug.configuration {
            UseDebugLibraries('true')
        }

        config.debug.general {
            LinkIncremental('true')
        }

        config.debug.compile {
            WarningLevel('Level3')
            Optimization('Disabled')
        }

        config.debug.link {
            SubSystem('Windows')
            GenerateDebugInformation('true')
        }

        config.release.configuration {
            UseDebugLibraries('false')
            WholeProgramOptimization('true')
            //CharacterSet('Unicode')
        }

        config.release.general {
            LinkIncremental('false')
        }

        config.release.compile {
            WarningLevel('Level3')
            Optimization('MaxSpeed')
            FunctionLevelLinking('true')
            IntrinsicFunctions('true')
        }

        config.release.link {
            SubSystem('Windows')
            GenerateDebugInformation('true')
            EnableCOMDATFolding('true')
            OptimizeReferences('true')
        }

        config.archiveSpec {}

        project.vcxproj.execute()

//        def task = project.task('msbuild', type: MSBuildTask)
//        assertTrue(task instanceof MSBuildTask)
    }
}
// END SNIPPET test-task
