package com.yanzhikai.worknode

import android.app.AlertDialog
import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yanzhikai.worknode.tree.IWorkNode
import com.yanzhikai.worknode.tree.DialogWorkBlock
import com.yanzhikai.worknode.tree.WorkTreeTestUtil
import com.yanzhikai.worknode.tree.WorkTreeNode
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_test.setOnClickListener {
            buildNodes()
        }

    }

    private fun buildNodes() {
        val dialogA = buildDialog1("A", "我是A")
        val data1 = Data1()
        val nodeA = object : WorkTreeNode<Data1, Data1>(dialogA, "a") {
//            override fun showWhat(data: Data1): Int? {
//                data.let {
//                    return if (it.a > 0) IWorkNode.Type.THIS else null
//                }
//            }

            override fun onPositiveCall(data: Data1) {
                Log.i("jky", "onPositiveCall A")
            }

            override fun onNodeCall(key: Int) {
                super.onNodeCall(key)
                Log.i("jky", "onCall A $key")
            }
        }

        val dialogB = buildDialog("B", "我是B")
        val nodeB = object : WorkTreeNode<Data1, Data1>(dialogB, "b") {
//            override fun showWhat(data: Data1): Int? {
//                data?.let {
//                    return if (it.b > 0) IWorkNode.Type.THIS else null
//                }
//                return null
//            }
        }

        val dialogC = buildDialog("C", "我是C")
        val nodeC = WorkTreeNode<Data1, Data1>(dialogC, "c")

        val dialogD = buildDialog("D", "我是D")
        val nodeD = object : WorkTreeNode<Data1, Data1>(dialogD, "d") {
//            override fun showWhat(data: Data1): Int? {
//                data.let {
//                    return if (it.d > 0) IWorkNode.Type.THIS else null
//                }
//            }
        }

        val dialogE = buildDialog("E", "我是E")
        val nodeE = object : WorkTreeNode<Data1, Data1>(dialogE, "e") {
//            override fun showWhat(data: Data1): Int? {
//                data?.let {
//                    return if (it.e > 0) IWorkNode.Type.THIS else null
//                }
//                return null
//            }
        }

        val dialogF = buildDialog("F", "我是F")
        val nodeF = object : WorkTreeNode<Data1, Data1>(dialogF, "f") {
//            override fun showWhat(data: Data1): Int? {
//                data?.let {
//                    return if (it.f > 0) IWorkNode.Type.THIS else null
//                }
//                return null
//            }

            override fun processNodeData(data: Data1): Boolean {
                return super.processNodeData(data)
            }
        }

        /*
                    a
                  c   b
                 f      d
                          e
                            f
         */

        nodeA.positiveNode = nodeB
        nodeA.negativeNode = nodeC
        nodeB.positiveNode = nodeD
        nodeC.negativeNode = nodeF
        nodeD.positiveNode = nodeE
        nodeE.negativeNode = nodeF

        nodeA.childNodes[3] = buildDialogTreeNode("异步方式测试", "异步方式测试")

        nodeA.start(data1)

        Log.i("jky", WorkTreeTestUtil.getOutputTrees(nodeA).toString())

    }

    private fun buildDialog(title: String, content: String): DialogWorkBlock<Data1> {
        return object : DialogWorkBlock<Data1>(2) {
            override fun buildDialog(data: Data1): Dialog {
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

    private fun buildDialog1(title: String, content: String): DialogWorkBlock<Data1> {
        return object : DialogWorkBlock<Data1>(2) {
            override fun buildDialog(data: Data1): Dialog {
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

                callBacks.put(IWorkNode.Type.POSITIVE, positiveCallBack)
                callBacks.put(IWorkNode.Type.NEGATIVE, negativeCallback)
                callBacks.put(3, thirdCallback)

                return builder.create()
            }

        }

    }

    private fun buildDialogTreeNode(title: String, content: String): WorkTreeNode<Data1, Data1> {
        val mockObservable = Observable.just(true).delay(500, TimeUnit.MILLISECONDS)
        val dialogNode = buildDialog(title, content)
        val dialogTreeNode = object : WorkTreeNode<Data1, Data1>(dialogNode) {

        }

        return dialogTreeNode

    }
}
