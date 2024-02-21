package xbss.utils

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.layout.HBox
import xbss.config.GlobalLog
import xbss.ssh.SSH
import java.util.*
import kotlin.concurrent.timerTask

/**
 * @author  Xbss
 * @create 2023-03-25-16:41
 * @version  1.0
 * @describe ：① nvidia-smi --list-gpus | wc -l
 *              该命令将返回已安装的GPU数量的行数。输出的整数值即为已安装的显卡数量。
 *              请注意，此命令只适用于安装了NVIDIA GPU的系统。如果您的系统没有安装NVIDIA GPU，
 *              则可以尝试使用其他命令，如lspci或hwinfo等命令来查找已安装的显卡数量。
 *            ②只需要显示总内存和已用内存的值时，可以在终端中使用以下命令  free -h | awk 'NR==2{print $2,$3}'
 *              这个命令会执行以下操作：
 *                 free -h命令显示系统的内存使用情况。
 *                 通过管道将该命令的输出发送到awk命令中。
 *                 awk命令使用NR==2{print $2,$3}的语法来提取第2行的第2个和第3个字段，即总内存和已用内存，并将它们打印到终端上。
 *                 这将输出一个类似于7.7G 2.4G的字符串，其中第一个数字表示总内存，第二个数字表示已用内存。
 *             ③获得当前CPU利用率的百分比，可以使用top命令。该命令将显示当前系统的进程和资源使用情况，包括CPU利用率。
 *                          top -b -n 1 | grep "Cpu(s)" | awk '{print $2+$4 "%"}'
 *                该命令将以类似于23.9%的格式显示当前CPU利用率。这个命令执行以下操作：
 *                top -b -n 1命令以批处理模式运行top命令，并在一次循环后退出。
 *                通过管道将该命令的输出发送到grep命令中。
 *                grep命令使用"Cpu(s)"过滤出与CPU相关的行。
 *                通过管道将grep命令的输出发送到awk命令中。
 *                awk命令使用'{print $2+$4 "%"}'的语法来提取用户空间和系统空间的CPU利用率，并将它们相加得到总的CPU利用率，并在结尾加上百分号。
 *                请注意，此命令显示的是当前系统的总CPU利用率，而不是单个进程的CPU利用率。如果要查看单个进程的CPU利用率，可以使用top命令中的交互式模式。
 *                在该模式下，您可以使用PID或COMMAND列中的进程名称或ID来过滤进程，并查看其CPU利用率。
 *             ④  nvidia-smi --query-gpu=index,memory.used,memory.total --format=csv | tail -n+2
 *                       0, 0 MiB, 24576 MiB
 *                       1, 1767 MiB, 24576 MiB
 *                       2, 2869 MiB, 24576 MiB
 *                       3, 21177 MiB, 24576 MiB
 *
 *           警告！！！如果上面有转换错误（一般由于\r\n等没处理，或者removeSuffix("%")等remove不到），他不会抛异常报错而是直接卡在转换失败
 *           的地方，我try catch也捕获不到，但是可以正常运行了，只是这个功能没了
 *
 */
