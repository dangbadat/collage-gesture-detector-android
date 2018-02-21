package com.cardinalblue.demo

import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import com.cardinalblue.gesture.GestureDetector
import com.cardinalblue.gesture.IGestureListener
import com.cardinalblue.gesture.MyMotionEvent
import com.cardinalblue.gesture.PointerUtils
import com.cardinalblue.gesture.PointerUtils.DELTA_RADIANS
import com.cardinalblue.gesture.PointerUtils.DELTA_SCALE_X
import com.cardinalblue.gesture.PointerUtils.DELTA_X
import com.cardinalblue.gesture.PointerUtils.DELTA_Y
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class GestureEditorActivity : AppCompatActivity(),
                              IGestureListener {

    private var mEnabled = true

    private val mLog: MutableList<String> = mutableListOf()

    private val mBtnEnable: TextView by lazy { findViewById<TextView>(R.id.btn_enable) }
    private val mBtnClearLog: ImageView by lazy { findViewById<ImageView>(R.id.btn_clear) }
    private val mTxtLog: TextView by lazy { findViewById<TextView>(R.id.text_gesture_test) }

    // Disposables.
    private val mDisposablesOnCreate = CompositeDisposable()

    private val mGestureDetector: GestureDetector by lazy {
        GestureDetector(this@GestureEditorActivity,
                        resources.getDimension(R.dimen.touch_slop),
                        resources.getDimension(R.dimen.tap_slop),
                        resources.getDimension(R.dimen.fling_min_vec),
                        resources.getDimension(R.dimen.fling_max_vec))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_gesture_editor)

        // Bind view.
        mDisposablesOnCreate.add(
            RxView.clicks(mBtnClearLog)
                .subscribe { _ ->
                    clearLog()
                })
        mDisposablesOnCreate.add(
            RxView.clicks(mBtnEnable)
                .subscribe { _ ->
                    setEnable()
                })

        // Gesture listener.
        mGestureDetector.listener = this@GestureEditorActivity
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unbind view.
        mDisposablesOnCreate.clear()

        // Gesture listener.
        mGestureDetector.listener = null
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event?.let {
            mGestureDetector.onTouchEvent(event, null, null)
        } ?: false
    }

    // GestureListener ----------------------------------------------------->

    override fun onActionBegin(event: MyMotionEvent,
                               target: Any?,
                               context: Any?) {
        printLog("--------------")
        printLog("⬇onActionBegin")
    }

    override fun onActionEnd(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        printLog("⬆onActionEnd")
    }

    override fun onSingleTap(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onSingleTap", 1))
    }

    override fun onDoubleTap(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onDoubleTap", 2))
    }

    override fun onMoreTap(event: MyMotionEvent,
                           target: Any?,
                           context: Any?,
                           tapCount: Int) {
        printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onMoreTap", tapCount))
    }

    override fun onLongTap(event: MyMotionEvent,
                           target: Any?,
                           context: Any?) {
        printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onLongTap", 1))
    }

    override fun onLongPress(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        printLog("\uD83D\uDD50 onLongPress")
    }

    override fun onDragBegin(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        printLog("✍️ onDragBegin")
    }

    override fun onDrag(event: MyMotionEvent,
                        target: Any?,
                        context: Any?,
                        startPointer: PointF,
                        stopPointer: PointF) {
        // DO NOTHING.
        printLog("✍️ onDrag")
    }

    override fun onDragEnd(event: MyMotionEvent,
                           target: Any?,
                           context: Any?,
                           startPointer: PointF,
                           stopPointer: PointF) {
        printLog("✍️ onDragEnd")
    }

    override fun onDragFling(event: MyMotionEvent,
                             target: Any?,
                             context: Any?,
                             startPointer: PointF,
                             stopPointer: PointF,
                             velocityX: Float,
                             velocityY: Float) {
        printLog("✍ \uD83C\uDFBC onDragFling")
    }

    override fun onPinchBegin(event: MyMotionEvent,
                              target: Any?,
                              context: Any?,
                              startPointers: Array<PointF>) {
        printLog("\uD83D\uDD0D onPinchBegin")
    }

    override fun onPinch(event: MyMotionEvent,
                         target: Any?,
                         context: Any?,
                         startPointers: Array<PointF>,
                         stopPointers: Array<PointF>) {
        val transform = PointerUtils.getTransformFromPointers(startPointers,
                                                              stopPointers)

        printLog(String.format(Locale.ENGLISH,
                               "\uD83D\uDD0D onPinch: " +
                               "dx=%.1f, dy=%.1f, " +
                               "ds=%.2f, " +
                               "dr=%.2f",
                               transform[DELTA_X], transform[DELTA_Y],
                               transform[DELTA_SCALE_X],
                               transform[DELTA_RADIANS]))
    }

    override fun onPinchFling(event: MyMotionEvent,
                              target: Any?,
                              context: Any?) {
        printLog("\uD83D\uDD0D onPinchFling")
    }

    override fun onPinchEnd(event: MyMotionEvent,
                            target: Any?,
                            context: Any?,
                            startPointers: Array<PointF>,
                            stopPointers: Array<PointF>) {
        printLog("\uD83D\uDD0D onPinchEnd")
    }

    // GestureListener <- end -----------------------------------------------

    private fun printLog(msg: String) {
        mLog.add(msg)
        while (mLog.size > 32) {
            mLog.removeAt(0)
        }

        val builder = StringBuilder()
        mLog.forEach { line ->
            builder.append(line)
            builder.append("\n")
        }

        mTxtLog.text = builder.toString()
    }

    private fun clearLog() {
        mLog.clear()
        mTxtLog.text = getString(R.string.tap_anywhere_to_start)
    }

    private fun setEnable() {
        mEnabled = !mEnabled
        mGestureDetector.setIsMultitouchEnabled(mEnabled)
        mBtnEnable.setText("Multitouch : " + mEnabled.toString())
    }
}
