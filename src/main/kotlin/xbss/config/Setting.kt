package xbss.config

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.io.File

/**
 * @author  Xbss
 * @create 2023-04-22-23:58
 * @version  1.0
 * @describe
 */
object Setting {
    val backgroundImagePathP = SimpleStringProperty("G:\\图片图标资源\\Image\\247402b1880511ebb6edd017c2d2eca2.jpg")
    val savePathP = SimpleStringProperty("F:\\删除")
    val reSavePathP = SimpleStringProperty("F:\\")
    var image = Image(backgroundImagePathP.value)
    val opacityP = SimpleDoubleProperty(0.5)
    val isAutoResize = SimpleBooleanProperty(true)
    init {
        image = try {
            Image(backgroundImagePathP.value)
        }catch (e:Exception){
            Image(this::class.java.getResourceAsStream("/img/back.png"))
        }
        backgroundImagePathP.addListener { _,_,newValue ->
            File(newValue).let {
                if (it.exists()){
                    image = Image(it.inputStream())
                }
            }
        }
    }
}