package com.github.venkatperi.gradle.plugins

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by IntelliJ IDEA.
 * User: vperi
 * Date: 3/6/12
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
class StructureTask extends DefaultTask {
    def config = project.extensions.msproject
    def dirList = [config.srcDir, config.resDir, config.testDir, config.tmpDir, config.stageDir]

    def StructureTask() {
        dirList.each { outputs.file new File(it) }
    }

    @TaskAction
    public void structure() {
        dirList.each {
            def f = new File(it)

            if (!f.exists()) {
                project.mkdir it
            }
        }

    }
}
