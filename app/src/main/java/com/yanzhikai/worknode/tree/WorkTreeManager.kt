package com.yanzhikai.worknode.tree

import io.reactivex.disposables.CompositeDisposable

/**
 * 一棵流程管理树的总管理对象
 * author: jacketyan
 * date: 2020/5/28
 */
class WorkTreeManager(private val root: WorkTreeNode) {

    companion object {
        const val TAG = "WorkTreeManager"
    }

    var curNode: WorkTreeNode? = root

    lateinit var nodeData: BaseNodeData

    private val compositeDisposable = CompositeDisposable()

    /**
     * 开始处理节点
     * @param data BaseNodeData
     */
    fun nodeStart(data: BaseNodeData) {
        nodeData = data
        root.start(data, this)
    }

    internal fun onNodeCall(node: WorkTreeNode?) {
        node?.let {
            compositeDisposable.add(it.start(nodeData,this))
        }
    }

    fun destroy() {
        compositeDisposable.clear()
        curNode?.onFinish()
    }
}