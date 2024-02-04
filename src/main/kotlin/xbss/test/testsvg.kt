package xbss.test

/**
 * @author  Xbss
 * @create 2023-05-06-10:24
 * @version  1.0
 * @describe
 */
import javafx.animation.*
import javafx.application.Application
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.SVGPath
import javafx.scene.shape.StrokeLineCap
import javafx.stage.Stage
import javafx.util.Duration


class LightningShape : Application() {

    override fun start(primaryStage: Stage) {

        val svgPath = SVGPath()
//        svgPath.content = "M503.306008 943.44652c-16.165136 0-31.740848-6.740504-42.680993-18.538178-10.886933-11.686133-16.208114-27.079698-15.027221-43.3707l19.127601-269.765487H210.028061c-22.189327 0-41.97696-12.415749-51.683-32.415206-9.775625-20.113043-7.24704-43.48224 6.516401-61.024743L491.691506 101.69085c21.784099-27.779638 66.585378-27.978159 89.057137-1.769293 10.924795 12.752416 15.533757 29.184634 12.946844 46.234929l-35.812575 234.458424h256.115633c21.459711 0 41.009937 11.881585 50.981013 30.952904 9.972099 19.1583 8.509797 42.078267-3.850694 59.801895L550.073054 918.671304c-10.923772 15.786513-27.975089 24.775217-46.767046 24.775216z m-0.310061-57.525043l0.310061 28.706752V885.864172c-0.112563 0-0.196474 0-0.310061 0.057305z m33.819179-748.474333L210.098669 553.908398l316.382108 0.280386-23.539065 331.450261 311.029204-447.162425-323.122613-0.251733 45.966823-300.777743z"
//        svgPath.content = "M 250 150 L 200 200 L 230 200 L 210 250 L 270 200 L 240 200 L 250 150"
//        svgPath.content = "M377.9 114.1h358.3l-136.4 268 231.5 0.3-485.1 559.3 139.5-356.4H222.6z"
//        svgPath.content = "M377.9 114.1h358.3l-136.4 268 231.5 0.3-485.1 559.3 139.5-356.4H222.6z"
//        svgPath.scaleX = 24.0/1024.0
//        svgPath.scaleY = 24.0/1024.0
        svgPath.content = "M 18 0 L 0 12 L 13 15 L 12 24 L 26 12 L 17 10 L 18 0"
//        svgPath.fill = Color.web("#FED928")
        svgPath.fill = Color.GREEN
        val xPath = SVGPath()
        xPath.content = "M0 0 L100 100 M100 0 L0 100" // 设置 X 号的路径

        xPath.stroke = Color.RED // 设置 X 号的颜色

        xPath.strokeLineCap = StrokeLineCap.ROUND // 设置线段端点为圆形

        xPath.strokeWidth = 4.0 // 设置 X 号的粗细

        val plusPath = SVGPath()
//        plusPath.content = "M2 10 H18 M10 2 V18" // 设置 X 号的路径
        plusPath.content = "M10 10 H26 M18 2 V18" // 设置 X 号的路径

        plusPath.stroke = Color.GRAY // 设置 X 号的颜色

//        plusPath.strokeLineCap = StrokeLineCap.ROUND // 设置线段端点为圆形

        plusPath.strokeWidth = 3.0 // 设置 X 号的粗细
        plusPath.addEventFilter(MouseEvent.MOUSE_CLICKED) {
            println("点击")
            it.consume()
        }
        val pane = Pane(plusPath)
        pane.style = "-fx-background-color:red"

//        val root = Group()
//        val duration = Duration.millis(500.0)
//        val translateTransition = TranslateTransition(duration, svgPath)
//        translateTransition.byX = -3.0
//        translateTransition.byY = 3.0
//        translateTransition.interpolator = Interpolator.EASE_IN
//
//        val scaleTransition = ScaleTransition(duration.divide(2.0), svgPath)
//        scaleTransition.toX = 0.8
//        scaleTransition.toY = 0.8
//        scaleTransition.interpolator = Interpolator.EASE_IN
//
//        val reverseScaleTransition = ScaleTransition(duration.divide(2.0), svgPath)
//        reverseScaleTransition.toX = 1.0
//        reverseScaleTransition.toY = 1.0
//        reverseScaleTransition.interpolator = Interpolator.EASE_OUT
        svgPath.isPickOnBounds = true
        val duration = Duration.seconds(0.02)
        val scaleDuration = Duration.seconds(0.02)
        val scale = 0.8
        val scaleTransition = ScaleTransition(scaleDuration, svgPath)
        scaleTransition.toX = scale
        scaleTransition.toY = scale

        val translateTransition = TranslateTransition(duration, svgPath)
        translateTransition.byX = 1.5
        translateTransition.byY = 1.5

        val parallelTransition = ParallelTransition(scaleTransition, translateTransition)

        val scaleBackTransition = ScaleTransition(scaleDuration, svgPath)
        scaleBackTransition.toX = 1.0
        scaleBackTransition.toY = 1.0

        val translateBackTransition = TranslateTransition(duration, svgPath)
        translateBackTransition.byX = -1.5
        translateBackTransition.byY = -1.5

        val sequenceTransition = SequentialTransition(parallelTransition, PauseTransition(duration),
            SequentialTransition(scaleBackTransition, translateBackTransition)
        )

        svgPath.setOnMouseClicked{
                sequenceTransition.play()
        }
        val root = HBox()
//        root.children.addAll(svgPath,xPath,plusPath)
        val eyePasswordT = SVGPath().apply {
            content = "M1024 497.664c0 100.352-210.944 313.344-512 313.344-292.864 0-512-208.896-512-313.344C0 393.216 221.184 184.32 512 184.32c303.104 0 512 208.896 512 313.344zM514.048 241.664c-137.216 0-245.76 114.688-245.76 256s110.592 256 245.76 256c137.216 0 245.76-114.688 245.76-256s-110.592-256-245.76-256z m-2.048 92.16c-86.016 0-157.696 73.728-157.696 161.792 0 90.09152 69.632 161.77152 157.696 161.77152 86.016 0 157.696-73.728 157.696-161.77152 0-88.064-69.632-161.792-157.696-161.792z"
//            fill = Paint.valueOf("#00FF00")
            cursor = Cursor.HAND
            isPickOnBounds = true
            scaleX = 24.0/1024.0
            scaleY = 24.0/1024.0
            style="-fx-background-color:blue"
            val centerX: Double = 1024 / 24.0
            val centerY: Double = 1024 / 24.0
            prefHeight(24.0)
            prefWidth(24.0)
//            val translate = Translate(-centerX * (1 - xScalexScale), -centerY * (1 - yScale))
//            transforms.addAll()
        }
        root.children.addAll(eyePasswordT,svgPath,xPath,pane)
//        root.children.addAll(HBox(eyePasswordT).apply {  style="-fx-background-color:blue" })
        val vBox = VBox(root)
        val scene = Scene(vBox, 300.0, 400.0)
        primaryStage.title = "Lightning Shape"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(LightningShape::class.java, *args)
}
