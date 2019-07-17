package com.jandroid.pngreduce.bean

/**
 * 封装数据对象
 * 图片原始路径
 * 图片输出路径
 * 图片输出前缀
 */
data class PngReduceBean(val inputDir : String,val outputDir : String ,val prefix : String )