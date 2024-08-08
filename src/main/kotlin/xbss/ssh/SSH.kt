package xbss.ssh

import com.jcraft.jsch.*
import com.pty4j.PtyProcess
import com.pty4j.WinSize
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import xbss.MainAPP
import xbss.config.GlobalLog
import xbss.myterminal.jediterm.pty.PtyProcessTtyConnector
import xbss.server.mapper.pojo.Account
import java.io.*
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.concurrent.timerTask


/**
 * @author  Xbss
 * @create 2023-03-23-12:06
 * @version  1.0
 * @describe
 */
//open class SSH(private val account: Account) {
open class SSH(val account: Account) {
    data class SSHMessage(val ssh: SSH?, val tip:String?= null)
    var firstConnect = true //是否是第一次连接，用于区分重连和初次连接
    private lateinit var timer:Timer //定时任务，每隔十五秒检测连接是否正常
    val isConnectProperty = SimpleBooleanProperty(true)
    val successP = SimpleObjectProperty<SSHMessage>() //连接是否成功以及自身、对应原因
    val progressP = SimpleStringProperty() //连接时的进度信息
//    private val output = ByteArrayOutputStream() //用户获得输出用的管道
    private lateinit var output:OutputStream  //用户获得输出用的管道
    private lateinit var userWrite:PipedOutputStream //用户写入命令用的管道
//    private lateinit var chSftp: ChannelSftp
//    private val chSftpPool: MutableList<ChannelSftp> = mutableListOf()
    private lateinit var chSftpPool: SftpChannelPool
    private lateinit var shellChannel: ChannelShell
    private lateinit var execChannel: ChannelExec
//    private val session: Session
    private lateinit var session: Session
    init {
        isConnectProperty.addListener { _,_,_ -> MainAPP.highlightIcon() }
    }
    fun initSSH(){
        progressP.value = "正在建立连接..."
        val jSch = JSch()
        session = jSch.getSession(account.username, account.host, account.port.toInt())
        session.setPassword(account.password)
        val properties = Properties().apply {
            put("StrictHostKeyChecking", "no")
        }
        session.setConfig(properties)
        session.timeout = 25000
        //这边经常会报错Caused by: com.jcraft.jsch.JSchException: Auth fail或者Session.connect: java.net.SocketTimeoutException: Read timed out，记得处理一下
        MainAPP.service.submit {
            try {
                session.connect()
                if (session.isConnected) {
                    progressP.value = "正在初始化管道..."
                    println("【SSH连接】连接成功")
                    shellChannel = session.openChannel("shell") as ChannelShell
//                    chSftp = session.openChannel("sftp") as ChannelSftp
                    val pipedInput = PipedInputStream()
                    userWrite = PipedOutputStream()
                    pipedInput.connect(userWrite)
//                    shellChannel.outputStream = output
                    output = shellChannel.outputStream
                    shellChannel.inputStream = pipedInput
                    shellChannel.connect()
                    shellChannel.setPty(true)
                    //wp是设置终端显示的宽度
                    shellChannel.setPtySize(119, 34, 980, 480)

                    //如果是第一次连接，需要初始化文件树需要用到sftp，要先初始化sftp池再返回
                    if (firstConnect)
                        chSftpPool = SftpChannelPool(session)
                    progressP.value = "正在初始化界面..."
                    isConnectProperty.value = true
                    successP.value = SSHMessage(this,"连接成功")

                    //如果是重新连接，那么可以放在后面，因为这个操作比较耗时，如果等到sftp池初始化完再返回，终端就有概率看不到最开始的两句：
                    //Last login: Sat May 13 03:14:06 2023 from 10.0.250.155
                    //(base) [msfgroup@msfgroup1 ~]$
                    if (!firstConnect)
                        chSftpPool = SftpChannelPool(session)
                    timer = Timer()
                    timer.schedule(getTimerTask(),5000,15000)
                } else {
                    progressP.value = "连接失败!"
                    successP.value = SSHMessage(null,"失败")
                    println("【SSH连接】连接失败")
                }
            }catch (e: SocketTimeoutException){
                progressP.value = "连接失败（超时）"
                successP.value = SSHMessage(null,"连接超时")
            }catch (e: JSchException){
                progressP.value = "连接失败（Auth fail）"
                successP.value = SSHMessage(null,"Auth fail")
            }
        }
    }

    private fun getTimerTask(): TimerTask {
        return timerTask {
            isConnectProperty.value = session.isConnected
            if (!session.isConnected)
                timer.cancel()
        }
    }

    //    fun getOutput():ByteArrayOutputStream = output
    fun getInput():PipedOutputStream = userWrite

    /**
     * 实现Ctrl+C的中断信号
     * 使用shellChannel.sendSignal("INT")无效
     */
    fun stopNowProcess(){
        output.write(3)
        output.flush()
    }

