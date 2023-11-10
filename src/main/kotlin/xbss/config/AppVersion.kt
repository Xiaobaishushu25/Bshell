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
 *       4.终端界面的esc的问题（进入vim模式发现的）？--2023年11月8日13:16:18 v0.5.9已解决（添加终端对ESC按键的处理）
 *       5.将h2迁移至sqlite（目前不模块化打包也比较简单，不必要）
 *       6.给文件（夹）右键添加一个属性的面板 -- 2023年9月26日21:31:52
 *
 * question：1.单tab使用大概230m内存，添加一千个下载面板后内存会飚到450m左右，使用弱引用技术并提醒gc没有提升，使用对象池缓存面板的话不太好用，
 *             因为面板数量 波动过大。放到listview试试？
 *
 *  bug：√1.当在终端输入一行过长时不会自动换行而是从头开始覆盖类似\r的效果--√（通过调整setPtySize与terminalTextBuffer.width）
 *  *      2.偶发刚初始化命令界面会时有是一大块黑的
 *         3.有时子节点加载不出来显示loading并且错位，重新点开一下就行
 *         4.有时只有一个的历史命令列表弹出的高度位置不对
 *         5.终端的滚动条有问题，有时分两端会
 *         6.在终端，中文字符和英文字符宽度不一样，现在可以正常输出英文，但是如果是一行很长的中文，会有应该换行却不换行问题
 *         √7.有时MainWindow注册的快捷键不生效，commandArea.inputRequestFocus()等获得焦点失败 --√(多次注册快捷键导致只有最后一个面板有效)
 *         √8.进度条过高，导致连续的上下行进度条会粘在一起 --√（①调整行间距（但是行间距太大不好）②修改字体的进度条字符，
 *                                    即 █（Unicode U+2588）：Full Block 字符，一个实心的方块，把这个字符削短点（采用此方法））
 *         ?√9.打jar包后文件树多选操作不会即刻生效，只有滚动一下或者改变文件树大小后才会将选中item高亮，但是IDEA运行没有此问题。
 *                                   --√(待改善)（每当选中新的item调用一下文件树重绘）
 *             v0.5.5：不使用jar包，使用javapackage打包，未发现此问题，将相关代码注掉了
 *         ?√10.应该是在v0.5.3后，之前版本的逻辑是在每个递归找到指定目录后自动滚动到指定目录，bug表现为滚动完成到指定目录后，会朝下滚动若干距离
 * version =  0.22 -- 2023年4月24日21:07:56：增加了文件树多选的功能、复制、粘贴（即移动）文件的功能，重构了sftp通道池，完善了多个文件下载时等待的
 *                    相关代码，增加右键清除所有消息功能
 *            0.32 -- 2023年5月1日21:36:32 ：把消息通知重构了，右边加了个侧边栏，顺便把消息面板背景透明，通知背景色调了透明度
 *            0.33 -- 2023年5月7日14:26:35 ：修复了通知面板遮挡终端滚动条的问题、优化了终端使用体验（滚动速度提升五倍）、修复了终端按下Ctrl
 *                    等键会直接滚动到最下面的问题
 *            0.37 -- 2023年5月9日13:01:30 ：在终端文本区添加了连接断开提示、解决了bug1，但是又发现bug6、添加了重连功能
 *            0.38 -- 2023年5月10日20:07:32 ：解决了bug7
 *            0.40 -- 2023年5月12日00:10:46 ：将文件树加载转移到后台线程，避免界面初始化用时过长出现界面无响应的情况。
 *            0.41 -- 2023年5月16日00:38:34 ：解决了bug8，使用削减 █（Unicode U+2588）：Full Block字符的字体FiraFZH2.ttf
 *            0.42 -- 2023年5月17日16:52:21 ：新增了对于终端界面对于ctrl+u(退格)、ctrl+u(剪切(删除)光标处到行首的所有字符)的处理,新增了
 *                    输入命令框ctrl+backspace 全部删除的快捷键
 *            0.43 -- 2023年5月18日16:49:23 ：优化了文件树图标的显示，现在创建名称为png的文件夹不会显示成图片的图标了
 *            0.44 -- 2023年5月23日10:54:02 ：优化了文件下载的处理，现在会判断设定文件夹是否存在
 *            0.45 -- 2023年5月28日14:29:55 ：优化了打开本地文件夹处理方式，旧版会直接打开一个新的窗口，优化后会在当前存在窗口新打开一个tab
 *            0.46 -- 2023年6月1日15:07:14 ：优化了不能复制ssh账号连接信息的问题，现将isDisable改为isEditable就可以选中复制了。
 *            0.47 -- 2023年6月6日20:29:08 ：优化了文件树的ui,现在悬浮在cell不会有背景色了。
 *            0.48 -- 2023年6月9日20:07:13 ：解决了bug9，使用treeView.refresh()。
 *            0.49 -- 2023年7月3日15:48:19 ：迁移连接时应该给账户一个新ID。
 *            0.50 -- 2023年7月27日14:35:58 ：修复了连接bug：尝试连接时返回初始化0会被判定连接成功；增加了历史测试记录（红绿点）
 *            0.51 -- 2023年8月19日19:51:34 ：历史命令列表关键字红色显示
 *            0.52 -- 2023年9月20日13:05:55 ：更新至jdk21
 *            0.5.3 -- 2023年10月14日16:23:20 ：优化了MainWindow及背景宽高问题，现在可以自适应窗口大小；现在在未检测到数据库文件时会自动创建；
 *                                             新增了一个全局日志记录。
 *            0.5.4 -- 2023年10月15日14:23:29 ：添加创建文件功能；添加了文本文件查看修改功能；
 *            0.5.5 -- 2023年10月16日20:23:28 ：修复了bug10，给文本编辑器添加了快捷键Ctrl+Y（删除当前行）、Ctrl+S（上传内容）
 *                                                                            Ctrl+D（复制当前行），并增加了缩进功能和自动补全括号。
 *            0.5.6 -- 2023年10月17日15:37:55 ：重构了设置架构
 *            0.5.7 -- 2023年10月18日20:32:54 ：完善了设置功能：支持实时刷新：终端文字大小、终端光标颜色
 *                                                          不支持实时刷新：终端文字颜色，终端背景颜色（不是图片），终端选中颜色
 *            0.5.8 -- 2023年10月19日10:02:08 ：将线程池换为虚拟线程池，改善了bug v0.5.3-1
 *            0.5.9 -- 2023年10月22日20:50:28 ：解决了bug10.
 *            0.6.0 -- 2023年11月8日13:16:18：终端增加了对按键ESC的支持(Vi模式常用)；终端界面支持中文输入。
 *            0.6.0.1 -- 2023年11月10日16:45:39：终端界面支持中文输入后有bug，暂时退回。
 *            0.6.1 -- 2023年11月10日21:38:07：创建文件夹失败会给出提示。
 *
 *
 *
 */
object AppVersion {
    const val VERSION = "0.6.1"
}