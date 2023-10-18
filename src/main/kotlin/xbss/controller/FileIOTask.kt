package xbss.controller

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.SftpProgressMonitor
import javafx.application.Platform
import javafx.beans.property.SimpleIntegerProperty
import javafx.concurrent.Task
import xbss.MainAPP
import xbss.utils.FileIOPane
import xbss.view.MainWindow
import java.io.File

/**
 * @author  Xbss
 * @create 2023-03-29-13:48
 * @version  1.0
 * @describe fromPath 从服务器下载到 toPath 本地地址
 *           fromPath 从本地地址上传到 toPath 服务器
 */
class FileIOTask(private val type:FileIOPane.FileIOType, private val mainWindow: MainWindow, private val fromPath:String, private val toPath:String) {
    private lateinit var sftp:ChannelSftp
    /**
     * 下载\上传的文件夹名
     */
    private val sumTitle = fromPath.split("/").last()
    private val nowIndex = SimpleIntegerProperty(0)
    private val totalNum = SimpleIntegerProperty(0)
    private val nowStatus = SimpleIntegerProperty(0)  // 状态: 1 等待中，2 初始化，3 下载中，4下载完成，5出错
    /**
     * 下载状态下 第一个是服务器地址，第二个是本地地址
     * 上传状态下 第一个是本地文件地址 第二个是服务器地址
     */
    private lateinit var finalList:MutableList<Pair<String,String>> // 下载状态下 第一个是服务器地址，第二个是本地地址
    private val downloader: Task<Unit>
    /**
     *  有空闲的管道直接开始任务，没有就等着外面传管道调用start
     */
    constructor(sftpS:ChannelSftp,type:FileIOPane.FileIOType,mainWindow: MainWindow,fromPath:String,toPath:String):this(type,mainWindow,fromPath,toPath){
        sftp = sftpS
        begin()
    }
    init {
        downloader = object : Task<Unit>() {
            var error = false
            val monitor = object: SftpProgressMonitor {
                private var transferred = 0L
                private var filesize = 0L
                private var percent = 0
                override fun init(op: Int, src: String?, dest: String?, max: Long) {
                    transferred = 0L
                    filesize = max
                    percent = 0
                }
                override fun count(bytes: Long): Boolean {
                    transferred += bytes
                    val newPercent = (transferred * 100 / filesize).toInt()
                    if (newPercent != percent) {
                        percent = newPercent
                        Platform.runLater { updateProgress(percent.toDouble(), 100.0) }
                    }
                    return true
                }
                override fun end() {
                    percent = 100
                    Platform.runLater { updateProgress(percent.toDouble(), 100.0) }
                }
            }
            override fun call() {
                // (F:\loader.py, /data1/pjf/Test)
                finalList.forEachIndexed { index, it ->
                    Platform.runLater { nowIndex.value = index + 1 }
                    try {
                        //这里是在更新当前操作的文件名
                        if (type==FileIOPane.FileIOType.DOWN){
                            updateMessage(it.first.split("/").last()) //linux
                            sftp.get(it.first, it.second,monitor)
                            mainWindow.writeInfoLog(javaClass.simpleName+"将${it.first}下载到${it.second}")
                        }
                        else{
                            updateMessage(it.first.split("\\").last()) //windows
                            sftp.put(it.first,it.second,monitor)
                            mainWindow.writeInfoLog(javaClass.simpleName+"将${it.first}上传到${it.second}")
                        }
//                        if (type==FileIOPane.FileIOType.DOWN)
//                            sftp.get(it.first, it.second,monitor)
//                        else
//                            sftp.put(it.first,it.second,monitor)
                    } catch (e: Exception) {
                        Platform.runLater { nowStatus.value = 5 }
                        mainWindow.ssh.releaseChannel(sftp)
                        println("捕获到异常")
                        error = true
                        println(e.message)
                        mainWindow.writeErrorLog("${javaClass.simpleName}：$error ${e.message}")
                        updateMessage(e.message)
                        return
                    }
                }
            }

            /**
             *  有时上面出错捕获到异常了，会直接进到succeeded，需要处理一下
             */
            override fun succeeded() {
                Platform.runLater { nowStatus.value = 4 }
                mainWindow.ssh.releaseChannel(sftp)
                mainWindow.treeArea.refreshItem()
                mainWindow.writeInfoLog("${javaClass.simpleName}完成")
            }

            override fun failed() {
                updateMessage(exception.message)
                println("任务出错了,$error")
                Platform.runLater { nowStatus.value = 5 }
                mainWindow.ssh.releaseChannel(sftp)
                mainWindow.writeErrorLog("FileIOTask：$error")
            }
        }
        mainWindow.addMessage(FileIOPane(type, sumTitle, downloader.messageProperty(), nowIndex, totalNum, downloader.progressProperty(), toPath,nowStatus))
    }
    fun start(sftpS:ChannelSftp){
        sftp = sftpS
        begin()
    }
    private fun begin(){
        MainAPP.service.submit {
            Platform.runLater { nowStatus.value = 2 }
            val planToDown = mutableListOf<Pair<String,String>>()
            finalList = if (type==FileIOPane.FileIOType.DOWN)
                recursiveLAllFile(fromPath, toPath, planToDown)
            else
                recursiveWAllFile(fromPath, toPath, planToDown)
            Platform.runLater {
                totalNum.value = finalList.size
                nowStatus.value = 3
            }
            Thread(downloader).start()
        }
    }

