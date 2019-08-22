package ro.code4.monitorizarevot.interfaces

import androidx.annotation.LayoutRes

interface Layout {
    @get:ExcludeFromCodeCoverage
    @get:LayoutRes
    val layout: Int
}
