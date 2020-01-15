package com.checkin.app.checkin.home.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.BaseViewHolder
import com.checkin.app.checkin.accounts.AccountUtil
import com.checkin.app.checkin.home.model.LiveSessionDetailModel
import com.checkin.app.checkin.home.model.SessionType
import com.checkin.app.checkin.restaurant.models.RestaurantLocationModel

class LiveSessionTrackerAdapter(val interactionListener: LiveSessionTrackerInteraction) : RecyclerView.Adapter<LiveSessionViewHolder<LiveSessionDetailModel>>() {
    private val mSessionsData = mutableListOf<LiveSessionDetailModel>()

    fun updateData(data: List<LiveSessionDetailModel>) {
        mSessionsData.clear()
        mSessionsData.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = mSessionsData[position].let {
        when (it.sessionType) {
            SessionType.PREDINING -> R.layout.item_home_session_live_predining
            SessionType.QSR -> R.layout.item_home_session_live_qsr
            SessionType.DINING -> R.layout.item_home_session_live_active
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveSessionViewHolder<LiveSessionDetailModel> = LayoutInflater.from(parent.context).inflate(viewType, parent, false).run {
        when (viewType) {
            R.layout.item_home_session_live_qsr -> QsrLiveSessionViewHolder(this, interactionListener) as LiveSessionViewHolder<LiveSessionDetailModel>
            R.layout.item_home_session_live_predining -> PreDiningLiveSessionViewHolder(this, interactionListener) as LiveSessionViewHolder<LiveSessionDetailModel>
            else -> ActiveLiveSessionViewHolder(this, interactionListener) as LiveSessionViewHolder<LiveSessionDetailModel>
        }
    }

    override fun getItemCount(): Int = mSessionsData.size

    override fun onBindViewHolder(holder: LiveSessionViewHolder<LiveSessionDetailModel>, position: Int) {
        holder.bindData(mSessionsData[position])
    }
}

abstract class LiveSessionViewHolder<in T : LiveSessionDetailModel>(itemView: View, val interactionListener: LiveSessionTrackerInteraction) : BaseViewHolder<T>(itemView) {
    abstract fun getData(): LiveSessionDetailModel?

    val username: String
        get() = AccountUtil.getUsername(itemView.context)

    init {
        itemView.setOnClickListener {
            getData()?.let { interactionListener.onOpenSessionDetails(it) }
        }
    }
}

interface LiveSessionTrackerInteraction {
    fun onOpenSessionDetails(session: LiveSessionDetailModel)
    fun onOpenRestaurantProfile(restaurant: RestaurantLocationModel)
    fun onOpenRestaurantMenu(session: LiveSessionDetailModel)
}
