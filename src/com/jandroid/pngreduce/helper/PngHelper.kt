package com.jandroid.pngreduce.helper

import com.intellij.a.g.Log
import com.intellij.openapi.application.PathManager
import com.jandroid.pngreduce.bean.PngReduceBean
import java.io.File
import java.nio.charset.Charset


/**
 * app_key的存储地址
 */
val FILE_PATH_APP_KEY = "${PathManager.getPluginsPath()}/pngreduce/apiKey.csv"

/**
 * 输入的图片地址
 */
val FILE_PATH_INPUT_DIRS= "${PathManager.getPluginsPath()}/pngreduce/input_dirs.csv"

/**
 * 输出的图片路径
 */
val FILE_PATH_OUTPUT_DIRS = "${PathManager.getPluginsPath()}/pngreduce/output_dirs.csv"

/**
 * 前缀
 */
val FILE_PATH_PREFIX= "${PathManager.getPluginsPath()}/pngreduce/prefix.csv"


/**
 * 检测App key文件
 */
fun checkApiKeyFile(inexistAction : ((String) -> Unit) ?= null, existAction : ((String) -> Unit) ?= null) = with(File(FILE_PATH_APP_KEY)){
    System.out.println("file path :"+toString())
    if(!exists() || readText(Charset.defaultCharset()).isNullOrBlank()){
        return@with inexistAction?.invoke("请输入TinyPng Key, 请往TinyPng官网申请")
    }else {
        return@with existAction?.invoke(readText(Charset.defaultCharset()))
    }
}

/**
 * 使用的是拓展函数,更新App_key
 */
fun updateExpireApiKey(apiKey : String) = File(FILE_PATH_APP_KEY).createFile {
    if(it.readText(Charset.defaultCharset()) != apiKey){
        it.writeText(apiKey, Charset.defaultCharset())
    }
}


/**
 * 读取本地缓存了用户使用过的输入，输出目录的文件
 */
fun readUserDirs() : Pair<List<String>,List<String>>{
    val inputDirs = mutableListOf<String>()
    val inputFile = File(FILE_PATH_INPUT_DIRS)

    val outputDirs = mutableListOf<String>()
    val outputFile = File(FILE_PATH_OUTPUT_DIRS)

    if(inputFile.exists()){
        inputDirs.addAll(inputFile.readLines(Charset.defaultCharset()))
    }
    if(outputFile.exists()){
        outputDirs.addAll(outputFile.readLines(Charset.defaultCharset()))
    }

    return inputDirs to outputDirs
}

/**
 * 存储用户数据
 */
fun saveUserDirs(bean : PngReduceBean){
    bean?.let {
        File(FILE_PATH_INPUT_DIRS).createFile {  if (!it.readLines(Charset.defaultCharset()).contains(bean.inputDir)) {
            it.appendText("${bean.inputDir}\n", Charset.defaultCharset())
        } }
        File(FILE_PATH_OUTPUT_DIRS).createFile {  if (!it.readLines(Charset.defaultCharset()).contains(bean.outputDir)) {
            it.appendText("${bean.outputDir}\n", Charset.defaultCharset())
        } }}
}


/**
 * 读取前缀
 */
fun readUserFilePrefix() : List<String> = with(File(FILE_PATH_PREFIX)){
    if(exists()) readLines(Charset.defaultCharset()) else listOf()
}


/**
 * 写入用户当前使用的文件前缀到缓存文件中
 */
fun saveUsedFilePrefix(filePrefix: String) = File(FILE_PATH_PREFIX).createFile {
    if (!it.readLines(Charset.defaultCharset()).contains(filePrefix)) {
        it.appendText("$filePrefix\n", Charset.defaultCharset())
    }
}


/**
 * 取用户输入目录下的所有图片文件
 */
//
fun readInputDirFiles(inputDir: String): List<File> {
    val inputFiles: List<String> = inputDir.split(",")
    if (inputFiles.isEmpty()) {
        return listOf()
    }

    if (inputFiles.size == 1) {
        val inputFile = File(inputFiles[0])
        if (inputFile.isFile) {
            return listOf(inputFile).filterPng()
        }

        if (inputFile.isDirectory) {
            return inputFile.listFiles().toList().filterPng()
        }
    }

    return inputFiles.map { File(it) }.filterPng()
}

/**
 * 过滤图片的过滤器
 */
fun List<File>.filterPng() : List<File> = this.filter { it.name.endsWith(".png") || it.name.endsWith(".jpg") || it.name.endsWith(".jpeg") }


/**
 * 拓展File的函数
 */
fun File.createFile(createFile : ((File) -> Unit)? = null){
    if(!exists()){
        File(parent).mkdirs()
        createNewFile()
    }
    createFile?.invoke(this)
}

