package com.deep

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import proguard.gradle.ProGuardTask

//import org.gradle.jvm.tasks.Jar
public class DeepLibraryPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("deep", DeepLibraryExtension)
        if (project['deep']!=null){
            println("wwwwwww")
            project.deep.extensions.create('jar', JarExtension)
            project.deep.extensions.create('aar', AARExtension)
        }

        project.afterEvaluate {
            DeepLibraryExtension extension = project['deep']


            if (extension!=null){
                    println("dddddddddd")
                    JarExtension jar= project.deep.jar
                    AARExtension aar = project.deep.aar
                println("jar="+jar)
                println("aar="+aar)
                if (aar!=null){
                   controlAAR(aar,project)
                }
                if (jar!=null){
                    controlJar(jar,project)
                }
            }

//            String a = extension.aaa
//            String b = extension.bbb
//            println("deep:${a},${b}")



        }

    }


def controlJar(JarExtension extension, Project project){
    String name
    if (extension.name instanceof String){
        name = extension.name
    }
    String path
    if (extension.name instanceof String){
        path = extension.path
        if (path!=null&&path.length()>0){
            if (path.startsWith("/")){
                path = path.substring(1);
            }
            if (path.endsWith("/")){
                path = path.substring(0,path.length()-2);
            }
        }
    }
    def excludeClass = extension.excludeClass
    def excludePackage = extension.excludePackage
    def includePackage = extension.includePackage
    boolean needDefaultProguard = extension.isProguard
    project.android.libraryVariants.all { variant ->
        def dJar = project.tasks.create("dJar"+variant.name.capitalize(), Jar)
        dJar.doFirst {
            println("sssssssssss")
            String srcClassDir =  project.buildDir.absolutePath + "/intermediates/classes/"+variant.getBuildType().getName()
            from srcClassDir
            println("srcClassDir:"+srcClassDir)
            if (excludeClass != null && excludeClass.size() > 0) {
                excludeClass.each {
                    //排除指定class
                    dJar.exclude(it)
                }

            }
            if (excludePackage != null && excludePackage.size() > 0) {
                excludePackage.each {
                    //过滤指定包名下class
                    dJar.exclude("${it}/**/*.class")
                }

            }
            if (includePackage != null && includePackage.size() > 0) {
                includePackage.each {
                    //仅仅打包指定包名下class
                    dJar.include("${it}/**/*.class")
                }

            } else {
                //默认全项目构建jar
                dJar.include("**/*.class")

            }

            String aName = name
            if (path!=null&&name!=null){
                aName =  aName.replace("{name}",project.name)
                aName =  aName.replace("{type}",variant.getBuildType().getName())
                aName = aName.replace("{flavor}",variant.getFlavorName())
                archiveName = aName
            }
        }
        def proguardJar = project.tasks.create("proguardJar"+variant.name.capitalize(),ProGuardTask)
        proguardJar.doFirst {
            if (needDefaultProguard) {
                proguardJar.configuration(project.android.getDefaultProguardFile('proguard-android.txt'))
            }
            String inJar = dJar.archivePath.getAbsolutePath()
            injars inJar
            String outJar = "${project.getProjectDir().absolutePath}/${path}/${aName}"
            outjars outJar
        }
        proguardJar.dependsOn dJar
       def compile = project.tasks.getByName("assembleDebug")
        compile.dependsOn proguardJar

    }


}

    static def controlAAR(AARExtension aar, Project project){

    String name
    if (aar.name instanceof String){
        name = aar.name
    }
    String path
    if (aar.name instanceof String){
        path = aar.path
        if (path!=null&&path.length()>0){
            if (path.startsWith("/")){
                path = path.substring(1);
            }
            if (path.endsWith("/")){
                path = path.substring(0,path.length()-2);
            }
        }

    }
    Map<String,String> holder
    if (aar.holder instanceof Map<String,String>){
        holder = aar.holder
    }
    project.tasks.getByName("preBuild") {
        it.doFirst {
            project.android.libraryVariants.all { variant ->
                variant.outputs.each { output ->
                    String aName = name
                    if (path!=null&&name!=null){
                        aName =  aName.replace("{name}",project.name)
                        aName =  aName.replace("{type}",variant.getBuildType().getName())
                        aName = aName.replace("{flavor}",variant.getFlavorName())
                        output.setOutputFile(new File("${project.getProjectDir().absolutePath}/${path}/${aName}"))
                    }
                    if (holder!=null){
                        output.processManifest.doLast {
                            def manifestFile = "${project.getProjectDir().absolutePath}/build/intermediates/manifests/aapt/${variant.dirName}/AndroidManifest.xml"
                            def updatedContent = new File(manifestFile).getText('UTF-8')
                            for (String key:holder.keySet()){
                                updatedContent.replaceAll(key, holder.get(key).toString())
                            }
                            new File(manifestFile).write(updatedContent, 'UTF-8')
                        }
                    }

                }


            }

        }

    }
}

}