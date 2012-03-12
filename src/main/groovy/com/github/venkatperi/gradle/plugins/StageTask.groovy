package com.github.venkatperi.gradle.plugins

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by IntelliJ IDEA.
 * User: vperi
 * Date: 3/6/12
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
class StageTask extends DefaultTask {

    def config = project.extensions.msproject

    StageTask() {
        inputs.dir config.outDir
        outputs.dir config.stageDir
    }

    @TaskAction
    def stage() {
        project.mkdir config.stageDir

        project.copy {
            with config.archiveCS
            into config.stageDir
        }

    }
}
