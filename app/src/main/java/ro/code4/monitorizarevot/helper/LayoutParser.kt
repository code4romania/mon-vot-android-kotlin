package ro.code4.monitorizarevot.helper

import android.view.View
import android.view.ViewGroup


/**
 * http://android-wtf.com/2013/06/how-to-easily-traverse-any-view-hierarchy-in-android/
 *
 * Parses recursively all nested views of a ViewGroup
 * Returns a single View when process returns true
 */

class LayoutParser private constructor(private val processor: Processor) {
    interface Processor {
        fun process(view: View): Boolean
    }

    fun parse(root: ViewGroup): View? {
        val childCount = root.childCount

        for (i in 0 until childCount) {
            val child = root.getChildAt(i)
            if (processor.process(child)) {
                return child
            }

            if (child is ViewGroup) {
                parse(child)
            }
        }

        return null
    }

    companion object {

        fun build(processor: Processor): LayoutParser {
            return LayoutParser(processor)
        }
    }
}