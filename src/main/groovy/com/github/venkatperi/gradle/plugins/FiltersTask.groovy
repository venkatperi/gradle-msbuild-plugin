package com.github.venkatperi.gradle.plugins

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import static java.util.UUID.randomUUID

/**
 * Created by IntelliJ IDEA.
 * User: vperi
 * Date: 3/6/12
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
class FiltersTask extends DefaultTask {

    static final String DEFAULT_EXTENSION = '.vcxproj.filters'
    static final String SCHEMA = 'http://schemas.microsoft.com/developer/msbuild/2003'
    static final String VERSION = '4.0'
    static final String SRC = 'src'

    def config = project.extensions.msproject
    def name = config.name == null ? project.name : config.name
    def n = name + DEFAULT_EXTENSION
    
    @InputDirectory
    def srcDirs = new File(config.srcDir)
    
    @OutputFile
    def filterFile = new File(n)

    @TaskAction
    public void filters() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        def dirList = [SRC]
        new File(SRC).eachDirRecurse() { dir -> dirList.add(project.relativePath(dir.getPath())) }

        xml.Project(ToolsVersion: VERSION, xmlns: SCHEMA) {
            ItemGroup() {
                dirList.each {
                    Filter(Include: it) {
                        UniqueIdentifier('{' + randomUUID() + '}')
                    }
                }
            }

            ItemGroup() {
                project.fileTree(dir: config.srcDir)
                        .include(config.hFiles.collect { '**/*.' + it })
                        .collect { project.relativePath(it) }
                        .sort()
                        .each {
                    mkp.yieldUnescaped("<ClInclude Include=\'" + it + "\'><Filter>" + new File(it).getParent() + "</Filter></ClInclude>")
                }
            }

            ItemGroup() {
                project.fileTree(dir: config.srcDir)
                        .include((config.cFiles + config.cppFiles).collect { '**/*.' + it })
                        .collect { project.relativePath(it) }
                        .sort()
                        .each {
                    mkp.yieldUnescaped("<ClCompile Include=\'" + it + "\'><Filter>" + new File(it).getParent() + "</Filter></ClCompile>")
                }
            }

            ItemGroup() {
                project.fileTree(dir: config.resDir)
                        .include('**/*.rc')
                        .collect { project.relativePath(it) }
                        .sort()
                        .each {
                    mkp.yieldUnescaped("<ResourceCompile Include=\'" + it + "\'><Filter>" + new File(it).getParent() + "</Filter></ResourceCompile>")
                }

                project.fileTree(dir: config.resDir)
                        .exclude('**/*.rc')
                        .collect { project.relativePath(it) }
                        .sort()
                        .each {
                    mkp.yieldUnescaped("<None Include=\'" + it + "\'><Filter>" + new File(it).getParent() + "</Filter></None>")
                }
            }

            filterFile.write(writer.toString())
        }
    }
}