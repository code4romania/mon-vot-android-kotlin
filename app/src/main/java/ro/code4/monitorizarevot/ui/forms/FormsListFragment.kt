package ro.code4.monitorizarevot.ui.forms

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_forms.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.FormDelegationAdapter
import ro.code4.monitorizarevot.ui.base.BaseAnalyticsFragment


class FormsListFragment : BaseAnalyticsFragment<FormsViewModel>() {

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
            formAdapter.items = ArrayList(it)
        })
        viewModel.syncVisibility().observe(this, Observer {
            syncGroup.visibility = it
        })

        viewModel.setTitle(getString(R.string.title_forms_list))

        syncButton.setOnClickListener {
            // TODO send number of unsynced items
            logSyncManuallyEvent(0)

            if (!isOnline(context!!)) {
                Snackbar.make(syncButton, getString(R.string.form_sync_no_internet), Snackbar.LENGTH_SHORT)
                    .show()

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

    private fun logSyncManuallyEvent(numberOfNotSynced: Int) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_event_manual_sync))
        bundle.putInt(FirebaseAnalytics.Param.VALUE, numberOfNotSynced)

        logAnalyticsEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }
}