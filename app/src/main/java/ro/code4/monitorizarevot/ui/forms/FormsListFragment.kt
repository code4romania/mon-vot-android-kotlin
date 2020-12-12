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
        viewModel = getSharedViewModel(from = { requireParentFragment() })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.forms().observe(viewLifecycleOwner, Observer {
            formAdapter.items = it
            updateSyncSuccessfulNotice()
        })
        viewModel.unSyncedDataCount().observe(viewLifecycleOwner, Observer {
            syncGroup.visibility = if (it > 0) View.VISIBLE else View.GONE
            updateSyncSuccessfulNotice()
        })

        viewModel.setTitle(getString(R.string.title_forms_list))

        syncButton.setOnClickListener {
            val unSyncedCount = viewModel.unSyncedDataCount().value ?: 0
            logAnalyticsEvent(Event.MANUAL_SYNC, Param(ParamKey.NUMBER_NOT_SYNCED, unSyncedCount))

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
        val areFormsVisible = viewModel.forms().value?.let { true } ?: false
        viewModel.unSyncedDataCount().value?.let { unSyncedCount ->
            when (if (unSyncedCount > 0) View.VISIBLE else View.GONE) {
                View.VISIBLE -> syncSuccessGroup.visibility = View.GONE
                View.GONE -> syncSuccessGroup.visibility =
                    if (areFormsVisible) View.VISIBLE else View.GONE
            }
        }
    }
}