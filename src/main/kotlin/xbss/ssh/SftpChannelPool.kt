package xbss.ssh

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.Session
import javafx.beans.property.SimpleIntegerProperty
import xbss.config.GlobalLog

/**
 * @author  Xbss
 * @create 2023-03-29-10:20
 * @version  1.0
 * @describe
 */
class SftpChannelPool(private val session: Session) {
    data class SftpChannelWrapper(val channel: ChannelSftp,var inUse:Boolean = false)
//    data class SftpChannelPoolMessage(val channel: ChannelSftp?,val isAvailable:SimpleBooleanProperty?=null)
//    val isAvailable = SimpleBooleanProperty(true)
    /**
     * 当前通道池内是否有可用的通道，会根据sftpAvailableNum的值是否等于0更新
     */
    var isAvailable = false

    /**
     * 可观察属性，当前通道池内可用的通道数
     */
    val sftpAvailableNumP = SimpleIntegerProperty(0)
    private val poolSize = 5 //连接数有限制的，不能无限大，大概五六个的样子
    private val channels = mutableListOf<SftpChannelWrapper>()
    init {
        sftpAvailableNumP.addListener{ _, _, newValue ->
            isAvailable = newValue.toInt() != 0
        }
        for (i in 1..poolSize) {
            addNewChannel()
        }
    }

    /**
     * 2023年4月24日20:46:14：获取一个可用的ChannelSftp，当前无可用通道返回空，不知道我当时怎么写了个return addNewChannel()
     * 就先这样吧，按理说我在取通道前应该都是先判断有可用通道才会来取，一般取不到空的，实测在五个通道时不够用还可以在new一个，再多没测试
     * @return
     */
    fun getChannel(): ChannelSftp?{
//        println("进来获取通道，可用数${getAvailable()}")
        for (wrapper in channels) {
            synchronized(wrapper) {
                if (!wrapper.inUse) {
                    sftpAvailableNumP.value--
                    wrapper.inUse = true
                    return wrapper.channel
                }
            }
        }
//        isAvailable.value = false
        GlobalLog.writeErrorLog("没找到空闲的sftp通道，尝试创建sftp通道")
        return addNewChannel()
    }

    /**
     * 释放channel
     * @param channel
     */
    fun releaseChannel(channel: ChannelSftp) {
//        println("进来释放使用的sftp $channel")
        for (wrapper in channels) {
            if (wrapper.channel == channel) {
                synchronized(wrapper) {
                    wrapper.inUse = false
                    sftpAvailableNumP.value++
                }
                break
            }
        }
//        isAvailable.value = true
    }

    private fun addNewChannel(): ChannelSftp? {
        val channel = session.openChannel("sftp") as ChannelSftp
        return try {
            channel.connect()
            channel.setFilenameEncoding("UTF-8")
            channels.add(SftpChannelWrapper(channel))
            sftpAvailableNumP.value++
            channel
        } catch (e: Exception) {
            GlobalLog.writeErrorLog("创建sftp通道失败")
            null
        }
    }
    private fun getAvailableNum():Int{
//        var i = 0
//        channels.forEach {
//            if (!it.inUse)
//                i++
//        }
//        println("当前Sftp池中有${i}个通道可用")
        return channels.count { !it.inUse }
    }
    fun close(){
        channels.forEach { it.channel.disconnect() }
    }

}