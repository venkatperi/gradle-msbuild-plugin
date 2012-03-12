package com.github.venkatperi.gradle.plugins

import com.github.venkatperi.groovy.utils.Path
import groovy.xml.StreamingMarkupBuilder
import org.gradle.api.Project
import org.gradle.api.tasks.StopActionException

/**
 * Created by IntelliJ IDEA.
 * User: vperi
 * Date: 3/6/12
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
class MSBuildExt {
    private static final String CPP = 'cpp'
    private static final String CXX = 'cxx'
    private static final String HPP = 'hpp'
    private static final String H = 'h'
    private static final String C = 'c'
    private static final String RC = 'rc'
    private static final String SRC = 'src'
    private static final String MAIN = 'main'
    private static final String RES = 'res'
    private static final String TEST = 'test'
    private static final String TMP = 'tmp'
    private static final String CAR = 'car'
    private static final String OUT = 'out'
    private static final String INT = 'int'
    private static final String SHORT_NAME = 'shortName'
    private static final String ARCH = 'arch'
    private static final String COMPILER = 'compiler'
    private static final String CRT = 'crt'
    private static final String TYPE = 'type'
    private static final String HYPHEN = '-'
    private Project project

    String name

    private def buildDir

    private def src
    private def main
    private def cpp
    private def res
    private def test
    private def tmp
    private def car
    private def configuration
    private def bld
    private def outp
    private def intp

    String baseSrcDir
    String outDir
    String intDir
    String srcDir
    String resDir
    String testDir
    String tmpDir
    String stageDir
    boolean usePrecompiledHeaders = true

    List cppFiles = [CPP, CXX]
    List cFiles = [C]
    List hFiles = [H, HPP]
    List rcFiles = [RC]
    List resFiles = ['bmp', 'ico', 'jpg', 'png']

    Config global = new Config()
    Config configuration = new Config()
    Config debug = new Config()
    Config release = new Config()

    List<String> incDirs = []
    List<String> cxxFlags = []
    List<String> linkFlags = []
    List<String> libDirs = []
    List<String> libs = []

//    String archiveName
    Object archiveCS
//    String manifestStr
    String archiveBaseName
    String outputName

    class Config {

        enum Type {
            Configuration, General, Compile, Link
        }

        HashMap<Type, String> config = new HashMap<Type, String>()

        List incDirs = []
        List libDirs = []
        List defines = []
        List libs = []

        void configuration(Closure c) {
            config[Type.Configuration] =
                new StreamingMarkupBuilder().bind(c).toString()
        }

        void general(Closure c) {
            config[Type.General] =
                new StreamingMarkupBuilder().bind(c).toString()
        }

        void compile(Closure c) {
            config[Type.Compile] =
                new StreamingMarkupBuilder().bind(c).toString()
        }

        void link(Closure c) {
            config[Type.Link] =
                new StreamingMarkupBuilder().bind(c).toString()
        }
    }

    def verify(Set<?> set, String value, String type) {
        if (!set.find { it == value })
            throw new StopActionException('Invalid ' + type + ': ' + value + ' or not set. Must be one of ' + set.sum { "'" + it.toString() + "' "})
    }

    def MSBuildExt(Project p) {
        this.project = p

        verify MsBuild.Crt.keySet(), project.crt, 'C-Runtime'
        verify MsBuild.Build.keySet(), project.buildConfig, 'Build Configuration'
        verify MsBuild.Type.keySet(), project.type, 'Output Type'
        verify MsBuild.Arch.keySet(), project.arch, 'Architecture'
        verify MsBuild.Call.keySet(), project.call, 'Calling Convention'
        verify MsBuild.Compiler.keySet(), project.compiler, 'Compiler'

        buildDir = project.buildDirName

        baseSrcDir = SRC
        src = new Path(baseSrcDir)
        main = new Path(MAIN)
        cpp = new Path(CPP)
        res = new Path(RES)
        test = new Path(TEST)
        tmp = new Path(TMP)
        car = new Path(CAR)
        outp = new Path(OUT)
        intp = new Path(INT)
        bld = new Path(buildDir)

        outDir = bld / outp
        intDir = bld / intp
        srcDir = src / main / cpp
        resDir = src / main / res
        testDir = src / main / test
        tmpDir = bld / tmp
        stageDir = bld / tmp / car

        if (!project.hasProperty(SHORT_NAME))
            project.shortName = project.name

        if (!project.hasProperty(COMPILER))
            project.compiler = MsBuild.VC10

        project.archivesBaseName = project.shortName + [project.arch, project.compiler,
                project.crt, project.call, project.buildConfig.toLowerCase(), project.type].sum { HYPHEN + it }

        outputName = project.shortName + [project.arch, project.compiler,
                project.crt, project.call, project.buildConfig.toLowerCase()].sum { HYPHEN + it }
    }

    /**
     *   archival specification+
     * @param cs
     */
    void archiveSpec(Closure cs) {
        archiveCS = project.copySpec(cs)
    }

}