class SystemData(private val ssh: SSH):HBox() {
    //lateinit var timer: Timer
    var timer: Timer? = null
    private val uiList = mutableListOf<MessageBar>()
    private val dataList = mutableListOf<Pair<Double,String>>()
    private var hasGpu = false
    /**
     *  数据是 index, memory.used [MiB], memory.total [MiB]
     * 0, 0 MiB, 24576 MiB
     * 1, 1767 MiB, 24576 MiB
     * 2, 2869 MiB, 24576 MiB
     * 3, 21177 MiB, 24576 MiB
     *
     */
    init {
        uiList.add(MessageBar("CPU",MessageBar.Type.CPU))
        uiList.add(MessageBar("内存",MessageBar.Type.MEMORY))
        try {
//            MainAPP.service.submit {
//                val gpuNum = ssh.execCommand("nvidia-smi --list-gpus | wc -l").replace("\n", "").toInt()
//                Platform.runLater {
//                    for (i in 0 until gpuNum){
//                        val messageBar = MessageBar("GPU${i}", MessageBar.Type.GPU)
//                        uiList.add(messageBar)
//                        this.children.add(messageBar)
//                    }
////                    for (s in uiList)
////                        this.children.add(s)
//                }
//            }
            var gpuNum = 0
            try {
                gpuNum = ssh.execCommand("nvidia-smi --list-gpus | wc -l").message.replace("\n", "").toInt()
                hasGpu = true
            }catch(e:Exception){
                GlobalLog.writeErrorLog("gpuNum解析失败")
            }
//            Platform.runLater {
//                for (i in 0 until gpuNum){
//                    val messageBar = MessageBar("GPU${i}", MessageBar.Type.GPU)
//                    uiList.add(messageBar)
//                    this.children.add(messageBar)
//                }
////                    for (s in uiList)
////                        this.children.add(s)
//            }
            if (hasGpu){
                for (i in 0 until gpuNum){
                    val messageBar = MessageBar("GPU${i}", MessageBar.Type.GPU)
                    uiList.add(messageBar)
                }
            }
            for (s in uiList)
                this.children.add(s)
            timer = Timer()
            val timerTask = getTimerTask()
            timer!!.schedule(timerTask,5000,15000)
            ssh.isConnectProperty.addListener { _,_,newValue ->
                if (!newValue)
                    timer!!.cancel()
                else{
                    try {
                        timer = Timer()
                        timer!!.schedule(getTimerTask(),5000,15000)
                    }catch (e:Exception){
                        println("抛个异常$e")
                    }
                }
            }
            this.padding = Insets(10.0,0.0,0.0,10.0)
            this.spacing = 20.0
            this.prefHeight = 20.0
        } catch (e: Exception) {
            //如果上面有转换错误（一般由于\r\n等没处理,或者根本没有显卡），他不会抛异常报错而是直接卡在转换失败的地方，我try catch也捕获不到，但是可以正常运行了，只是这个功能没了
//            println("初始化系统信息失败！")
            GlobalLog.writeErrorLog("初始化系统信息失败！")
        }
    }

    /**
     *   返回 （value1/value2）*100再保留一位小数  变成类似25.1
     * @param value1
     * @param value2
     * @return
     */
    private fun compute(value1:String,value2:String) = ((value1.toDouble()/value2.toDouble())*100).reserveOne()
    private fun compute(value1:Double,value2:String) = ((value1/value2.toDouble())*100).reserveOne()

    /**
     * double保留1位小数
     *
     */
    private fun Double.reserveOne() = String.format("%.1f", this).toDouble()

    /**
     * 获得定时任务
     * @return
     */
    private fun getTimerTask():TimerTask{
        return timerTask {
            dataList.clear()
            val cpuResult =
                ssh.execCommand("top -b -n 1 | grep \"Cpu(s)\" | awk '{print \$2+\$4 \"%\"}'").message.replace("\n", "")
            //28.2%
            dataList.add(Pair(cpuResult.removeSuffix("%").toDouble(),""))
            //7.7G 2.4G
            //15G 643M
            //这里有可能是两个单位，需要判断换算
            val memoryResult =
                ssh.execCommand("free -h | awk 'NR==2{print \$2,\$3}'").message.replace("\n", "").replace("\r", "")
                    .split(" ")
            val memoryPer = if (memoryResult[1].last()=='M'){
                val value1 = memoryResult[1].removeSuffix("M")
                compute(value1.toDouble().div(1024).reserveOne(),memoryResult[0].removeSuffix("G"))
            }else{
                compute(memoryResult[1].removeSuffix("G"),memoryResult[0].removeSuffix("G"))
            }

//                val memoryPer = compute(memoryResult[1].removeSuffix("G"),memoryResult[0].removeSuffix("G"))

            dataList.add(Pair(memoryPer,"${memoryResult[1]}/${memoryResult[0]}"))
            if (hasGpu){
                val gpuResult =
                    ssh.execCommand("nvidia-smi --query-gpu=index,memory.used,memory.total --format=csv | tail -n+2").message.replace(
                        " ",
                        ""
                    ).split("\n")
                // 3, 21177 MiB, 24576 MiB

                for (s in gpuResult){
                    if (s.isNotEmpty()){ // 会有空行
                        val strings = s.split(",")
                        val nowValue = strings[1].replace("MiB", "")
                        val maxValue = strings[2].replace("MiB", "")
                        dataList.add(Pair((compute(nowValue,maxValue)),"${strings[1].replace("MiB", "M")}/${strings[2].replace("MiB", "M")}"))
                    }
                }
            }
            Platform.runLater {
                for ((index,value) in uiList.withIndex()){
                    try {
                        value.update(dataList[index].first, dataList[index].second)
                    } catch (e: Exception) {
                        GlobalLog.writeErrorLog("${e} ${dataList} index是${index}")
                    }
                }
            }
        }
    }
}