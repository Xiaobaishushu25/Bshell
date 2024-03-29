package xbss.view

import com.github.difflib.DiffUtils
import com.jcraft.jsch.ChannelSftp
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.concurrent.Task
import javafx.event.Event
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.stage.Stage
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import org.fxmisc.wellbehaved.event.EventPattern
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.Nodes
import xbss.config.FileType
import xbss.config.GlobalLog
import xbss.config.ImageIcon
import xbss.ssh.SSH
import xbss.utils.LsTreeItem
import java.nio.charset.Charset
import java.time.Duration
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern

//class FileContentStage(programmingLanguage:ProgrammingLanguage?=null,private val content:String):Application() {
/**
 * 这里传个ssh仅仅为了回收通道用，感觉不优雅，但是又想把获取通道失败放在外面处理方便弹窗。
 */
class FileContentStage(private val item: LsTreeItem.FileItem, private val ssh: SSH, private val sftp: ChannelSftp) :
    Application() {
    private lateinit var codeArea: CodeArea
    private val encodeListener = SimpleStringProperty("utf-8")
    private var executor: ExecutorService? = null
    private var fontSize = 20
    private var programmingLanguage: ProgrammingLanguage? = null
    private var pattern: Pattern? = null
    private var oriContent: String //初始内容
    private var oriLines: List<String>
    private val sameFlag = SimpleBooleanProperty(true) //内容是否改变
    private var bytes: ByteArray //初试内容字节数组
    private val green: Paint = Paint.valueOf("#00FF00")
    private val orange: Paint = Paint.valueOf("#FFA500")

    init {
        val inputStream = sftp.get(item.path)
        bytes = inputStream.readBytes() // 将 InputStream 中的数据缓存到内存中
        oriContent = bytes.toString(charset("utf-8"))
        oriLines = oriContent.lines()
        when (item.fileType) {
            FileType.JAVA, FileType.KOTLIN -> {
                programmingLanguage = ProgrammingLanguage.JAVA
            }

            FileType.PY -> {
                programmingLanguage = ProgrammingLanguage.PYTHON
            }

            FileType.GO -> {
                programmingLanguage = ProgrammingLanguage.GO
            }

            FileType.RS -> {
                programmingLanguage = ProgrammingLanguage.RUST
            }

            else -> {}
        }
        programmingLanguage?.let { pattern = programmingLanguage!!.getPattern() }
        encodeListener.addListener { _, _, _ ->
            changeEncode()
        }
        inputStream.close()
    }

    private fun closeAll() {
        ssh.releaseChannel(sftp)
//        inputStream.close()
    }

    private fun changeEncode() {
        val format = encodeListener.get()
        oriContent = bytes.toString(Charset.forName(format))
        oriLines = oriContent.lines()
        codeArea.clear()
        codeArea.replaceText(0, 0, oriContent)
    }

    /**
     * 将当前内容上传
     */
    private fun uploadContent() {
        val text = codeArea.text
        val byteInputStream = text.byteInputStream(charset(encodeListener.value))
        try {
            sftp.put(byteInputStream, item.path) //这里put应该是同步的吧
            oriContent = text
            oriLines = oriContent.lines()
            bytes = byteInputStream.readAllBytes()
            byteInputStream.close()
//            codeArea.clear()
//            codeArea.replaceText(0, 0, oriContent)
            sameFlag.value = true
            GlobalLog.writeInfoLog("更新文件${item.path}")
        } catch (e: Exception) {
            byteInputStream.close()
            GlobalLog.writeErrorLog("上传出错 $e")
        }
    }

    /**
     * 将内容重置为最后提交版本
     */
    private fun resetContent() {
        codeArea.clear()
        codeArea.replaceText(0, 0, oriContent)
    }


    override fun start(primaryStage: Stage?) {
        val stage = primaryStage!!
        executor = Executors.newSingleThreadExecutor()
        codeArea = CodeArea()
        val factory = LineNumberFactory.get(codeArea)
        codeArea.paragraphGraphicFactory = factory
        codeArea.style = "-fx-font-size: 20px"
        if (pattern != null) {
            codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(250))
                .retainLatestUntilLater(executor)
                .supplyTask { computeHighlightingAsync() }
                .awaitLatest(codeArea.multiPlainChanges())
                .filterMap { t ->
                    if (t.isSuccess) {
                        return@filterMap Optional.of(t.get())
                    } else {
                        t.failure.printStackTrace()
                        return@filterMap Optional.empty()
                    }
                }
                .subscribe(this::applyHighlighting)
        }
        // call when no longer need it: `cleanupWhenFinished.unsubscribe();`
        //本来可以codeArea = CodeArea("oriContent")来初始化的，但是在内容改变前这样不会高亮关键字
        codeArea.replaceText(0, 0, oriContent)
        val circle = Circle(8.0).apply {
            fill = green
            sameFlag.addListener { _, _, newValue ->
                this.fill = if (newValue) green else orange
            }
        }
        val tip = Label("此文件内容与远程内容相同。").apply {
            style = "-fx-font-size:16px"
            sameFlag.addListener { _, _, newValue ->
                this.text = if (newValue) "此文件内容与远程内容相同。" else "文件内容已经改变，上传？"
            }
        }
        val hPane = Pane()
        val reset = Button("").apply {
            graphic = ImageView(ImageIcon.UPDATE)
            style = "-fx-background-color:null;-fx-cursor:hand"
            disableProperty().bind(sameFlag)
            setOnAction { resetContent() }
            tooltip = Tooltip("Revert to Last Uploaded Version").apply {
                style = "-fx-background-color: #FFFACD;-fx-text-fill:black;-fx-font-size: 13"
            }
        }
        val upload = Button("").apply {
            graphic = ImageView(ImageIcon.UPLOAD)
            style = "-fx-background-color:null;-fx-cursor:hand"
            disableProperty().bind(sameFlag)
            setOnAction { uploadContent() }
            tooltip = Tooltip("Upload").apply {
                style = "-fx-background-color: #FFFACD;-fx-text-fill:black;-fx-font-size: 13"
            }
        }
        val header = HBox(10.0, circle, tip, hPane, reset, upload).apply {
            HBox.setHgrow(hPane, Priority.ALWAYS)
            padding = Insets(5.0, 5.0, 5.0, 20.0)
            prefHeight = 30.0
            alignment = Pos.CENTER
        }
        val bPane = Pane()
        val point = Label().apply {
            style = "-fx-font-size:14px"
            textProperty().bind(
                Bindings.concat(
                    "行",
                    codeArea.currentParagraphProperty().map { it + 1 },
                    " :列",
                    codeArea.caretColumnProperty()
                )
            )
        }
        val selectCharNum = Label().apply {
            style = "-fx-font-size:14px"
            textProperty().bind(Bindings.concat("已选中", codeArea.selectedTextProperty().map { it.length }, "个字符"))
        }
        //enumValues<EncodingFormat>()是获取EncodingFormat枚举所有可能的值
        val encodeBox = ChoiceBox<String>(FXCollections.observableList(enumValues<EncodingFormat>().map { it.format }))
        encodeBox.value = encodeListener.value
        encodeListener.bind(encodeBox.valueProperty())
        val bottom = HBox(10.0, bPane, point, selectCharNum, encodeBox).apply {
            HBox.setHgrow(bPane, Priority.ALWAYS)
            padding = Insets(10.0)
            alignment = Pos.CENTER
        }
        codeArea.prefHeight = 1400.0
        codeArea.prefWidth = 950.0
        val thisScene = Scene(StackPane(VBox(header, VirtualizedScrollPane(codeArea), bottom)), 1400.0, 950.0)
        thisScene.stylesheets.add(this::class.java.getResource("/css/keywords.css")?.toExternalForm())
        stage.apply {
            scene = thisScene
            title = item.path
            icons.add(ImageIcon.B)
            show()
            setOnCloseRequest {
                if (!sameFlag.value) {
                    //todo 你确定要关闭吗
                } else {
                    closeAll()
                }
            }
        }
        initCodeAreaListener()
    }

    private fun initCodeAreaListener() {
        //阻止默认的enter事件（默认插入新行后光标在行首）
        val map = InputMap.consume<Event, Event>(
            EventPattern.anyOf(
                EventPattern.keyPressed(
                    KeyCode.ENTER,
                    KeyCombination.SHORTCUT_ANY,
                    KeyCombination.SHIFT_ANY
                )
            )
        )
        Nodes.addInputMap(codeArea, map)
        codeArea.apply {
            textProperty().addListener { _, _, newValue ->
                sameFlag.value = compareContent(newValue)
            }
            addEventFilter(ScrollEvent.ANY) { e: ScrollEvent ->
                if (e.isControlDown) {
                    if (e.deltaY > 0) {
                        fontSize += 1
                    } else {
                        fontSize -= 1
                    }
                    this.style = "-fx-font-size:" + fontSize + "px"
                }
            }
            setOnKeyPressed { it ->
                if (it.isControlDown) {
                    when (it.code) {
                        KeyCode.D -> { //复制当前行，且保持光标列数不变
                            val caretColumnPosition = codeArea.caretColumn
                            val currentParagraph = codeArea.currentParagraph
                            val end = codeArea.currentLineEndInParargraph
                            val text = codeArea.getText(currentParagraph)
//                            codeArea.insertText(currentParagraph + 1, 0, text + "\n") 存在问题，最后一行无法使用，因为没有currentParagraph + 1
                            codeArea.insertText(currentParagraph, end, "\n$text")
                            codeArea.moveTo(currentParagraph + 1, caretColumnPosition)
                            //todo  滚动一下？最底下加一行不会自动滚，但是没找到类似codeArea.sceneTo(currentParagraph)的api
                        }

                        KeyCode.Y -> { //删除当前行
                            val currentParagraph = codeArea.currentParagraph
                            val end = codeArea.currentLineEndInParargraph
                            codeArea.deleteText(currentParagraph, 0, currentParagraph, end)
                            codeArea.deletePreviousChar() //有一个回车要删除
                            codeArea.currentParagraph
                        }

                        KeyCode.S -> {
                            uploadContent()
                        }

                        else -> {}
                    }
                } else if (it.code == KeyCode.ENTER) {//改写enter行为：插入一行，并缩进（根据按下回车行的行首空白字符串长度）
                    val currentLineText = this.getText(this.currentParagraph)
                    val blank = Regex("^\\s+").find(currentLineText)?.value ?: ""
                    this.insertText(codeArea.caretPosition, "\n$blank")
                }
            }
            //自动补全几个括号
            val autoComplete = mapOf<String, String>(
                Pair("{", "}"),
                Pair("[", "]"),
                Pair("(", ")")
            )
            setOnKeyTyped { it ->
                autoComplete.get(it.character)?.let { right ->
                    val caretPosition = codeArea.caretPosition
                    codeArea.insert(caretPosition, right, "")
                    codeArea.moveTo(codeArea.caretPosition - 1)
                }
            }
        }
    }

    /**
     * 计算内容是否相同
     */
    private fun compareContent(newContent: String): Boolean {
        return DiffUtils.diff(oriLines, newContent.lines()).deltas.isEmpty()
    }

    private fun applyHighlighting(highlighting: StyleSpans<Collection<String>>) {
        codeArea.setStyleSpans(0, highlighting)
    }

    private fun computeHighlighting(text: String): StyleSpans<Collection<String>> {
        val matcher = pattern!!.matcher(text)
        var lastKwEnd = 0
        val spansBuilder = StyleSpansBuilder<Collection<String>>()
        while (matcher.find()) {
            val styleClass =
                (if (matcher.group("KEYWORD") != null) "keyword"
                else if (matcher.group("PAREN") != null) "paren"
                else if (matcher.group("BRACE") != null) "brace"
                else if (matcher.group("BRACKET") != null) "bracket"
                else if (matcher.group("SEMICOLON") != null) "semicolon"
                else if (matcher.group("STRING") != null
                ) "string" else if (matcher.group("COMMENT") != null) "comment" else null)!! /* never happens */
            spansBuilder.add(emptyList<String>(), matcher.start() - lastKwEnd)
            spansBuilder.add(setOf<String>(styleClass), matcher.end() - matcher.start())
            lastKwEnd = matcher.end()
        }
        spansBuilder.add(emptyList<String>(), text.length - lastKwEnd)
        return spansBuilder.create()
    }

    private fun computeHighlightingAsync(): Task<StyleSpans<Collection<String>>> {
        val text = codeArea.text
        val task: Task<StyleSpans<Collection<String>>> = object : Task<StyleSpans<Collection<String>>>() {
            @Throws(Exception::class)
            override fun call(): StyleSpans<Collection<String>> {
                return computeHighlighting(text)
            }
        }
        executor!!.execute(task)
        return task
    }

    enum class ProgrammingLanguage(
        private val KEYWORDS: Array<String>, //关键词
        private val PAREN_PATTERN: String,
        private val BRACE_PATTERN: String,
        private val BRACKET_PATTERN: String,
        private val SEMICOLON_PATTERN: String, //分号
        private val STRING_PATTERN: String,  //字符串内容
        private val COMMENT_PATTERN: String, //注释
    ) {
        JAVA(
            arrayOf(
                "abstract", "assert", "boolean", "break", "byte",
                "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else",
                "enum", "extends", "final", "finally", "float",
                "for", "goto", "if", "implements", "import",
                "instanceof", "int", "interface", "long", "native",
                "new", "package", "private", "protected", "public",
                "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while"
            ),
            "\\(|\\)",
            "\\{|\\}",
            "\\[|\\]",
            "\\;",
            "\"([^\"\\\\]|\\\\.)*\"",
            "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"
        ),
        RUST(
            arrayOf(
                "as", "async", "await", "break", "const", "continue",
                "crate", "dyn", "else", "enum", "extern", "false", "fn", "for",
                "if", "impl", "in", "let", "loop", "match", "mod", "move", "mut",
                "pub", "return", "self", "static", "struct", "super", "trait",
                "true", "type", "unsafe", "use", "where", "while"
            ),
            "\\(|\\)",
            "\\{|\\}",
            "\\[|\\]",
            "\\;",
            "\"([^\"\\\\]|\\\\.)*\"",
            "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"
        ),
        PYTHON(
            arrayOf(
                "and", "as", "assert", "async", "await", "break",
                "class", "continue", "def", "del", "elif", "else", "except",
                "False", "finally", "for", "from", "global", "if", "import",
                "in", "is", "lambda", "None", "nonlocal", "not", "or", "pass",
                "raise", "return", "True", "try", "while", "with", "yield"
            ),
            "\\(|\\)",
            "\\{|\\}",
            "\\[|\\]",
            "\\;",
            "\"([^\"\\\\]|\\\\.)*\"",
            "(\'\'\'[\\s\\S]*?\'\'\'|\"\"\"[\\s\\S]*?\"\"\"|#.*)"
        ),
        GO(
            arrayOf(
                "break", "default", "func", "interface", "select",
                "case", "defer", "go", "map", "struct",
                "chan", "else", "goto", "package", "switch",
                "const", "fallthrough", "if", "range", "type",
                "continue", "for", "import", "return", "var"
            ),
            "\\(|\\)",
            "\\{|\\}",
            "\\[|\\]",
            "\\;",
            "\"([^\"\\\\]|\\\\.)*\"",
            "(//[^\\n]*|/\\*.*?\\*/)"
        );

        private fun getKeywordPattern(): String {
            return "\\b(" + this.KEYWORDS.joinToString("|") + ")\\b"
        }
        fun getPattern(): Pattern {
            return Pattern.compile(
                "(?<KEYWORD>" + this.getKeywordPattern() + ")"
                        + "|(?<PAREN>" + this.PAREN_PATTERN + ")"
                        + "|(?<BRACE>" + this.BRACE_PATTERN + ")"
                        + "|(?<BRACKET>" + this.BRACKET_PATTERN + ")"
                        + "|(?<SEMICOLON>" + this.SEMICOLON_PATTERN + ")"
                        + "|(?<STRING>" + this.STRING_PATTERN + ")"
                        + "|(?<COMMENT>" + this.COMMENT_PATTERN + ")"
            )
        }
    }

    enum class EncodingFormat(val format: String) {
        UTF_8("utf-8"),
        GBK("gbk"),
        ANSI("ANSI");
    }
}
