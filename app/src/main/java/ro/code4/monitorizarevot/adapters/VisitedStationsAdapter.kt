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
import ro.code4.monitorizarevot.data.pojo.CountyCommunityAndPollingStation

class VisitedStationsAdapter(
    private val context: Context,
    private val itemSelected: (CountyCommunityAndPollingStation) -> Unit
) : ListAdapter<CountyCommunityAndPollingStation, VisitedStationsAdapter.VisitedStationViewHolder>(
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

    private val CountyCommunityAndPollingStation.displayName: String
        get() = countyOrNull()?.let { county ->
            communityOrNull()?.let {community ->
                context.getString(R.string.polling_station_visited, pollingStationNumber, county.name, community.name)
            }
        } ?: "Not Available" // TODO extract string?

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CountyCommunityAndPollingStation>() {
            override fun areItemsTheSame(
                oldItem: CountyCommunityAndPollingStation,
                newItem: CountyCommunityAndPollingStation
            ): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: CountyCommunityAndPollingStation,
                newItem: CountyCommunityAndPollingStation
            ): Boolean =
                oldItem.pollingStationNumber == newItem.pollingStationNumber &&
                        oldItem.observerArrivalTime == newItem.observerArrivalTime &&
                        oldItem.communityCode == newItem.communityCode &&
                        oldItem.community == newItem.community

        }
    }
}