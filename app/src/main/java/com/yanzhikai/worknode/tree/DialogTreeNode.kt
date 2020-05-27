package com.yanzhikai.worknode.tree

import android.support.annotation.CallSuper
import android.text.TextUtils
import android.util.Log
import com.yanzhikai.worknode.tree.IWorkNode.Type.Companion.THIS
import io.reactivex.disposables.CompositeDisposable
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * Dialog管理控件，把每个需要弹出的Dialog的逻辑（为什么弹，弹的内容，点击回调）
 * 使用者可以继承DialogTreeNode，传入data，按需实现各个回调
 * @author jacketyan
 * @date 2019/11/12
 */
open class DialogTreeNode<T>(
    private val rawDialogNode: DialogNode<T>,
    private var data: T,
    var alias: String = ""
) :
    IWorkNode<T> {
    companion object {
        private const val TAG = "DialogTreeNode"
    }

    private val dialogNode by lazy {
        rawDialogNode.init(data)
        rawDialogNode.dismissCallback = {
            onBlockDismissCall()
        }


        rawDialogNode.callBacks.forEach { (key, value) ->
            value.callBack = { callButtonClick(key) }
        }
        rawDialogNode
    }

    internal val id: Long = DTIdGenerator.instance.generate()

    var positiveNode: DialogTreeNode<out Any>? = null
        set(value) {
            childNodes[IWorkNode.Type.POSITIVE] = value
            field = value
        }

    var negativeNode: DialogTreeNode<out Any>? = null
        set(value) {
            childNodes[IWorkNode.Type.NEGATIVE] = value
            field = value
        }

    var childNodes: HashMap<Int, DialogTreeNode<out Any>?> = HashMap(2)

    private var showNode: Int? = IWorkNode.Type.THIS

    private val compositeDisposable = CompositeDisposable()

    init {
        if (TextUtils.isEmpty(alias)) {
            alias = id.toString()
        }
    }

    fun setShowNode(node: Int?){
        showNode = node
    }


    @CallSuper
    final override fun onBlockPositiveCall() {
        onPositiveCall(data)
        callNode(positiveNode)
    }

    @CallSuper
    final override fun onBlockNegativeCall() {
        onNegativeCall(data)
        callNode(negativeNode)

    }

    open fun onPositiveCall(data: T) {}

    open fun onNegativeCall(data: T) {}

    @CallSuper
    override fun onNodeCall(key: Int) {
        if (key == THIS) {
            show()
        } else {
            callNode(childNodes[key])
        }
    }

    override fun onBlockDismissCall() {
        Log.i(TAG, "onDismissCall")
    }

    override fun processNodeData(data: T): Boolean {
        onNodeCall(THIS)
        return false
    }


    open fun show() {
        dialogNode.show()
    }

    /**
     * 从这个节点开始
     * @return CompositeDisposable
     */
    fun start(): CompositeDisposable {

        processNodeData(data)
        return compositeDisposable
    }

    fun testShow(show: Int?) {
        if (show != null) {
            if (show == IWorkNode.Type.THIS) {
                show()
            } else {
                onNodeCall(show)
            }
        }
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

    private fun callNode(node: DialogTreeNode<out Any>?) {
        node?.let {
            compositeDisposable.add(it.start())
        }

        dialogNode.dismiss()
    }

    /**
     * 根据key值调用对应回调
     * @param key Int
     */
    private fun callButtonClick(key: Int) {
        when (key) {
            IWorkNode.Type.POSITIVE -> onBlockPositiveCall()
            IWorkNode.Type.NEGATIVE -> onBlockNegativeCall()
            else -> onNodeCall(key)
        }
    }

    final override fun equals(other: Any?): Boolean {
        if (other is DialogTreeNode<*>) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}