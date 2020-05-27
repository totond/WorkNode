package com.yanzhikai.worknode.tree

import io.reactivex.Observable

/**
 *
 * @author jacketyan
 * @date 2019/11/12
 */
interface IWorkNode<T> {
    /**
     * Block正向回调
     */
    fun onBlockPositiveCall()

    /**
     * Block负面回调
     */
    fun onBlockNegativeCall()

    /**
     * Block取消回调
     */
    fun onBlockDismissCall()

    /**
     * 所有Node的key按钮回调，需要自定义key值
     * @param key Int参 自定义key值，注意考下面的CallBackType，不要覆盖里面的值，规则：大于0
     */
    fun onNodeCall(key: Int)

    fun processNodeData(data: T): Boolean



    class Type {
        companion object {
            //自身展示回调
            const val THIS = -1
            //确认按钮回调
            const val POSITIVE = -2
            //取消按钮回调
            const val NEGATIVE = -3
        }
    }

}