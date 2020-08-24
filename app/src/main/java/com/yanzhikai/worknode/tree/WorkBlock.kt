package com.yanzhikai.worknode.tree

/**
 * 流程Block，用于绑定用户操作
 * author: jacketyan
 * date: 2020/5/27
 */
abstract class WorkBlock constructor(initialCallbackNum: Int) {

    var dismissCallback: (() -> Unit)? = null

    var callBacks: HashMap<Int, BlockCallback> = HashMap(initialCallbackNum)

    /**
     * 初始化用户操作Block,用于绑定回调
     */
    abstract fun init(data: BaseNodeData)

    /**
     * 用户交互操作实现
     */
    abstract fun action()

    /**
     * 结束交互实现
     */
    abstract fun finish()

    /**
     * Block回调方式封装
     */
    class BlockCallback(var callBack: (() -> Unit)? = null) {

        fun onCall() {
            callBack?.invoke()
        }
    }
}