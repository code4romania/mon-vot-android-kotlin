package ro.code4.monitorizarevot.helper

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/*
 *  It'll hide software keyboard if user taps outside the input field
 *  use inside override fun dispatchTouchEvent()
 */
fun collapseKeyboardOnOutsideTap(
    activity: Activity?,
    motionEvent: MotionEvent,
    currentFocusedView: View?
) {
    if (currentFocusedView != null
        && (motionEvent.action == MotionEvent.ACTION_UP
            || motionEvent.action == MotionEvent.ACTION_MOVE
            )
        && currentFocusedView is EditText
        && !currentFocusedView.javaClass.name.startsWith("android.webkit.")
    ) {
        val srcCoordinates = IntArray(2)
        // stores the coordinates of current view in srcCoordinates
        currentFocusedView.getLocationOnScreen(srcCoordinates)

        val x = motionEvent.rawX + currentFocusedView.getLeft() - srcCoordinates[0]
        val y = motionEvent.rawY + currentFocusedView.getTop() - srcCoordinates[1]

        // Checks the motion event is inside the currentFocused view
        if (x < currentFocusedView.getLeft() || x > currentFocusedView.getRight()
            || y < currentFocusedView.getTop() || y > currentFocusedView.getBottom()
        ) {
            // Collapse the keyboard from activity
            if (activity != null && activity.window != null && activity.window.decorView != null
            ) {
                val imm = activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(
                    activity.window.decorView
                        .windowToken, 0
                )
            }
        }
    }
}