package com.app.bustracking.presentation.views.fragments.bottomsheets

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.R
import com.app.bustracking.data.responseModel.Route
import com.app.bustracking.data.responseModel.Stop
import com.app.bustracking.presentation.ui.RoutesMapAdapter
import kotlin.math.abs

class CustomStopsBottomSheet @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val gestureDetector: GestureDetector
    private var isDragging = false
    private var originalY = 0f
    private var currentY = 0f
    private var peekHeight = 300 // Set your initial peek height
    private var view: View
    private var stop: Stop? = null

    private var tvTitle: TextView
    private var tvText: TextView
    private var llRoutes: LinearLayout
    private var lvMsg: LinearLayout
    private var rvMapRoutes: RecyclerView

    init {
        view = inflate(context, R.layout.fragment_stop_map_modal_sheet, this)
        gestureDetector = GestureDetector(context, GestureListener())

        tvTitle = view.findViewById(R.id.tvTitle)
        lvMsg = view.findViewById(R.id.lvMsg)
        tvText = view.findViewById(R.id.tvText)
        llRoutes = view.findViewById(R.id.llRoutes)
        rvMapRoutes = view.findViewById(R.id.rvMapRoutes)
    }


    fun updateRouteAndCallData(stop: Stop) {
        this.stop = stop

        //show main view
        stop.apply {
            tvTitle.text = stop_title
        }

        rvMapRoutes.setHasFixedSize(true)
        rvMapRoutes.adapter = RoutesMapAdapter(listOf(stop)) { stop, position ->

        }
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                originalY = event.rawY
                currentY = originalY
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaY = event.rawY - currentY
                currentY = event.rawY

                if (abs(deltaY) > DRAG_THRESHOLD) {
                    isDragging = true
                    moveSheet(deltaY)
                    return true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    animateSheetToClosestPosition()
                    isDragging = false
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun moveSheet(deltaY: Float) {
        val newY = y + deltaY
        if (newY >= 0 && newY <= (height - peekHeight)) {
            y = newY
        }
    }

    private fun animateSheetToClosestPosition() {
        val finalY = if (y < height / 2) 0f else (height - peekHeight).toFloat()

        val animator = ValueAnimator.ofFloat(y, finalY)
        animator.addUpdateListener {
            y = it.animatedValue as Float
        }
        animator.duration = ANIMATION_DURATION
        animator.start()
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
    }

    companion object {
        private const val DRAG_THRESHOLD = 10
        private const val ANIMATION_DURATION = 300L
    }
}