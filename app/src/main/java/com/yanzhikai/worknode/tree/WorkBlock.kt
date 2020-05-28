package com.yanzhikai.worknode.tree

/**
 * author: jacketyan
 * date: 2020/5/27
 */
abstract class WorkBlock constructor(initialCallbackNum: Int) {

    var dismissCallback: (() -> Unit)? = null

    var callBacks: HashMap<Int, BlockCallback> = HashMap(initialCallbackNum)

    abstract fun init(data: BaseNodeData)

    abstract fun show()

    abstract fun dismiss()

    /**
     * Block回调方式封装
     */
    class BlockCallback(var callBack: (() -> Unit)? = null) {

        fun onCall() {
            callBack?.invoke()
        }
    }
}