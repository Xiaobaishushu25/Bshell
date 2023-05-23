package xbss.config

/**
 * @author  Xbss
 * @create 2023-04-24-21:07
 * @version  1.0
 * @describe :
 * todo：1.InitPane的账户列表是个Vbox而不是listView，当账户太多时下面的会看不见，用listView的话会有空行（可能可以通过调css解决），后续改进的话
 *         可以把vbox放到ScrollPane里面
 *       2.整个软件的size设置的很乱，里面各个组件的宽高什么的好多都是试出来的，不能自由调节
 *       3.Jsch库已经很久没更新了，可以使用一个替代库： https://github.com/mwiede/jsch
 *                               或者阿帕奇的： https://github.com/apache/mina-sshd
 *       4.esc的问题？
 *
 * question：1.单tab使用大概230m内存，添加一千个下载面板后内存会飚到450m左右，使用弱引用技术并提醒gc没有提升，使用对象池缓存面板的话不太好用，
 *             因为面板数量 波动过大。
 *
 *  bug：√1.当在终端输入一行过长时不会自动换行而是从头开始覆盖类似\r的效果--√（通过调整setPtySize与terminalTextBuffer.width）
 *  *      2.偶发刚初始化命令界面会时有是一大块黑的
 *         3.有时子节点加载不出来显示loading并且错位，重新点开一下就行
 *         4.有时只有一个的历史命令列表弹出的高度位置不对
 *         5.终端的滚动条有问题，有时分两端会
 *         6.在终端，中文字符和英文字符宽度不一样，现在可以正常输出英文，但是如果是一行很长的中文，会有应该换行却不换行问题
 *         √7.有时MainWindow注册的快捷键不生效，commandArea.inputRequestFocus()等获得焦点失败 --√(多次注册快捷键导致只有最后一个面板有效)
 *         √8.进度条过高，导致连续的上下行进度条会粘在一起（①调整行间距（但是行间距太大不好）②修改字体的进度条字符，
 *                                    即 █（Unicode U+2588）：Full Block 字符，一个实心的方块，把这个字符削短点（采用此方法））
 * version =  0.22---2023年4月24日21:07:56：增加了文件树多选的功能、复制、粘贴（即移动）文件的功能，重构了sftp通道池，完善了多个文件下载时等待的
 *                    相关代码，增加右键清除所有消息功能
 *            0.32 -- 2023年5月1日21:36:32 ：把消息通知重构了，右边加了个侧边栏，顺便把消息面板背景透明，通知背景色调了透明度
 *            0.33 -- 2023年5月7日14:26:35 ：修复了通知面板遮挡终端滚动条的问题、优化了终端使用体验（滚动速度提升五倍）、修复了终端按下Ctrl
 *                     等键会直接滚动到最下面的问题
 *            0.37 -- 2023年5月9日13:01:30 ：在终端文本区添加了连接断开提示、解决了bug1，但是又发现bug6、添加了重连功能
 *            0.38 -- 2023年5月10日20:07:32 ：解决了bug7
 *            0.40 -- 2023年5月12日00:10:46 ：将文件树加载转移到后台线程，避免界面初始化用时过长出现界面无响应的情况
 *            0.41 -- 2023年5月16日00:38:34 ：解决了bug8，使用削减 █（Unicode U+2588）：Full Block字符的字体FiraFZH2.ttf
 *            0.42 -- 2023年5月17日16:52:21 ：新增了对于ctrl+u(退格)、ctrl+u(剪切(删除)光标处到行首的所有字符)的处理,新增了输入命令框
 *                      ctrl+backspace 全部删除的快捷键
 *            0.43 -- 2023年5月18日16:49:23 ：优化了文件树图标的显示，现在创建名称为png的文件夹不会显示成图片的图标了
 *            测试合并代码
 *
 *
 */
object AppVersion {
    const val version = "0.43"
}