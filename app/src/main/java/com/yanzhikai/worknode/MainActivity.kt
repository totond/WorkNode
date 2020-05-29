package com.yanzhikai.worknode

import android.app.AlertDialog
import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.yanzhikai.worknode.tree.*
import com.yanzhikai.worknode.tree.IWorkNode.Key.Companion.TYPE_NEGATIVE
import com.yanzhikai.worknode.tree.IWorkNode.Key.Companion.TYPE_THIS
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val data1 = Data1()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_test_old.setOnClickListener {
            buildNodesOld()
        }

        tv_test_new.setOnClickListener {
            buildNodesNew()
        }

    }

    // region old
    private fun buildNodesOld() {
        buildNodesOldA()
    }

    private fun buildNodesOldA(): Boolean {
        if (buildDialogOld(data1.a, "A", "我是A", { buildNodesOldB() }, { buildNodesOldC() })) {
            return true
        } else {
            return buildNodesOldB()
        }
    }

    private fun buildNodesOldB(): Boolean {
        if (buildDialogOld(data1.b, "B", "我是B", { buildNodesOldD() }, null)) {
            return true
        } else {
            return buildNodesOldD()
        }
    }

    private fun buildNodesOldC(): Boolean {
        return buildDialogOld(data1.c, "C", "我是C", null, { buildNodesOldF() })
    }

    private fun buildNodesOldD(): Boolean {
        if (buildDialogOld(data1.d, "D", "我是D", { buildNodesOldF() }, null)) {
            return true
        } else {
            buildNodesOldF()
            return false
        }
    }

    private fun buildNodesOldF() {
        Toast.makeText(this, "F 这是最后一发", Toast.LENGTH_SHORT).show()
    }

    private fun buildDialogOld(
        judge: Int, title: String,
        content: String,
        positive: (() -> Unit)? = null,
        negative: (() -> Unit)? = null
    ): Boolean {
        if (judge > 0) {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle(title)
                .setMessage(content)
                .setPositiveButton("是") { _, _ ->
                    positive?.invoke()
                }
                .setNegativeButton("否") { _, _ ->
                    negative?.invoke()
                }
            builder.create().show()
            return true
        } else {
            return false
        }
    }


    //endregion

    // region new

    private fun buildNodesNew() {
        val dialogA = buildDialog1("A", "我是A")

        val nodeA = object : WorkTreeNode(dialogA, "a") {
            override fun processNodeData(data: BaseNodeData): Disposable? {
                if (data is Data1 && data.a > 0) {
                    callNode(TYPE_THIS)
                } else {
                    callNode(null)
                }
                return null
            }
        }

        val dialogB = buildDialog("B", "我是B")
        val nodeB = object : WorkTreeNode(dialogB, "b") {
            override fun processNodeData(data: BaseNodeData): Disposable? {
                if (data is Data1 && data.b > 0) {
                    callNode(TYPE_THIS)
                } else {
                    callNode(null)
                }
                return null
            }
        }

        val dialogC = buildDialog("C", "我是C")
        val nodeC = object : WorkTreeNode(dialogC, "c") {
            override fun processNodeData(data: BaseNodeData): Disposable? {
                if (data is Data1 && data.c > 0) {
                    callNode(TYPE_THIS)
                } else {
                    callNode(null)
                }
                return null
            }
        }

        val dialogD = buildDialog("D", "我是D")
        val nodeD = object : WorkTreeNode(dialogD, "d") {
            override fun processNodeData(data: BaseNodeData): Disposable? {
                if (data is Data1 && data.d > 0) {
                    callNode(TYPE_THIS)
                } else {
                    callNode(null)
                }
                return null
            }
        }

        val nodeF = SimpleWorkTreeNode("F") {
            if (it is Data1 && it.f > 0) {
                Toast.makeText(this, "F 这是最后一发", Toast.LENGTH_SHORT).show()
            }
        }
        /*
                    a
                  c   b
                 f      d
                          f
         */

        nodeA.positiveNode = nodeB
        nodeA.negativeNode = nodeC
        nodeB.positiveNode = nodeD
        nodeC.negativeNode = nodeF
        nodeD.positiveNode = nodeF

        nodeA.childNodes[3] = buildDialogTreeNode("异步方式测试", "异步方式测试")

        nodeA.start(data1)

        Log.i("jky", WorkTreeTestUtil.getOutputTrees(nodeA).toString())

    }

    private fun buildDialog(title: String, content: String): DialogWorkBlock {
        return object : DialogWorkBlock(2) {
            override fun buildDialog(data: BaseNodeData): Dialog? {
                val builder = AlertDialog.Builder(this@MainActivity)
                return createDialog(
                    builder.setTitle(title).setMessage(content).create(),
                    "是",
                    "否",
                    this
                )
            }

        }
    }

    private fun buildDialog1(title: String, content: String): DialogWorkBlock {
        return object : DialogWorkBlock(2) {
            override fun buildDialog(data: BaseNodeData): Dialog? {
                val builder = AlertDialog.Builder(this@MainActivity)
                val positiveCallBack = BlockCallback()
                val negativeCallback = BlockCallback()
                val thirdCallback = BlockCallback()
                builder.setTitle(title)
                    .setMessage(content)
                    .setPositiveButton("是") { _, _ ->
                        positiveCallBack.onCall()
                    }
                    .setNegativeButton("否") { _, _ ->
                        negativeCallback.onCall()
                    }
                    .setNeutralButton("第三个") { _, _ ->
                        thirdCallback.onCall()
                    }

                callBacks.put(IWorkNode.Key.TYPE_POSITIVE, positiveCallBack)
                callBacks.put(IWorkNode.Key.TYPE_NEGATIVE, negativeCallback)
                callBacks.put(3, thirdCallback)

                return builder.create()
            }

        }

    }

    private fun buildDialogTreeNode(title: String, content: String): WorkTreeNode {
        val dialogNode = buildDialog(title, content)

        return object : WorkTreeNode(dialogNode) {
            override fun processNodeData(data: BaseNodeData): Disposable? {
                tv_test_new.text = "loading..."
                return Observable.just(true)
                    .delay(1300, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        callNode(TYPE_THIS)
                        tv_test_new.text = "新方式"
                    }
            }
        }

    }


    //endregion
}
