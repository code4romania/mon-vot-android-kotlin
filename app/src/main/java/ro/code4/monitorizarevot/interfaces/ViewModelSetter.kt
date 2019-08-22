package ro.code4.monitorizarevot.interfaces

import androidx.lifecycle.ViewModel

interface ViewModelSetter<out T : ViewModel> {
    @get:ExcludeFromCodeCoverage
    val viewModel: T
}