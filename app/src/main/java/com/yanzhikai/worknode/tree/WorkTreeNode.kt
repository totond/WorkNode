package com.yanzhikai.worknode.tree

import android.support.annotation.CallSuper
import android.text.TextUtils
import android.util.Log
import com.yanzhikai.worknode.tree.IWorkNode.Key.Companion.TYPE_THIS
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * 流程管理节点
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

    internal lateinit var nodeData: BaseNodeData

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

    var manager: WorkTreeManager? = null

    private val compositeDisposable = CompositeDisposable()

    init {
        if (TextUtils.isEmpty(alias)) {
            alias = id.toString()
        }
    }


    @CallSuper
    final override fun onBlockPositiveCall() {
        onPositiveCall(nodeData)
        onNodeCall(positiveNode)
    }

    @CallSuper
    final override fun onBlockNegativeCall() {
        onNegativeCall(nodeData)
        onNodeCall(negativeNode)

    }

    open fun onPositiveCall(data: BaseNodeData) {}

    open fun onNegativeCall(data: BaseNodeData) {}

    @CallSuper
    override fun callNode(key: Int?) {
        if (key == TYPE_THIS) {
            show()
        } else {
            onNodeCall(childNodes[key])
        }
    }

    override fun onBlockDismissCall() {
        Log.i(TAG, "onDismissCall")
    }

    override fun processNodeData(data: BaseNodeData): Disposable? {
        callNode(TYPE_THIS)
        return null
    }

    open fun show() {
        workBlockLazy.value?.show()
    }
    /**
     * 从这个节点开始
     * @return CompositeDisposable
     */
    fun start(data: BaseNodeData): CompositeDisposable {
        nodeData = data

        processNodeData(data)?.let {
            compositeDisposable.add(it)
        }
        return compositeDisposable
    }

    fun testShow(show: Int?) {
        if (show != null) {
            if (show == IWorkNode.Key.TYPE_THIS) {
                show()
            } else {
                callNode(show)
            }
        }
    }

    internal fun onDestroy() {
        compositeDisposable.clear()
    }

    // region private


    private fun onNodeCall(node: WorkTreeNode?) {
        node?.let {
            it.manager = manager
            manager = null
            compositeDisposable.add(it.start(nodeData))
        }

        if (workBlockLazy.isInitialized()) {
            workBlockLazy.value?.dismiss()
        }
    }

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