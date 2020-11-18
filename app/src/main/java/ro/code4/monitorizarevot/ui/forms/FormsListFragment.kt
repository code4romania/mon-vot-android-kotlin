package ro.code4.monitorizarevot.ui.forms

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_forms.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.FormDelegationAdapter
import ro.code4.monitorizarevot.analytics.Event
import ro.code4.monitorizarevot.analytics.Param
import ro.code4.monitorizarevot.analytics.ParamKey
import ro.code4.monitorizarevot.helper.isOnline
import ro.code4.monitorizarevot.ui.base.ViewModelFragment


class FormsListFragment : ViewModelFragment<FormsViewModel>() {

    companion object {
        val TAG = FormsListFragment::class.java.simpleName
    }

    override val layout: Int
        get() = R.layout.fragment_forms
    override val screenName: Int
        get() = R.string.analytics_title_forms

    override lateinit var viewModel: FormsViewModel
    private val formAdapter: FormDelegationAdapter by lazy {
        FormDelegationAdapter(
            viewModel::selectForm
        ) {
            viewModel.selectedNotes()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = getSharedViewModel(from = { parentFragment!! })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.forms().observe(this, Observer {
            formAdapter.items = it
            updateSyncSuccessfulNotice()
        })
        viewModel.syncVisibility().observe(this, Observer {
            syncGroup.visibility = it
            updateSyncSuccessfulNotice()
        })

        viewModel.setTitle(getString(R.string.title_forms_list))

        syncButton.setOnClickListener {
            // TODO send number of unsynced items
            logAnalyticsEvent(Event.MANUAL_SYNC, Param(ParamKey.NUMBER_NOT_SYNCED, 0))

            if (!mContext.isOnline()) {
                Snackbar.make(
                    syncButton,
                    getString(R.string.form_sync_no_internet),
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewModel.sync()
        }
        formsList.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = formAdapter
            addItemDecoration(
                HorizontalDividerItemDecoration.Builder(activity)
                    .color(Color.TRANSPARENT)
                    .sizeResId(R.dimen.small_margin).build()
            )
        }
    }

    /**
     * Update the visibility of a successful sync indicator based on the values of the LiveDatas for forms
     * and the sync Button. If we only use the syncVisibility() LiveData then we could get into a situation
     * when the syncVisibility LiveData will trigger before the forms LiveData and we will have an empty
     * screen which shows that everything is synchronized(and the info views will also jump around after the
     * forms will be loaded).
     */
    private fun updateSyncSuccessfulNotice() {
        val visibilityOfSyncBtn = viewModel.syncVisibility().value
        val areFormsVisible = viewModel.forms().value?.let { true } ?: false
        visibilityOfSyncBtn?.let {
            when (it) {
                View.VISIBLE -> syncSuccessGroup.visibility = View.GONE
                View.GONE -> syncSuccessGroup.visibility =
                    if (areFormsVisible) View.VISIBLE else View.GONE
            }
            Unit
        }
    }
}