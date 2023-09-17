package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.pojo.CountyMunicipalityAndPollingStation

class VisitedStationsAdapter(
    private val context: Context,
    private val itemSelected: (CountyMunicipalityAndPollingStation) -> Unit
) : ListAdapter<CountyMunicipalityAndPollingStation, VisitedStationsAdapter.VisitedStationViewHolder>(
    DIFF_CALLBACK
) {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitedStationViewHolder {
        return VisitedStationViewHolder(
            layoutInflater.inflate(
                R.layout.item_visited_section,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VisitedStationViewHolder, position: Int) {
        val currentStation = getItem(position)
        holder.textView.text = currentStation.displayName
        holder.rowView.setOnClickListener {
            itemSelected(currentStation)
        }
    }

    class VisitedStationViewHolder(
        val rowView: View
    ) : RecyclerView.ViewHolder(rowView) {
        val textView: TextView = rowView.findViewById(R.id.visitedStation)
    }

    private val CountyMunicipalityAndPollingStation.displayName: String
        get() = countyAndMunicipalityAsTextOrNull()?.let {
            context.getString(R.string.polling_station_visited, pollingStationNumber, it.first, it.second)
        } ?: "Not Available" // TODO extract string?

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CountyMunicipalityAndPollingStation>() {
            override fun areItemsTheSame(
                oldItem: CountyMunicipalityAndPollingStation,
                newItem: CountyMunicipalityAndPollingStation
            ): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: CountyMunicipalityAndPollingStation,
                newItem: CountyMunicipalityAndPollingStation
            ): Boolean =
                oldItem.pollingStationNumber == newItem.pollingStationNumber &&
                        oldItem.observerArrivalTime == newItem.observerArrivalTime &&
                        oldItem.countyCode == newItem.countyCode &&
                        oldItem.county == newItem.county

        }
    }
}