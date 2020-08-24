package com.yanzhikai.worknode.tree

import android.support.annotation.CallSuper
import android.text.TextUtils
import android.util.Log
import com.yanzhikai.worknode.tree.IWorkNode.Key.Companion.TYPE_THIS
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * 流程管理节点,通过实现{@link #processNodeData(data: BaseNodeData)来实现数据处理和节点触发
 * @author jacketyan
 * @date 2019/11/12
 */
open class WorkTreeNode(
    private val rawWorkBlock: DialogWorkBlock?,
    var alias: String = ""
) : IWorkNode {

    companion object {
        private const val TAG = "WorkTreeNode"
    }

    /**
     * 流转的NodeData
     */
    internal lateinit var nodeData: BaseNodeData

    private lateinit var manager: WorkTreeManager

    private val workBlockLazy = lazy {
        rawWorkBlock?.apply {
            init(nodeData)
            dismissCallback = {
                onBlockDismissCall()
            }

            callBacks.forEach { (key, value) ->
                value.callBack = { callButtonClick(key) }
            }
        }
    }

    internal val id: Long = DTIdGenerator.instance.generate()

    var positiveNode: WorkTreeNode? = null
        set(value) {
            childNodes[IWorkNode.Key.TYPE_POSITIVE] = value
            field = value
        }

    var negativeNode: WorkTreeNode? = null
        set(value) {
            childNodes[IWorkNode.Key.TYPE_NEGATIVE] = value
            field = value
        }

    var childNodes: HashMap<Int, WorkTreeNode?> = HashMap(2)



    init {
        if (TextUtils.isEmpty(alias)) {
            alias = id.toString()
        }
    }


    @CallSuper
    final override fun onBlockPositiveCall() {
        onPositiveCall(nodeData)
        manager.onNodeCall(positiveNode)
    }

    @CallSuper
    final override fun onBlockNegativeCall() {
        onNegativeCall(nodeData)
        manager.onNodeCall(negativeNode)

    }

    open fun onPositiveCall(data: BaseNodeData) {}

    open fun onNegativeCall(data: BaseNodeData) {}

    @CallSuper
    override fun callNode(key: Int?) {
        if (key == TYPE_THIS) {
            action()
        } else {
            manager.onNodeCall(childNodes[key])
        }
    }

    override fun onBlockDismissCall() {
    }

    /**
     * 流程处理回调，需要在这里调用 {@link #callNode(key: Int)}来确定调用自身（Type.THIS）,还是其它
     * 在这里可以进行异步操作，异步操作后请return 对应的Disposable，方便全局取消
     * @param data BaseNodeData 传入的数据
     * @return Disposable?
     */
    override fun processNodeData(data: BaseNodeData): Disposable? {
        callNode(TYPE_THIS)
        return null
    }

    open fun action() {
        workBlockLazy.value?.action()
    }
    /**
     * 从这个节点开始
     * @return CompositeDisposable
     */
    internal fun start(data: BaseNodeData, manager: WorkTreeManager): CompositeDisposable {
        nodeData = data
        this.manager = manager

        return CompositeDisposable().apply {
            Observable.just(data)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    processNodeData(data)?.let {
                        add(it)
                    }
                }
                .subscribe()
                .let {
                    add(it)
                }
        }
    }

    fun testShow(show: Int?) {
        if (show != null) {
            if (show == TYPE_THIS) {
                action()
            } else {
                callNode(show)
            }
        }
    }

    internal fun onFinish() {
        if (workBlockLazy.isInitialized()) {
            workBlockLazy.value?.finish()
        }
    }

    // region private

    /**
     * 根据key值调用对应回调
     * @param key Int
     */
    private fun callButtonClick(key: Int) {
        when (key) {
            IWorkNode.Key.TYPE_POSITIVE -> onBlockPositiveCall()
            IWorkNode.Key.TYPE_NEGATIVE -> onBlockNegativeCall()
            else -> callNode(key)
        }
    }

    //endregion

    final override fun equals(other: Any?): Boolean {
        if (other is WorkTreeNode) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}