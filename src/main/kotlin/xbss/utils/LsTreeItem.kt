package xbss.utils

import com.jcraft.jsch.ChannelSftp.LsEntry
import com.jcraft.jsch.SftpATTRS
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem
import xbss.MainAPP
import xbss.config.FileType
import xbss.ssh.SSH

/**
 * @author  Xbss
 * @create 2023-03-15-16:38
 * @version  1.0
 * @describe 主构造器（单个文件用）：必须传一个path，即该文件的路径，第二构造器（文件夹用）,传一个sftp用来遍历
 */
class LsTreeItem(val path: String):TreeItem<LsTreeItem.FileItem>(){
    data class FileItem(var path: String,var fileName:String,val fileType:FileType)
    val loadComplete = SimpleBooleanProperty(false) //该结点是否加载完成
    private val files: MutableList<LsEntry> = mutableListOf()
    private lateinit var sftpATTRS: SftpATTRS
    private var notInitialized = true
    private var permission = true //这个节点的文件是否有权限查看
//    private var sftp1: ChannelSftp? = null
    private var ssh: SSH? = null
    private var isFolder = false //这个节点是不是一个文件夹
    init {
        setFullName()
    }
    constructor(csSh: SSH, path:String):this(path){
        ssh = csSh
        val sftp = ssh!!.getChSftp()
        //过滤掉没有权限的
        sftpATTRS = sftp.stat(path)
        if (sftpATTRS.isDir){
            isFolder = true
            try {
                val originList = sftp.ls(path) as MutableList<LsEntry>
                ssh!!.releaseChannel(sftp)
                val dirs = mutableListOf<LsEntry>()
                val nonDirs = mutableListOf<LsEntry>()
                for (file in originList) {
                    if (file.attrs.isDir) { dirs.add(file) } else { nonDirs.add(file) }
                }
                dirs.sortBy { it.filename }
                nonDirs.sortBy { it.filename }
                files.addAll(dirs)
                files.addAll(nonDirs)
            }catch (e:Exception){
                permission = false
                isFolder = false
                ssh!!.releaseChannel(sftp)
//                println("权限错误,请求文件夹${path}权限为${sftpATTRS.permissions}")
            }
//            val files = sftp1.ls(path) as MutableList<LsEntry>
//            val filter = files.filter { !it.attrs.isDir }
//            files.removeAll(filter.toSet())
//            files.addAll(filter)
//            files.removeIf { it.filename.endsWith("cfg") } //有个cfg文件stat(path)就报错，直接去掉
            //todo 优化排序
        }else
            ssh!!.releaseChannel(sftp)
        setFullName()
    }

    override fun getChildren(): ObservableList<TreeItem<FileItem>> {
        val children = super.getChildren()
        synchronized(children){
        if (notInitialized&&permission&&isFolder){ //没有初始化、有权限、是个文件夹才进行遍历字文件（夹）
            val sftp = ssh!!.getChSftp()
            notInitialized = false
            children.add(LsTreeItem("loading.deny")) //先加上加载中的结点
            MainAPP.service.submit { //另开一个线程慢慢遍历加载，加载完后把loading移除
                val items = mutableListOf<LsTreeItem>()
                try {
                    files.forEach {
                        if (!it.filename.endsWith(".")){
                            //因为不判断的话会会变成 //data1/pjf/model/vgg11.pth|FILE
                            if (path!="/")
                                items.add(LsTreeItem(ssh!!,path+"/"+it.filename))
                            else
                                items.add(LsTreeItem(ssh!!,path+it.filename))
                        }
                    }
                }catch (e:Exception){
                    println("加载子节点时捕获到异常$e")
                }finally {
                    ssh!!.releaseChannel(sftp)
                }
                children.addAll(items)
                children.removeFirst()
                loadComplete.value = true
            }
        }else if (notInitialized&&!permission){
            notInitialized = false
            children.add(LsTreeItem("Permission denied.deny"))
        }
        return children
        }
    }


    override fun isLeaf(): Boolean {
        return !isFolder
    }

    /**
     * full name 格式如下：/data1/pjf/model/vgg11.pth|FILE
     *
     */
    private fun setFullName(){
        val fileName = path.split("/").last()
        when(fileName.split(".").last()){
            "jpg","png","jpeg" -> this.value = FileItem(path,fileName,FileType.IMG)
            "py" -> this.value = FileItem(path,fileName,FileType.PY)
            "txt" -> this.value = FileItem(path,fileName,FileType.TXT)
            "md" -> this.value = FileItem(path,fileName,FileType.MD)
            "mp4" -> this.value = FileItem(path,fileName,FileType.VIDEO)
            "deny" -> this.value = FileItem(path,fileName.split(".").first(),FileType.DENY)
            else ->{
                if (isLeaf)
                    this.value = FileItem(path,fileName,FileType.FILE)
                else
                    this.value = FileItem(path,fileName,FileType.FOLDER)
            }
        }
    }
}