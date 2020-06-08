package com.yanzhikai.worknode.tree

import com.yanzhikai.worknode.tree.IWorkNode.Key.Companion.TYPE_THIS
import io.reactivex.disposables.Disposable

/**
 * 没有Block的TreeNode，通过输入callBack的方式来展示交互
 * author: jacketyan
 * date: 2020/5/28
 */
open class SimpleWorkTreeNode(alias: String = "", internal var callBack:((data: BaseNodeData) -> Unit)?): WorkTreeNode(null, alias) {

    override fun action() {
        callBack?.invoke(nodeData)
    }

    override fun processNodeData(data: BaseNodeData): Disposable? {
        callNode(TYPE_THIS)
        return null
    }
}