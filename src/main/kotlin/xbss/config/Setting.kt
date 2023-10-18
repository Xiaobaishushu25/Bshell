package xbss.config

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xbss.MainAPP
import xbss.config.DatabasePro.userPath
import xbss.myterminal.jediterm.terminal.TerminalColor
import java.io.File

/**
 * @author  Xbss
 * @create 2023-04-22-23:58
 * @version  1.0
 * @describe
 */

/**
 * 设置有两种情况，改动后即刻生效或保存后重启生效，没有P的属性一般都是不能实时更新的
 * 目前是改动后，仅仅修改Property属性，不点确定，不会写入配置文件，重启后仍是之前设置
 * 设计思路：Setting可以视为两层，外层是设置的ui面板，可以直接修改Setting中的可观察属性。
 *                            内层是持久化的数据即SettingPersistence，仅当点击确定时修改并持久化
 */
object Setting {
    /**
     * 与config文件交互的类
     */
    @Serializable
    data class SettingPersistence(
        var backgroundImagePath: String?, //背景图片路径，为空时使用默认背景
        var opacity: Double, //背景图片透明度
        var savePath: String?, //默认下载路径
        var reSavePath: String?, //另存为路径
        var terminalFontSize: Float, //终端文字大小
        var terminalCursorColor: String, //终端光标颜色
        var terminalUseBackImage: Boolean, //终端是否使用背景图片
        var terminalTextColorUnSelected: String, //终端文字未选中时的颜色
        var terminalBackColor: String, //终端背景颜色，默认透明的
        var terminalTextColorSelecting: String, //终端文字选中时的颜色
        var terminalBackColorSelecting: String, //终端选中时的背景颜色
    )

    //设置json格式化
    private val json = Json { prettyPrint = true }
    private val configFile = File("$userPath${File.separator}database${File.separator}config")
    private lateinit var settingPersistence: SettingPersistence
    var terminalUseBackImage = true
    lateinit var terminalTextColorUnSelected: TerminalColor
    lateinit var terminalBackColor: TerminalColor
    lateinit var terminalTextColorSelecting: TerminalColor
    lateinit var terminalBackColorSelecting: TerminalColor
    lateinit var backgroundImagePathP: SimpleStringProperty
    lateinit var savePathP: SimpleStringProperty
    lateinit var reSavePathP: SimpleStringProperty
    lateinit var opacityP: SimpleDoubleProperty
    lateinit var terminalFontSizeP: SimpleFloatProperty
    lateinit var terminalCursorColorP: SimpleStringProperty
    lateinit var image: Image

    //    val isAutoResize = SimpleBooleanProperty(true)
    init {
        readSetting()
        initSetting()
        registerListener()
    }

    private fun initSetting() {
        backgroundImagePathP = SimpleStringProperty(settingPersistence.backgroundImagePath)
        savePathP = SimpleStringProperty(settingPersistence.savePath)
        reSavePathP = SimpleStringProperty(settingPersistence.reSavePath)
        opacityP = SimpleDoubleProperty(settingPersistence.opacity)
        terminalFontSizeP = SimpleFloatProperty(settingPersistence.terminalFontSize)
        terminalCursorColorP = SimpleStringProperty(settingPersistence.terminalCursorColor)
        terminalUseBackImage = settingPersistence.terminalUseBackImage
        terminalTextColorUnSelected = settingPersistence.terminalTextColorUnSelected.parseColor()
        terminalBackColor = settingPersistence.terminalBackColor.parseColor()
        terminalTextColorSelecting = settingPersistence.terminalTextColorSelecting.parseColor()
        terminalBackColorSelecting = settingPersistence.terminalBackColorSelecting.parseColor()
        try {
            val backFileInputStream = File(backgroundImagePathP.value).inputStream()
            image = Image(backFileInputStream)
            backFileInputStream.close()
        }catch (e:Exception){
            GlobalLog.writeErrorLog("指定背景${backgroundImagePathP.value}不存在，设置为默认背景.")
            image = Image(this::class.java.getResourceAsStream("/img/back.jpg"))
        }
    }

