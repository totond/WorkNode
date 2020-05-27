package com.yanzhikai.worknode.nodes

/**
 * author: jacketyan
 * date: 2020/5/27
 */
interface IWorkNode {
    /**
     * Block确认回调
     */
    fun onBlockPositiveCall()

    /**
     * Block取消回调
     */
    fun onBlockNegativeCall()

    /**
     * 所有Block的key按钮回调，需要自定义key值
     * @param key Int参 自定义key值，注意考下面的CallBackType，不要覆盖里面的值，规则：大于0
     */
    fun onBlockCall(key: Int)


}