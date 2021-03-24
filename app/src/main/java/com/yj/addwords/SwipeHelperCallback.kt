package com.yj.addwords

import android.graphics.Canvas
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_add_list_item.view.*
import java.lang.Float.max
import java.lang.Float.min

class SwipeHelperCallback : ItemTouchHelper.Callback() {
//    이동 방향
    private var currentPosition : Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        var flagSwipe = ItemTouchHelper.START or ItemTouchHelper.END

         /*
         Drag와 Swipe 방향을 결정. Drag는 사용안하니까 0, Swipe의 경우는 LEFT, RIGHT
         모두 사용 가능하도록 설정ㅈ
         */
        return makeMovementFlags(0, flagSwipe)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { }


//    View 전체가 아닌 윗부분만 swipe_view만 swipe 되도록
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentDx = 0f
        previousPosition = viewHolder.adapterPosition
        getDefaultUIUtil().clearView(getView(viewHolder))
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            currentPosition = viewHolder.adapterPosition
            getDefaultUIUtil().onSelected(getView(it))
        }
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        val isClamped = getTag(viewHolder)
//        현재 View가 고정되어있지 않고 사용자가 -clamp 이상 swipe시 isClamped true로 변경 아닐시 false로 변경
        setTag(viewHolder, !isClamped && currentDx <= -clamp)
        return 2f
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (actionState == ACTION_STATE_SWIPE) {
            val view = getView(viewHolder)
            val isClamped = getTag(viewHolder)
            val x =  clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)

            currentDx = x
            getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                view,
                x,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ) : Float {
        // View의 가로 길이의 절반까지만 swipe 되도록
        val min: Float = -view.width.toFloat()/2
        // RIGHT 방향으로 swipe 막기
        val max: Float = 0f

        val x = if (isClamped) {
            // View가 고정되었을 때 swipe되는 영역 제한
            if (isCurrentlyActive) dX - clamp else -clamp
        } else {
            dX
        }

        return min(max(min, x), max)
    }

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        // isClamped를 view의 tag로 관리
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder) : Boolean {
        // isClamped를 view의 tag로 관리
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    // 다른 View가 swipe 되거나 터치되면 고정 해제
    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentPosition == previousPosition)    // 여기밑에 Log넣으니까 막히는듯이 여러개 스와이프가능하게 됨
            return
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            getView(viewHolder).translationX = 0f
            setTag(viewHolder, false)
            previousPosition = null
        }
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder): View {
        return (viewHolder as AddViewHolder).itemView.swipe_view
    }

    /*
    ItemTouchHelper는 일정 범위 밖이나 일정 속도 이상으로 swipe가 될 시 View 밖으로 escape되도록
    구현되어있는데 getSwipeEscapeVelocity(), getSwipeThreshold()를 override하여 escape를 막는다
    */

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }



}