package xbss.view

import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import xbss.config.ImageIcon

/**
 * @author  Xbss
 * @create 2023-03-29-22:24
 * @version  1.0
 * @describe
 */
class ShowImage(private val imageView: ImageView,private val text:String =""):Application() {
    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        initController()
        imageView.canDrag()
        stage.apply {
            scene = Scene(AnchorPane(imageView).apply {
                AnchorPane.setTopAnchor(imageView,100.0)
                AnchorPane.setLeftAnchor(imageView,100.0)
                   },600.0,600.0)
            titleProperty().bind(Bindings.concat(text,"  ",imageView.fitWidthProperty(),"*",imageView.fitHeightProperty()))
            icons.add(ImageIcon.B)
            show()
        }
    }
    private fun initController(){
        imageView.setOnScroll { event ->
            val deltaY = event.deltaY
            val zoomFactor = 1.1 // 放大/缩小的因子
            if (deltaY > 0) {
                // 向下滚动，缩小图像
                imageView.scaleX *= zoomFactor
                imageView.scaleY *= zoomFactor
            } else {
                // 向上滚动，放大图像
                imageView.scaleX /= zoomFactor
                imageView.scaleY /= zoomFactor
            }
            event.consume() // 防止事件继续传递
        }
    }
    private fun ImageView.canDrag(){
        this.apply {
            var oldNodeX = 0.0
            var oldNodeY = 0.0
            var oldMoveX=0.0
            var oldMoveY=0.0
            setOnMousePressed {
                oldNodeX = it.sceneX
                oldNodeY = it.sceneY
                oldMoveX=translateX
                oldMoveY=translateY
            }
            setOnMouseDragged {
                translateX= it.sceneX - oldNodeX+oldMoveX
                translateY= it.sceneY - oldNodeY+oldMoveY
            }
        }
    }
}