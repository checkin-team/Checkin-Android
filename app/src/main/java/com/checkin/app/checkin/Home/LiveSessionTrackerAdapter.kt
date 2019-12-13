package com.checkin.app.checkin.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.checkin.app.checkin.Home.fragment.ActiveLiveSessionViewHolder
import com.checkin.app.checkin.Home.fragment.PreDiningLiveSessionViewHolder
import com.checkin.app.checkin.Home.fragment.QsrLiveSessionViewHolder
import com.checkin.app.checkin.Home.model.ActiveLiveSessionDetailModel
import com.checkin.app.checkin.Home.model.LiveSessionDetail
import com.checkin.app.checkin.Home.model.ScheduledLiveSessionDetailModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.BaseViewHolder

class LiveSessionTrackerAdapter : RecyclerView.Adapter<LiveSessionViewHolder<LiveSessionDetail>>() {
    private val mSessionsData = mutableListOf<LiveSessionDetail>()

    fun updateData(data: List<LiveSessionDetail>) {
        mSessionsData.clear()
        mSessionsData.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = mSessionsData[position].let {
        when (it) {
            is ScheduledLiveSessionDetailModel -> if (it.isPreDining) R.layout.item_home_session_live_predining else R.layout.item_home_session_live_qsr
            is ActiveLiveSessionDetailModel -> 0 // TODO active session design
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveSessionViewHolder<LiveSessionDetail> = LayoutInflater.from(parent.context).inflate(viewType, parent, false).run {
        when (viewType) {
            R.layout.item_home_session_live_qsr -> QsrLiveSessionViewHolder(this) as LiveSessionViewHolder<LiveSessionDetail>
            R.layout.item_home_session_live_predining -> PreDiningLiveSessionViewHolder(this) as LiveSessionViewHolder<LiveSessionDetail>
            else -> ActiveLiveSessionViewHolder(this) as LiveSessionViewHolder<LiveSessionDetail>
        }
    }

    override fun getItemCount(): Int = mSessionsData.size

    override fun onBindViewHolder(holder: LiveSessionViewHolder<LiveSessionDetail>, position: Int) {
        holder.bindData(mSessionsData[position])
    }
}

abstract class LiveSessionViewHolder<in T : LiveSessionDetail>(itemView: View) : BaseViewHolder<T>(itemView)
