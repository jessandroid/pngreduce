package com.jandroid.pngreduce.helper

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.jandroid.pngreduce.bean.PngReduceBean
import com.jandroid.pngreduce.extension.otherwise
import com.jandroid.pngreduce.extension.yes
import com.tinify.Tinify
import java.io.File

/**
 *
 *一些工具方法。主要包含文件传输、APP_KEY的初始化
 */

/**
 * 初始化Tin Key
 */
fun initTinAppKey(apiKey : String){
    apiKey?.let { Tinify.setKey(apiKey) }
}


/**
 * 检查Api key的合法。通过网络请求
 */
fun checkApiKeyValid(project: Project?,apiKey:String,validAction : (()->Unit) ?= null ,invalidAction : ((String) -> Unit)){
    if(apiKey.isNullOrBlank()){
        invalidAction.invoke("TinyPng key为空，请重新输入")
    }
    project?.asyncTask("正在检查key是否合法",runAction = {
        try {
            Tinify.setKey(apiKey)
            Tinify.validate()
        } catch (exception: Exception) {
            throw exception
        }
    },successAction = {
        validAction?.invoke()
    },failAction = {
        println("验证Key失败!!${it.message}")
        invalidAction?.invoke("TinyPng key验证失败，请重新输入")
    })
}


/***
 * 裁剪图片
 */
fun slimImage(project: Project?, inputFiles: List<File>,
        model: PngReduceBean = PngReduceBean("", "", ""), successAction: (() -> Unit)? = null,
        outputSameFile: Boolean = false, failAction: ((String) -> Unit)? = null) {
    project?.asyncTask(hintText = "正在压缩", runAction = {
        //执行图片压缩操作
        outputSameFile.yes { inputFiles.forEach { inputFile -> Tinify.fromFile(inputFile.absolutePath).toFile(inputFile.absolutePath) } }
                .otherwise { inputFiles.forEach { inputFile -> Tinify.fromFile(inputFile.absolutePath).toFile(getDestFilePath(model, inputFile.name))}}
    }, successAction = {
        successAction?.invoke()
    }, failAction = {
        failAction?.invoke("TinyPng key存在异常，请重新输入")
    })
}

/**
 * 目标文件名称
 */
private fun getDestFilePath(model: PngReduceBean, sourceName: String): String {
    return "${model.outputDir}/$sourceName"
}


/**
 * 拓展出来异步执行的方法
 */
fun Project.asyncTask(hintText: String, runAction: (ProgressIndicator) -> Unit, successAction: (() -> Unit)? = null,
                      failAction: ((Throwable) -> Unit)? = null, finishAction: (() -> Unit)? = null){
    object : Task.Backgroundable(this,hintText){
        override fun run(p0: ProgressIndicator) {
            runAction?.invoke(p0)
        }

        override fun onSuccess() {
            super.onSuccess()
            successAction?.invoke()
        }

        override fun onThrowable(error: Throwable) {
            super.onThrowable(error)
            failAction?.invoke(error)
        }

        override fun onFinished() {
            super.onFinished()
            finishAction?.invoke()
        }
    }.queue()
}


