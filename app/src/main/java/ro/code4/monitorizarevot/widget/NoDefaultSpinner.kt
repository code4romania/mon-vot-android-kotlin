package ro.code4.monitorizarevot.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SpinnerAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import ro.code4.monitorizarevot.R
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


/**
 * A modified Spinner that doesn't automatically select the first entry in the list.
 *
 *
 * Shows the prompt if nothing is selected.
 *
 *
 * Limitations: does not display prompt if the entry list is empty.
 */
class NoDefaultSpinner : AppCompatSpinner {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun setAdapter(orig: SpinnerAdapter) {
        val adapter = newProxy(orig)

        super.setAdapter(adapter)

        try {
            val m = AdapterView::class.java.getDeclaredMethod(
                "setNextSelectedPositionInt", Int::class.javaPrimitiveType
            )
            m.isAccessible = true
            m.invoke(this, -1)

            val n = AdapterView::class.java.getDeclaredMethod(
                "setSelectedPositionInt", Int::class.javaPrimitiveType
            )
            n.isAccessible = true
            n.invoke(this, -1)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    private fun newProxy(obj: SpinnerAdapter): SpinnerAdapter {
        return java.lang.reflect.Proxy.newProxyInstance(
            obj.javaClass.classLoader,
            arrayOf<Class<*>>(SpinnerAdapter::class.java),
            SpinnerAdapterProxy(obj)
        ) as SpinnerAdapter
    }

    /**
     * Intercepts getView() to display the prompt if position < 0
     */
    private inner class SpinnerAdapterProxy(private val obj: SpinnerAdapter) :
        InvocationHandler {
        private var getView: Method? = null

        init {
            try {
                this.getView = SpinnerAdapter::class.java.getMethod(
                    "getView", Int::class.javaPrimitiveType, View::class.java, ViewGroup::class.java
                )
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }

        @Throws(Throwable::class)
        override operator fun invoke(proxy: Any, m: Method, args: Array<Any>): Any {
            try {
                return if (m == getView && (args[0] as Int) < 0)
                    getView(args[0] as Int, args[1] as View, args[2] as ViewGroup)
                else
                    m.invoke(obj, args)
            } catch (e: InvocationTargetException) {
                throw e.targetException
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }

        private fun getView(position: Int, convertView: View, parent: ViewGroup): View {

            if (position < 0) {
                val v = (context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE
                ) as LayoutInflater).inflate(
                    android.R.layout.simple_spinner_item, parent, false
                ) as TextView
                v.setTextColor(ContextCompat.getColor(context, R.color.hint))
                v.text = prompt
                return v
            }
            return obj.getView(position, convertView, parent)
        }
    }
}