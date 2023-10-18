package xbss.controller

import javafx.application.Platform
import javafx.beans.property.SimpleIntegerProperty
import javafx.concurrent.Task
import xbss.MainAPP
import xbss.ssh.SSH
import xbss.utils.FileCommandPane
import xbss.utils.FileIOPane
import xbss.view.MainWindow

/**
 * @author  Xbss
 * @create 2023-04-25-18:54
 * @version  1.0
 * @describe
 */
class FileCommandTask(private val ssh: SSH,private val mainWindow: MainWindow) {
    private lateinit var type:FileCommandPane.FileCommandType
    private val nowIndex = SimpleIntegerProperty(0)
    private var totalNum :Int = 0
    private val nowStatus = SimpleIntegerProperty(0)  // 状态: 1 等待中，2 初始化，3 下载中，4下载完成，5出错
    private lateinit var downloader: Task<Unit>
    private lateinit var waitToOperatePathList:List<String>
    private lateinit var pastePath:String
    private var sumTitle:String = ""
    constructor(ssh: SSH,mainWindow: MainWindow,waitToCopyPathList: List<String>,cPastePath:String):this(ssh,mainWindow){
        totalNum = waitToCopyPathList.size
        type = FileCommandPane.FileCommandType.COPY
        waitToOperatePathList = waitToCopyPathList
        pastePath = cPastePath
        sumTitle = cPastePath.split("/").last()
        begin()
    }
    constructor(ssh: SSH,mainWindow: MainWindow,waitToDeletePathList: List<String>):this(ssh,mainWindow){
        totalNum = waitToDeletePathList.size
        type = FileCommandPane.FileCommandType.DELETE
        waitToOperatePathList = waitToDeletePathList
//        nowStatus.value = 3
        begin()
    }
    private fun initDownloader() {
        downloader = object : Task<Unit>() {
            var error = false
            override fun call() {
                //像waitToOperatePathList.forEachIndexed这样写在when(type)里面代码会多很多，但是我觉得把判断类型放在里面岂不是每一个元素都要
                //判断后在操作，先进行总的判断会不会更快？
                when(type){
                    FileCommandPane.FileCommandType.DELETE -> {
                        waitToOperatePathList.forEachIndexed{ index,it ->
                            Platform.runLater { nowIndex.value = index + 1 }
                            updateMessage(it.split("/").last())
                            try {
                                mainWindow.writeInfoLog("${javaClass.simpleName}：删除$it(rm -r $it)")
//                                println("进来删除$it")
                                val result = ssh.execCommand("rm -r $it")
                                mainWindow.writeInfoLog("${javaClass.simpleName}：删除${it}结果$result")
                            } catch (e: Exception) {
                                Platform.runLater { nowStatus.value = 5 }
                                println("捕获到异常")
                                error = true
                                println(e.message)
                                updateMessage(e.message)
                                mainWindow.writeErrorLog("${javaClass.simpleName}：$error")
                                return
                            }
                        }
                    }
                    FileCommandPane.FileCommandType.COPY -> {
                        waitToOperatePathList.forEachIndexed{ index,it ->
                            Platform.runLater { nowIndex.value = index + 1 }
                            updateMessage(it.split("/").last())
                            try {
                                mainWindow.writeInfoLog("${javaClass.simpleName}：将{$it}复制到$pastePath(cp -r $it $pastePath)")
                                val result = ssh.execCommand("cp -r $it $pastePath")
                                mainWindow.writeInfoLog("${javaClass.simpleName}：将{$it}复制到${pastePath}结果${result}")

                            } catch (e: Exception) {
                                Platform.runLater { nowStatus.value = 5 }
                                println("捕获到异常")
                                error = true
                                println(e.message)
                                updateMessage(e.message)
                                mainWindow.writeErrorLog("${javaClass.simpleName}：$error")
                                return
                            }
                        }
                    }
                    else -> {}
                }
            }
            /**
             *  有时上面出错捕获到异常了，会直接进到succeeded，需要在面板里面处理一下
             */
            override fun succeeded() {
                Platform.runLater {
                    nowStatus.value = 4
                    when(type){
                        FileCommandPane.FileCommandType.DELETE ->  mainWindow.treeArea.refreshFatherItem()
                        FileCommandPane.FileCommandType.COPY ->  mainWindow.treeArea.refreshItem()
                    }
                    mainWindow.writeInfoLog("${javaClass.simpleName}完成")
                }
            }
            override fun failed() {
                updateMessage(exception.message)
                println("任务出错了,$error")
                Platform.runLater { nowStatus.value = 5 }
                mainWindow.writeErrorLog("${javaClass.simpleName}：$error")
            }
        }
        mainWindow.addMessage(FileCommandPane(type, sumTitle, downloader.messageProperty(), nowIndex, totalNum,nowStatus))
    }
    private fun begin(){
        initDownloader()
        MainAPP.service.submit {
            Platform.runLater { nowStatus.value = 3 }
            Thread(downloader).start()
        }
    }
}