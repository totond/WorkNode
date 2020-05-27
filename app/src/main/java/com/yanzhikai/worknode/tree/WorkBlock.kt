package com.yanzhikai.worknode.tree

/**
 * author: jacketyan
 * date: 2020/5/27
 */
abstract class WorkBlock<T> constructor(initialCallbackNum: Int) {

    var dismissCallback: (() -> Unit)? = null

    var callBacks: HashMap<Int, DialogWorkBlock.DialogButtonCallback> = HashMap(initialCallbackNum)

    companion object {
        const val TAG = "WorkBlock"
    }

    abstract fun init(data: T)

    abstract fun show()

    abstract fun dismiss()
}