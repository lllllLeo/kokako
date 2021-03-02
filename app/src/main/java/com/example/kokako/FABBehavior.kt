package com.example.kokako

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton.OnVisibilityChangedListener


class FABBehavior(context: Context?, attrs: AttributeSet?) :
    FloatingActionButton.Behavior(context, attrs) {
    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
    ) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        if (dyConsumed > 0 && child.visibility == View.VISIBLE) {
            child.hide(object : OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton) {
                    super.onHidden(fab)
                    fab.visibility = View.INVISIBLE
                }
            })
        } else if (dyConsumed < 0 && child.visibility == View.INVISIBLE) {
            child.show(object : OnVisibilityChangedListener() {
                override fun onShown(fab: FloatingActionButton) {
                    super.onShown(fab)
                    fab.visibility = View.VISIBLE
                }
            })
        }
    }

//    override fun onDependentViewChanged(parent: CoordinatorLayout,child: FloatingActionButton,dependency: View,
//    ): Boolean {
//        val translationY : Float = Math.min(0f, dependency.translationY - dependency.height)
//        val percentComplete : Float = -translationY / dependency.height
//        val scaleFactor : Float = 1 - percentComplete
//
//        child.scaleX = scaleFactor
//        child.scaleY = scaleFactor
////        child.translationY = translationY
//        return false
//    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int,
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }
}