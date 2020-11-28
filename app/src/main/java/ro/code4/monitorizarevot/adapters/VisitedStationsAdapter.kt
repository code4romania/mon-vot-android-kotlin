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
import ro.code4.monitorizarevot.data.pojo.CountyAndPollingStation

class VisitedStationsAdapter(
    private val context: Context,
    private val itemSelected: (CountyAndPollingStation) -> Unit
) : ListAdapter<CountyAndPollingStation, VisitedStationsAdapter.VisitedStationViewHolder>(
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

    private val CountyAndPollingStation.displayName: String
        get() = countyOrNull()?.let {
            context.getString(R.string.polling_station_visited, idPollingStation, it.name)
        } ?: "Not Available" // TODO extract string?

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CountyAndPollingStation>() {
            override fun areItemsTheSame(
                oldItem: CountyAndPollingStation,
                newItem: CountyAndPollingStation
            ): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: CountyAndPollingStation,
                newItem: CountyAndPollingStation
            ): Boolean =
                oldItem.idPollingStation == newItem.idPollingStation &&
                        oldItem.observerArrivalTime == newItem.observerArrivalTime &&
                        oldItem.countyCode == newItem.countyCode &&
                        oldItem.county == newItem.county

        }
    }
}