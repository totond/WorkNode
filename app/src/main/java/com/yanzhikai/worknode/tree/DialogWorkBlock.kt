package com.yanzhikai.worknode.tree

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface

/**
 * 用于生成DialogTreeNode转化器
 * 把用户使用的Dialog，转化为DialogTreeNode所需要的DialogNode
 *
 * 使用：重写buildDialog()
 *
 * 自定义Dialog的转化方式，
 * 需要以key，value方式传入callBack：key作为识别callBacks的标识（具体规则是>= 0参考DTNodeCallBack.CallBackType），value则是实现DialogButtonCallback的方式
 */
abstract class DialogWorkBlock<S> constructor(initialCallbackNum: Int) : WorkBlock<S>(initialCallbackNum){

    private var dialog: Dialog? = null

    /**
     * 生成Dialog的方法，使用者可以通过需要重写这个方法进行DialogNode.callBacks与Dialog按钮回调的绑定
     * @param data S 可以根据传入的数据来确定Dialog的表现
     * @return Dialog
     */
    abstract fun buildDialog(data: S): Dialog

    override fun init(data: S) {
        dialog = buildDialog(data)
    }

    override fun show() {
        dialog?.show()
    }

    override fun dismiss() {
        dialog?.dismiss()
    }

    companion object {
        /**
         * 通过传入AlertDialog.Builder来生成Dialog
         * @param alertDialogBuilder Builder
         * @param positive String? 确认按钮文案，为空则是没有确认回调
         * @param negative String? 取消按钮文案，为空则是没有确认回调
         * @return Dialog
         */
        @JvmStatic
        fun createDialog(
                alertDialogBuilder: AlertDialog.Builder,
                positive: String?,
                negative: String?,
                dialogWorkBlock: DialogWorkBlock<*>
        ): Dialog {

            positive?.let {
                val positiveCallback = BlockCallback()
                alertDialogBuilder.setPositiveButton(positive) { _: DialogInterface, _: Int ->
                    positiveCallback.onCall()
                }
                dialogWorkBlock.callBacks[IWorkNode.Type.POSITIVE] = positiveCallback
            }

            negative?.let {
                val negativeCallback = BlockCallback()
                alertDialogBuilder.setNegativeButton(negative) { _: DialogInterface, _: Int ->
                    negativeCallback.onCall()
                }
                dialogWorkBlock.callBacks[IWorkNode.Type.NEGATIVE] = negativeCallback
            }

            return alertDialogBuilder.create()
        }


        /**
         * 通过传入AlertDialog来生成Dialog
         * @param dialog dialog
         * @param positive String? 确认按钮文案，为空则是没有确认回调
         * @param negative String? 取消按钮文案，为空则是没有确认回调
         * @return Dialog
         */
        @JvmStatic
        fun createDialog(dialog: AlertDialog, positive: String?, negative: String?, dialogWorkBlock: DialogWorkBlock<*>): Dialog {
            positive?.let {
                val positiveCallback = BlockCallback()
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, positive) { _: DialogInterface, _: Int ->
                    positiveCallback.onCall()
                }
                dialogWorkBlock.callBacks[IWorkNode.Type.POSITIVE] = positiveCallback
            }

            negative?.let {
                val negativeCallback = BlockCallback()
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, negative) { _: DialogInterface, _: Int ->
                    negativeCallback.onCall()
                }
                dialogWorkBlock.callBacks[IWorkNode.Type.NEGATIVE] = negativeCallback
            }

            return dialog
        }

    }


}