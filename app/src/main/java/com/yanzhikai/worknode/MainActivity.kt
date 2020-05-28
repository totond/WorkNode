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
        val nodeA = object : WorkTreeNode(dialogA, "a") {
//            override fun showWhat(data: Data1): Int? {
//                data.let {
//                    return if (it.a > 0) IWorkNode.Type.THIS else null
//                }
//            }

            override fun onPositiveCall(data: BaseNodeData) {
                Log.i("jky", "onPositiveCall A")
            }

            override fun callNode(key: Int?) {
                super.callNode(key)
                Log.i("jky", "onCall A $key")
            }

            override fun processNodeData(data: BaseNodeData): Disposable? {
                if (data is Data1) {
                    data.e = 2
                }
                callNode(TYPE_THIS)
                return null
            }
        }

        val dialogB = buildDialog("B", "我是B")
        val nodeB = object : WorkTreeNode(dialogB, "b") {
//            override fun showWhat(data: Data1): Int? {
//                data?.let {
//                    return if (it.b > 0) IWorkNode.Type.THIS else null
//                }
//                return null
//            }
        }

        val dialogC = buildDialog("C", "我是C")
        val nodeC = WorkTreeNode(dialogC, "c")

        val dialogD = buildDialog("D", "我是D")
        val nodeD = object : WorkTreeNode(dialogD, "d") {
            override fun processNodeData(data: BaseNodeData): Disposable? {
                return super.processNodeData(data)
            }
        }

        val dialogE = buildDialog("E", "我是E")
        val nodeE = object : WorkTreeNode(dialogE, "e") {
            override fun processNodeData(data: BaseNodeData): Disposable? {
                if (data is Data1 && data.e > 0) {
                    callNode(TYPE_THIS)
                } else {
                    callNode(TYPE_NEGATIVE)
                }
                return null
            }
        }

        val dialogF = buildDialog("F", "我是F")
        val nodeF = object : WorkTreeNode(dialogF, "f") {
//            override fun showWhat(data: Data1): Int? {
//                data?.let {
//                    return if (it.f > 0) IWorkNode.Type.THIS else null
//                }
//                return null
//            }

            override fun processNodeData(data: BaseNodeData): Disposable? {
                return super.processNodeData(data)
            }
        }

        val singleNode = SimpleWorkTreeNode{
            Toast.makeText(this, "这是最后一发", Toast.LENGTH_SHORT).show()
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
        nodeB.negativeNode = singleNode

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
                tv_test.text = "loading..."
                return Observable.just(true)
                    .delay(2000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{
                        callNode(TYPE_THIS)
                        tv_test.text = "Hello"
                    }
            }
        }

    }
}
