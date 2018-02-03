package com.deep

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState



//自定义的log输出
class DeepLogger implements BuildListener, TaskExecutionListener {
    private boolean display;
    public DeepLogger(boolean display){
        this.display = display
    }
    @Override
    void buildStarted(Gradle gradle) {
        if(display){
            println "buildStarted"
        }

    }

    @Override
    void settingsEvaluated(Settings settings) {
        if(display){
        println "settingsEvaluated"
        }
    }

    @Override
    void projectsLoaded(Gradle gradle) {
        if(display){
        println "projectsLoaded"
        }
    }

    @Override
    void projectsEvaluated(Gradle gradle) {
        if(display){

        println "projectsEvaluated"
        }
    }

    public void beforeExecute(Task task) {
        if(display){

        println("${task.project.name}工程的${task.name}任务开始执行")
//        println "beforeExecute:[$task.name] 工程名:${task.project.name}"
        }
    }

    public void afterExecute(Task task, TaskState state) {
        if(display){
//            println("${task.project.name}工程的${task.name}任务结束")
//        println "afterExecute:[$task.name] 工程名:${task.project.name}"
        }
    }

    public void buildFinished(BuildResult result) {
        if(display){

        println 'buildFinished'
        if (result.failure != null) {
            result.failure.printStackTrace()
        }
        }
    }
}