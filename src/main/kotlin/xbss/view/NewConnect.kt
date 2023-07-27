package xbss.view

import javafx.animation.FadeTransition
import javafx.application.Application
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.shape.Circle
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.Duration
import xbss.config.AppData
import xbss.config.ImageIcon
import xbss.server.mapper.pojo.Account
import xbss.ssh.testConnect
import xbss.utils.KeyUtil

/**
 * @author  Xbss
 * @create 2023-03-25-0:22
 * @version  1.0
 * @describe
 */
class NewConnect(private val account: Account?= null,private val isChange:Boolean = true):Application() {
    val success = SimpleIntegerProperty(0) //连上了去外面报个信，让InitPane加账户
    val update = SimpleStringProperty("")  //返回修改的名字，让InitPane修改对应的Tab
    private  var nickNameT:TextField = getTextFiled()
    private  var hostT:TextField = getTextFiled()
    private  var portT:TextField = getTextFiled("22",60.0)
    private  var usernameT:TextField = getTextFiled()
//    private  var passwordT:TextField = getTextFiled(moveX = 15.0)
    private lateinit var passwordT:PasswordField
    private  var cdPathT:TextField = getTextFiled("/", double = 230.0)
    private  var initCommandT:TextField = getTextFiled(double = 230.0)
    private  var commentT:TextField = getTextFiled(text = "无",double = 230.0, moveX = 30.0)
    private lateinit var tip:HBox
    private lateinit var hisTry:HBox //历史尝试记录
    private lateinit var test:Button
    private lateinit var ft:FadeTransition//动画，展示完连接信息10秒后淡出
    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        test = Button("Test").apply {
            style = "-fx-background-radius: 10;-fx-font-size: 14.0;-fx-font-weight: bold;-fx-cursor: hand;-fx-background-color: #BBFFFF;"
            setOnAction {
                isDisable = true
                //移除登录结果的提示不完善，先随便写写，点击登录时如果有的话，要移除上次登录提示
//                tip.children.clear()
//                tip.isVisible = false
                this.parent.requestFocus()
                val account = Account {
                    id = KeyUtil.genUniqueKey()
                    nickname = nickNameT.text
                    username = usernameT.text
                    password = passwordT.text
                    host = hostT.text
                    port = portT.text
                    defaultPath = cdPathT.text
                    initCommand = initCommandT.text
                    comments = commentT.text
                }
//                Platform.runLater { success.bind(testConnect(account)) }
                success.bind(testConnect(account))
            }
        }
        passwordT = PasswordField().apply {
            style = "-fx-border-radius: 4px;-fx-border-color: #708090;-fx-background-radius: 4px;-fx-background-insets:0;-fx-background-color:transparent"
            translateX = 15.0
        }
        tip = HBox(10.0).apply {
            isVisible = false
            alignment = Pos.CENTER_LEFT
            translateX = -100.0
            //点击信息立刻删除，不需要等淡出
            setOnMouseClicked { isVisible = false }
//            setStyle("-fx-border-width: 1px;-fx-border-color: green")

        }
        hisTry = HBox(5.0).apply {
            alignment = Pos.CENTER
//            translateX = -190.0
//            setStyle("-fx-border-width: 1px;-fx-border-color: red")
        }
        ft =FadeTransition(Duration.millis(10000.0), tip).apply {
            fromValue = 1.0
            toValue = 0.0
            cycleCount = 1
            isAutoReverse = false }
        val vBox = VBox(
            20.0,
            HBox(15.0, getLabel("名称"), nickNameT),
            HBox(15.0, getLabel("主机"), hostT, getLabel("端口"), portT),
            HBox(15.0, getLabel("用户名"), usernameT),
            HBox(15.0, getLabel("密码"), passwordT),
            HBox(15.0, getLabel("映射路径"), cdPathT),
            HBox(15.0, getLabel("初始命令"), initCommandT),
            HBox(15.0, getLabel("备注"), commentT).apply { alignment = Pos.BOTTOM_LEFT },
            VBox(5.0,
                HBox(tip,test).apply { alignment = Pos.BOTTOM_RIGHT },
                hisTry
                )
        )
        VBox.setMargin(hisTry, Insets(5.0))
        vBox.padding = Insets(30.0)
        stage.apply {
            scene = Scene(vBox,400.0,400.0)
            title = "新建连接"
            icons.add(ImageIcon.B)
            initModality(Modality.APPLICATION_MODAL)
            isResizable = false
            show()
        }
        account?.let {
            nickNameT.text = it.nickname
            hostT.text = it.host
            portT.text = it.port
            usernameT.text = it.username
            passwordT.text = it.password
            cdPathT.text = it.defaultPath
            initCommandT.text = it.initCommand
            commentT.text = it.comments
            hostT.isEditable = false
//            hostT.isDisable = true
//            portT.isDisable = true
            portT.isEditable = false
//            usernameT.isDisable = true
            usernameT.isEditable = false
//            passwordT.isDisable = true
            passwordT.isEditable = false
            test.text = "保存"
            stage.title =if (isChange) "修改信息" else "迁移连接"
//            stage.title = "修改信息"
            test.setOnAction {
                test.isDisable = true
                val account = Account {
                    id = account.id
                    nickname = nickNameT.text
                    username = usernameT.text
                    password = passwordT.text
                    host = hostT.text
                    port = portT.text
                    defaultPath = cdPathT.text
                    initCommand = initCommandT.text
                    comments = commentT.text
                }
                if (isChange){
                    AppData.updateAccount(account)
                    update.set(account.nickname)
                }else{
                    //如果是迁移连接的话要给一个新的ID
                    account.id = KeyUtil.genUniqueKey()
                    success.bind(testConnect(account))
                }
            }
        }
        //这里的success可能有三个值，0：刚初始化；1：连接失败；2：连接成功
        success.addListener { _,_,newValue ->
            if (newValue==1){
                test.isDisable = false
                showTip(false)
            }
            else if (newValue==2){
                test.isDisable = false
                showTip(true)
            }
        }
    }
    private fun getLabel(text:String) = Label(text).apply { style = "-fx-font-size:15px;"}

    /**
     *
     * @param text
     * @param double :宽度
     * @param moveX
     * @return
     */
    private fun getTextFiled(text:String = "",double :Double = 150.0 ,moveX:Double = 0.0):TextField{
        return TextField(text).apply {
            style = "-fx-border-radius: 4px;-fx-border-color: #708090;-fx-background-radius: 4px;-fx-background-insets:0;-fx-background-color:transparent"
            translateX = moveX
            prefWidth = double
        }
    }
    private fun showTip(success:Boolean){
        tip.children.clear()
        hisTry.children.add(Circle(5.0).apply {
            fill = if (success) InitPane.green else InitPane.red
        })
        if (success){
            tip.children.addAll(
                ImageView(ImageIcon.SUCCESS16),
                Label("连接成功").apply { style ="-fx-font-size:15px;-fx-text-fill:#2cc236" }
            )
        }else{
            tip.children.addAll(
                ImageView(ImageIcon.CLOSE16),
                Label("连接失败").apply { style ="-fx-font-size:15px;-fx-text-fill:#d81e06" },
            )
        }
        tip.isVisible = true
        ft.play()
    }

}