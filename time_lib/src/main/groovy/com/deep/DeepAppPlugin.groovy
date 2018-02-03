package com.deep

import org.gradle.api.Plugin
import org.gradle.api.Project

public class DeepAppPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("deep", DeepAppExtension)
        project.afterEvaluate {
            DeepAppExtension extension = project['deep']
            String name;
            if (extension.name instanceof String){
                name = extension.name
            }
            String path;
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
            Map<String,String> holder;
            if (extension.name instanceof Map<String,String>){
                holder = extension.holder
            }


            project.tasks.getByName("preBuild") {
                it.doFirst {
                    project.android.applicationVariants.all { variant ->
//                        println("1111"+variant.getName()+"       getFlavorName="+variant.getFlavorName())
                        variant.outputs.each { output ->
//                                println("getDirName："+output.getDirName())
                            String aName = name;
                            if (path!=null&&name!=null){
                                aName =  aName.replace("{name}",project.name)
                                aName =  aName.replace("{type}",variant.getBuildType().getName())
                                aName = aName.replace("{flavor}",variant.getFlavorName())
                                output.setOutputFile(new File("${project.getProjectDir().absolutePath}/${path}/${aName}"))
                            }
//                                println("out put=" + output.getOutputFile())
                            if (holder!=null){
                                output.processManifest.doLast {
                                    def manifestFile = "${project.getProjectDir().absolutePath}/build/intermediates/manifests/full/${variant.dirName}/AndroidManifest.xml"
                                    def updatedContent = new File(manifestFile).getText('UTF-8')

                                    for (String key:holder.keySet()){
                                        updatedContent.replaceAll(key, holder.get(key))
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


}