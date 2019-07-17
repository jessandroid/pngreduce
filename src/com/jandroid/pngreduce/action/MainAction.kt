package com.jandroid.pngreduce.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.jandroid.pngreduce.bean.PngReduceBean
import com.jandroid.pngreduce.extension.showDialog
import com.jandroid.pngreduce.helper.*
import com.jandroid.pngreduce.ui.ImageSlimmingDialog
import com.jandroid.pngreduce.ui.InputKeyDialog

/**
 * 入口Action
 */
class MainAction : AnAction(){
    override fun actionPerformed(p0: AnActionEvent) {
        checkApiKeyFile(inexistAction = {
            popupInputKeyDialog(it,p0)
        },existAction = {
            System.out.println("previous app key: "+it)
            initTinAppKey(it)
            popupCompressDialog(p0)
        })
    }



    //弹出输入apiKey dialog
    private fun popupInputKeyDialog(labelTitle: String, event: AnActionEvent?) {
        InputKeyDialog(labelTitle, object : InputKeyDialog.DialogCallback {
            override fun onOkBtnClicked(tinyPngKey: String) = checkApiKeyValid(project = getEventProject(event), apiKey = tinyPngKey, validAction = {
                updateExpireApiKey(apiKey = tinyPngKey)
                popupCompressDialog(event)
            }, invalidAction = {
                popupInputKeyDialog(labelTitle = it, event = event)
            })

            override fun onCancelBtnClicked() {

            }
        }).showDialog(width = 530, height = 150, isInCenter = true, isResizable = false)
    }

    //弹出压缩目录选择 dialog
    private fun popupCompressDialog(event: AnActionEvent?) {
        ImageSlimmingDialog(readUserDirs(), readUserFilePrefix(), object : ImageSlimmingDialog.DialogCallback {
            override fun onOkClicked(imageSlimmingModel: PngReduceBean) {
                saveUserDirs(imageSlimmingModel)
                saveUsedFilePrefix(imageSlimmingModel.prefix)
                val inputFiles = readInputDirFiles(imageSlimmingModel.inputDir)
                val startTime = System.currentTimeMillis()
                slimImage(project = getEventProject(event), inputFiles = inputFiles, model = imageSlimmingModel, successAction = {
                    Messages.showWarningDialog("压缩完成, 已压缩: ${inputFiles.size}张图片, 压缩总时长共计: ${(System.currentTimeMillis() - startTime) / 1000}s",
                            "来自ImageSlimming提示")
                }, failAction = {
                    popupInputKeyDialog(labelTitle = it, event = event)
                })
            }

            override fun onCancelClicked() {

            }

        }).showDialog(width = 550, height = 200, isInCenter = true, isResizable = false)
    }

}