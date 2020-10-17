package ro.code4.monitorizarevot.helper

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat

/*
 *  Hide software keyboard if user taps outside the EditText
 *  use inside override fun dispatchTouchEvent()
 */
fun collapseKeyboardIfFocusOutsideEditText(
        motionEvent: MotionEvent,
        oldFocusedView: View,
        newFocusedView: View
) {
    if (motionEvent.action == MotionEvent.ACTION_UP) {
        if (newFocusedView == oldFocusedView) {

            val srcCoordinates = IntArray(2)
            oldFocusedView.getLocationOnScreen(srcCoordinates)

            val rect = Rect(srcCoordinates[0], srcCoordinates[1], srcCoordinates[0] +
                    oldFocusedView.width, srcCoordinates[1] + oldFocusedView.height)

            if (rect.contains(motionEvent.x.toInt(), motionEvent.y.toInt()))
                return
        } else if (newFocusedView is EditText) {
            //  If new focus is other EditText then will not collapse
            return
        }

        // Collapse the keyboard from activity
        ContextCompat.getSystemService(newFocusedView.context, InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(newFocusedView.windowToken, 0)
    }
}