    private fun registerListener() {
        backgroundImagePathP.addListener { _,_,newValue ->
            File(newValue).let {
                if (it.exists()){
                    val inputStream = it.inputStream()
                    image = Image(inputStream)
                    inputStream.close()
                }
            }
        }
    }

    /**
     * 读取配置文件，当不存在时新建一个
     */
    private fun readSetting() {
        if (!configFile.exists()) {
            val defaultDirPath = "${userPath}${File.separator}download"
            val downDir = File(defaultDirPath)
            if (!downDir.exists())
                downDir.mkdir()
            GlobalLog.writeInfoLog("配置文件不存在，创建配置文件.")
            if (configFile.createNewFile()) {
                settingPersistence = SettingPersistence(
                    null,
                    0.5,
                    defaultDirPath,
                    defaultDirPath,
                    16.0f,
                    "152,251,152",
                    true,
                    "28,28,28",
                    "255,246,142,0",
                    "28,28,28",
                    "151,255,255"
                )
                //另开一个线程写入，因为还挺耗时的
                MainAPP.service.submit {
                    configFile.writeText(json.encodeToString(settingPersistence), charset("utf-8"))
                }
            }
        } else {
            settingPersistence = json.decodeFromString<SettingPersistence>(configFile.readText(charset("utf-8")))
        }
    }

    /**
     * 保存设置，将当前Setting的属性保存到settingPersistence中，再持久化到配置文件
     */
    fun saveSetting() {
        settingPersistence.apply {
            backgroundImagePath = backgroundImagePathP.value
            opacity = opacityP.value
            savePath = savePathP.value
            terminalFontSize = terminalFontSizeP.value
            terminalCursorColor = terminalCursorColorP.value
            terminalUseBackImage = this@Setting.terminalUseBackImage
            terminalTextColorUnSelected = this@Setting.terminalTextColorUnSelected.toRgbString()
            terminalBackColor = this@Setting.terminalBackColor.toRgbaString()
            terminalTextColorSelecting = this@Setting.terminalTextColorSelecting.toRgbString()
            terminalBackColorSelecting = this@Setting.terminalBackColorSelecting.toRgbString()
        }
        MainAPP.service.submit {
            configFile.writeText(json.encodeToString(settingPersistence), charset("utf-8"))
        }
    }

    /**
     * 由字符串解析颜色
     */
    fun String.parseColor(): TerminalColor {
        val colorNum: List<Int> = this.split(",").map { it.trim().toInt() }
        return if (colorNum.size == 3) {
            TerminalColor(colorNum[0], colorNum[1], colorNum[2])
        } else {
            TerminalColor(colorNum[0], colorNum[1], colorNum[2], colorNum[3])
        }
    }

    /**
     * 把TerminalColor转为不包含透明度的字符串,形式"255,255,255"
     */
    private fun TerminalColor.toRgbString(): String {
        val color = this.toColor()
        return "${color.red},${color.green},${color.blue}"
    }

    /**
     * 把TerminalColor转为包含透明度的字符串,形式"255,255,255,0"，应该只有终端背景纯色会用到这个函数
     */
    private fun TerminalColor.toRgbaString(): String {
        val color = this.toColor()
        return "${color.red},${color.green},${color.blue},${color.alpha}"
    }

    /**
     * 因为另存为 和其他属性不一样。其他的要点确定才保存，这个属性要自动保存
     */
    private fun writeReSavePathOnly() {
        MainAPP.service.submit {
            configFile.writeText(json.encodeToString(settingPersistence), charset("utf-8"))
        }
    }

    /**
     * 更新另存为的路径
     */
    fun setNewReSavePath(newPath: String) {
        settingPersistence.reSavePath = newPath
        writeReSavePathOnly()
    }
}