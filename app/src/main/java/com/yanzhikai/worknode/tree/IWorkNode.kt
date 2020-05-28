package com.yanzhikai.worknode.tree

import io.reactivex.disposables.Disposable

/**
 *
 * @author jacketyan
 * @date 2019/11/12
 */
interface IWorkNode {
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
    fun callNode(key: Int?)

    /**
     * 流程处理回调，需要在这里调用 {@link #callNode(key: Int)}来确定调用自身（Type.THIS）,还是其它
     * 在这里可以进行异步操作，异步操作后请return 对应的Disposable，方便全局取消
     * @param data BaseNodeData 传入的数据
     * @return Disposable?
     */
    fun processNodeData(data: BaseNodeData): Disposable?



    class Key {
        companion object {
            //自身展示回调
            const val TYPE_THIS = -1
            //正向回调
            const val TYPE_POSITIVE = -2
            //负面回调
            const val TYPE_NEGATIVE = -3
        }
    }

}