package xbss.controller

import javafx.application.Platform
import xbss.MainAPP
import xbss.config.TextColor
import xbss.ssh.SSH
import xbss.utils.FileIOPane
import xbss.view.MainWindow

/**
 * @author  Xbss
 * @create 2023-03-29-13:46
 * @version  1.0
 * @describe
 */
class FileTaskHandler(private val mainWindow: MainWindow) {
    private val ssh = mainWindow.ssh
    private var sftpNum = ssh.sftpAvailableNum()
    private val taskList = mutableListOf<FileIOTask>()
    init {
        sftpNum.addListener { _,_,newValue ->
//            println("在FileTaskHandler中监听到了当前可用通道为$newValue")
            if (newValue.toInt()>1&&taskList.isNotEmpty()){
                val fileTask = taskList.removeFirst()
                fileTask.start(ssh.getChSftp())
            }
        }
        ssh.successP.addListener{ _, _, _ -> sftpNum = ssh.sftpAvailableNum() }
    }

    /**
     * 添加一个上传、下载任务，需要使用到sftp的通道
     * @param type
     * @param fromPath
     * @param toPath
     */
    fun addIOTask(type: FileIOPane.FileIOType, fromPath:String, toPath:String){
        if (ssh.sftpIsAvailable())
            FileIOTask(ssh.getChSftp(),type,mainWindow,fromPath,toPath)
        else
            taskList.add(FileIOTask(type,mainWindow,fromPath,toPath))
    }

    /**
     * 添加一个复制、删除的任务，不需要使用sftp，直接使用linux命令就可以做到
     * @param waitToOperatePathList
     * @param cPastePath
     */
    fun addCommandTask(waitToOperatePathList: List<String>, cPastePath:String? = null){
        cPastePath?.let {
            FileCommandTask(ssh, mainWindow, waitToOperatePathList, it)
        }?:run {
            FileCommandTask(ssh, mainWindow, waitToOperatePathList)
        }
    }
}