    /**  for：Linux
     *   遍历该路径下所有文件及文件夹，加到fileList里面
     * @param pathToRecursive ：需要遍历的文件（夹）
     * @param pathToSave ：需要下载到本地的位置
     * @param fileList ：一个MutableList<Pair<String,String>>)，value1是文件路径，2是要下载到的本地路径
     * @return 一个MutableList<Pair<String,String>>)，value1是文件路径，2是要下载到的本地路径
     */
    private fun recursiveLAllFile(pathToRecursive:String, pathToSave:String, fileList:MutableList<Pair<String,String>>):MutableList<Pair<String,String>>{
        if (!sftp.stat(pathToRecursive).isDir){
            fileList.add(Pair(pathToRecursive,pathToSave))
            return fileList
        }else{
            val fileName = pathToRecursive.split("/").last()
            val newToPath = pathToSave + File.separator + fileName
            File(newToPath).mkdirs()
            //遍历出来有两个文件夹 . 和 ..  需要去掉
            (sftp.ls(pathToRecursive) as List<ChannelSftp.LsEntry>).filter { it.filename!="."&&it.filename!=".." }.forEach {
                recursiveLAllFile(pathToRecursive+"/"+it.filename,newToPath,fileList)
            }
        }
        return fileList
    }
    /**   for：Windows
     *   遍历该路径下所有文件及文件夹，加到fileList里面
     * @param pathToRecursive ：需要遍历的本地文件（夹）
     * @param pathToSave ：需要上传到服务器的位置
     * @param fileList ：一个MutableList<Pair<String,String>>)，value1是本地文件路径，2是要上传到的服务器路径
     * @return 一个MutableList<Pair<String,String>>)，value1是本地文件路径，2是要上传到的服务器路径
     */
    private fun recursiveWAllFile(pathToRecursive:String, pathToSave:String, fileList:MutableList<Pair<String,String>>):MutableList<Pair<String,String>>{
        val file = File(pathToRecursive)
        if (!file.isDirectory){
            fileList.add(Pair(pathToRecursive,pathToSave))
        }else{
            val newToPath = pathToSave + "/" + file.name
            mainWindow.ssh.execCommand("mkdir $newToPath")
            file.listFiles().forEach {
                recursiveWAllFile(pathToRecursive+File.separator+it.name,newToPath,fileList)
            }
        }
        return fileList
    }

}