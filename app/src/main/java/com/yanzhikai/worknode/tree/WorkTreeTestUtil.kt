package com.yanzhikai.worknode.tree

import java.lang.StringBuilder

/**
 * WorkTree测试工具
 */
object WorkTreeTestUtil {

    const val DIALOG_END = "#end"

    const val DIALOG_REPEAT = "#repeat"

    // region 找出每一个可能的分支，中序遍历

    @JvmStatic
    fun getOutputTrees(root: WorkTreeNode): List<String> {
        val resultList = ArrayList<String>()
        val curList = Array(50) { "" }

        checkOneNode(root, curList, resultList, 0)

        return resultList
    }

    private fun checkOneNode(
        node: WorkTreeNode,
        curList: Array<String>,
        resultList: ArrayList<String>,
        index: Int
    ) {
        curList[index] = node.alias

        if (node.negativeNode == null || node.positiveNode == null) {
            resultList.add(getListString(curList, index))
        }
        node.negativeNode?.let {
            checkOneNode(it, curList, resultList, index + 1)
        }

        node.positiveNode?.let {
            checkOneNode(it, curList, resultList, index + 1)
        }


    }

    private fun getListString(array: Array<String>, index: Int): String {
        if (array.isNotEmpty()) {
            val sb = StringBuilder()

            for (i in 0..index) {
                sb.append(array[i])
                sb.append(" ")
            }

            sb.append(DIALOG_END)
            return sb.toString()
        }

        return DIALOG_END
    }

    // endregion

//    fun getTestNode():Pair<DialogNode>{
//
//    }
    @JvmStatic
    fun getTreeString(): String {
        return ""
    }
}