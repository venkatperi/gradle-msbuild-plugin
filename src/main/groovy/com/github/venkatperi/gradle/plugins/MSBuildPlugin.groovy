package com.github.venkatperi.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import static java.util.UUID.randomUUID

/**
 * msbuild plugin
 */
class MSBuildPlugin implements Plugin<Project> {

    private static final String STRUCTURE = 'structure'
    private static final String PROJECT_GUID = 'projectGuid'
    private static final String VCXPROJ = 'vcxproj'
    private static final String CONFIGURE = 'configure'
    private static final String FILTERS = 'filters'
    private static final String LIBS = 'libs'
    private static final String COMPILE = 'compile'
    private static final String BUILD = 'build'
    private static final String STAGE = 'stage'
    private static final String CAR = 'car'
    private static final String ASSEMBLE = 'assemble'
    MSBuildExt config

    /**
     *
     * @param project
     */
    def void apply(Project project) {
        project.extensions.msproject = new MSBuildExt(project)
        config = project.extensions.msproject

        //check if our consumer has set project properties
        if (!project.hasProperty(PROJECT_GUID)) {
            project.setProperty(PROJECT_GUID, '{' + randomUUID() + '}')
        }

        def structure = project.task(STRUCTURE, type: StructureTask) << {
            description 'Create project directory structure'
            group CONFIGURE
        }

        def vcxproj = project.task(VCXPROJ, type: VcxProjTask, dependsOn: [STRUCTURE]) << {
            description 'Generate main (vcxproj) project file'
            group CONFIGURE
        }

        project.task(FILTERS, type: FiltersTask, dependsOn: VCXPROJ) << {
            description 'Generate project filters file (reflecting code hierarchy)'
            group CONFIGURE
        }

        project.task(CONFIGURE, dependsOn: [STRUCTURE, VCXPROJ, FILTERS]) << {
            description 'All project configuration tasks including structure, vcxproj and filters'
            group CONFIGURE
        }

        project.task(COMPILE, type: CompileTask, dependsOn: CONFIGURE) << {
            description = 'Compiles and links production C++ source files using msdev'
            group BUILD
        }

        def stage = project.task(STAGE, type: StageTask, dependsOn: COMPILE) << {
            description 'stage files for archiving'
            group BUILD
        }

        project.task(BUILD, dependsOn: [ASSEMBLE]) << {
            description 'Performs a full build of the project.'
            group BUILD
        }

        try {
            def l = project.tasks.getByName(LIBS)  //see if cars plugin is available
            l.dependsOn 'structure'
            vcxproj.dependsOn 'libs'
            vcxproj.inputs.files.add l.outputs.files
        }
        catch (e) {
        }

        try {
            def car = project.tasks.getByName(CAR)
            car.dependsOn 'stage'
        }
        catch (e) {
        }
    }
}


