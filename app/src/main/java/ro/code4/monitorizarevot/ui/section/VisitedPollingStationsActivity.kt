package ro.code4.monitorizarevot.ui.section

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_polling_station.toolbar
import kotlinx.android.synthetic.main.activity_visited_polling_stations.*
import kotlinx.android.synthetic.main.activity_visited_polling_stations.syncButton
import kotlinx.android.synthetic.main.activity_visited_polling_stations.syncGroup
import kotlinx.android.synthetic.main.activity_visited_polling_stations.syncSuccessGroup
import kotlinx.android.synthetic.main.fragment_forms.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.VisitedStationsAdapter
import ro.code4.monitorizarevot.analytics.Event
import ro.code4.monitorizarevot.analytics.Param
import ro.code4.monitorizarevot.analytics.ParamKey
import ro.code4.monitorizarevot.helper.Result
import ro.code4.monitorizarevot.helper.changePollingStation
import ro.code4.monitorizarevot.helper.isOnline
import ro.code4.monitorizarevot.ui.base.BaseActivity

class VisitedPollingStationsActivity : BaseActivity<VisitedPollingStationsViewModel>() {
    override val layout: Int
        get() = R.layout.activity_visited_polling_stations
    override val viewModel: VisitedPollingStationsViewModel by viewModel()

    private lateinit var visitedStationsAdapter: VisitedStationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        visitedStationsAdapter = VisitedStationsAdapter(this) { station ->
            changePollingStation(station.countyOrNull(), station.idPollingStation)
        }
        visitedStations.apply {
            layoutManager = LinearLayoutManager(this@VisitedPollingStationsActivity)
            visitedStations.adapter = visitedStationsAdapter
            addItemDecoration(
                HorizontalDividerItemDecoration.Builder(this@VisitedPollingStationsActivity)
                    .color(Color.TRANSPARENT)
                    .sizeResId(R.dimen.small_margin).build()
            )
        }
        syncButton.setOnClickListener {
            // TODO analytics?
            if (!this.isOnline()) {
                Snackbar.make(
                    syncButton,
                    getString(R.string.form_sync_no_internet),
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.sync()
        }

        viewModel.visitedStations.observe(this, Observer { stationsResult ->
            stationsResult.handle({
                visitedStations.visibility = View.VISIBLE
                loadingIndicator.visibility = View.GONE
                visitedStationsAdapter.submitList(it)
            }, {
                Toast.makeText(this, "Failure: $it", Toast.LENGTH_SHORT).show()
            }, {
                visitedStations.visibility = View.GONE
                loadingIndicator.visibility = View.VISIBLE
            })
        })
        viewModel.hasUnsentData.observe(this, Observer { syncStatusResult ->
            when (syncStatusResult) {
                is Result.Success -> {
                    syncStatusResult.data?.let {
                        syncGroup.visibility = if (it) View.VISIBLE else View.GONE
                        syncSuccessGroup.visibility = if (it) View.GONE else View.VISIBLE
                        Unit
                    } ?: hideSyncNotice()
                }
                else -> {
                    // keep everything hidden until we have a clear status
                    hideSyncNotice()
                }
            }
        })
    }

    private fun hideSyncNotice() {
        syncGroup.visibility = View.GONE
        syncSuccessGroup.visibility = View.GONE
    }
}