package com.yanzhikai.worknode.tree

/**
 * author: jacketyan
 * date: 2020/5/28
 */
class WorkTreeManager(private val root: WorkTreeNode) {

    companion object {
        const val TAG = "WorkTreeManager"
    }

    var curNode: WorkTreeNode? = root

    lateinit var nodeData: BaseNodeData

    fun nodeStart(data: BaseNodeData) {
        nodeData = data
        root.start(data)
    }

    fun onDestroy() {
        root.onDestroy()
    }
}