    /**
     * col是一行最多显示的字符，这个必须和终端的terminalTextBuffer.width一样，过短会出现当在终端输入一行过长时不会自动换行而是从头
     *                          开始覆盖类似\r的效果。过长会可以换行，但是换行到第二行还是会出现从头开始覆盖类似\r的效果
     * 设置col的作用是伪终端输出的数据流会根据设置的col在数据中相应的位置添加换行符，比如你设置col为80，另一端输出的数据有90个字符，
     *     他就会在第80个字符后面插入一个换行符，这样终端渲染时就知道从哪里断开换行了，所以col必须和终端的textBuffer宽度一样
     * wp是设置终端显示的宽度
     * int col - 表示要设置的终端的列数（宽度）。这个参数指定了伪终端的列数，即在终端窗口中水平方向上可以显示的字符数。
     *
     * int row - 表示要设置的终端的行数（高度）。这个参数指定了伪终端的行数，即在终端窗口中垂直方向上可以显示的行数。
     *
     * int wp - 表示要设置的终端的宽度像素。这个参数指定了终端窗口的宽度，以像素为单位。
     *
     * int hp - 表示要设置的终端的高度像素。这个参数指定了终端窗口的高度，以像素为单位。
     * @param col
     */
    fun setPtySize(col:Int){
        shellChannel.setPtySize(col,34, 980, 480)
    }
    fun sftpIsAvailable() = chSftpPool.isAvailable
    fun sftpAvailableNum() = chSftpPool.sftpAvailableNumP

    /**
     *建议使用{@link #getChSftpOrNull() },这个当初写时没考虑太多，用的地方懒得改了，就保留下来了
     * 虽然返回不为空，实际有可能是空的，会报错，不过可以用在必须取到通道的地方，取不到就报错终止程序
     * @return
     */
    fun getChSftp():ChannelSftp{
        return chSftpPool.getChannel()!!
    }

    /**
     * 推荐使用，因为有可能获取时没有可用的Sftp通道
     * @return
     */
    fun getChSftpOrNull():ChannelSftp?{
        return chSftpPool.getChannel()
    }
    fun releaseChannel(channel: ChannelSftp) {
        chSftpPool.releaseChannel(channel)
    }
    //todo :释放的有没有问题？ https://blog.csdn.net/weixin_43898952/article/details/119967566
    /**执行单个命令的结果，返回exitCode和message
     * exitCode为0表示成功，其他值（大部分是1）表示失败
     */
    data class ExecResponse(val exitCode: Int, val message: String)

    //    fun execCommand(command:String):String{
    fun execCommand(command: String): ExecResponse {
//        println("执行命令 $command")
        val result = StringBuilder()
        var exitStatus = 0 //退出码
        execChannel = session.openChannel("exec") as ChannelExec
        execChannel.setCommand(command)
        execChannel.inputStream = null
        // 错误信息输出流，用于输出错误的信息，当exitstatus<0的时候
//        execChannel.setErrStream(System.err)
        try {
            // 执行命令，等待执行结果
            execChannel.connect()
        }catch (e:Exception){
            println("发生异常$e")
            isConnectProperty.value = false
            execChannel.disconnect()
            close()
//            return ""
            return ExecResponse(1, "")
        }
        // 获取命令执行结果
        val inputStream = execChannel.inputStream
        val errStream = execChannel.errStream
        // 错误信息输出流，用于输出错误的信息，当exitstatus<0的时候
        /**
         * 通过channel获取信息的方式，采用官方Demo代码
         */
        val tmp = ByteArray(1024)
        while (true) {
            while (inputStream.available() > 0) {
                val i: Int = inputStream.read(tmp, 0, 1024)
                if (i < 0) {
                    break
                }
                result.append(String(tmp, 0, i))
            }
            while (errStream.available() > 0) {
                val i: Int = errStream.read(tmp, 0, 1024)
                if (i < 0) {
                    break
                }
                result.append(String(tmp, 0, i))
            }
            // 从channel获取全部信息之后，channel会自动关闭
            if (execChannel.isClosed) {
                if (inputStream.available() > 0) {
                    continue
                }
                exitStatus = execChannel.exitStatus//正常是0 错误是1
                execChannel.disconnect()
                break
            }
        }
        if (exitStatus != 0) {
            GlobalLog.writeErrorLog("执行命令${command}失败，错误信息：$result")
        }
        return ExecResponse(exitStatus, result.toString())
    }

    /**
     * 重新连接
     * @return
     */
    fun reConnect(){
        successP.value = SSHMessage(null,"连接主机...")
        initSSH()
    }
    fun close(){
        println("关闭连接")
        chSftpPool.close()
        // 关闭sftpChannel
//        if (chSftp.isConnected)
//            chSftp.quit()
        if (shellChannel.isConnected)
            shellChannel.disconnect()
        // 关闭jschSesson流
        if (session.isConnected)
            session.disconnect()
        timer.cancel()
        isConnectProperty.value = false
    }

    /**
     * 获取该连接对应的PtyProcessTtyConnector，用于与终端交互
     * @return PtyProcessTtyConnector
     */
    fun getPtyProcessTtyConnector():PtyProcessTtyConnector{
        val process = object : PtyProcess() {
            override fun getOutputStream(): OutputStream {
                return shellChannel.outputStream
            }

            override fun getInputStream(): InputStream {
                return shellChannel.inputStream
            }

            override fun getErrorStream(): InputStream {
                return shellChannel.extInputStream
            }
            override fun waitFor(): Int { return 0 }
            override fun exitValue(): Int { return 0 }
            override fun destroy() {}

            /**
             *这方法根本没调用过
             * @param p0
             */
            override fun setWinSize(p0: WinSize) { shellChannel.setPtySize(110, 34, 640, 480) }
            /**
             *  我不知道这是干嘛的，好像是设置伪终端大小，但是这方法根本没调用过
             */
            override fun getWinSize(): WinSize { return WinSize(110, 24) }
        }
        return PtyProcessTtyConnector(process, StandardCharsets.UTF_8)
    }
}