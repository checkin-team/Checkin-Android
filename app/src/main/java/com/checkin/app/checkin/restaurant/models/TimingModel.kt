package com.checkin.app.checkin.restaurant.models

import android.content.Context
import com.checkin.app.checkin.R
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.add
import com.checkin.app.checkin.utility.toHtml
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class TimingModel(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        val open: Date,
        var close: Date?
) {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @JsonProperty("close")
    fun setCloseDate(closeDate: Date) {
        close = if (closeDate.before(open)) closeDate.add(Calendar.DATE, 1) else closeDate
    }

    fun formatDescription(context: Context): CharSequence {
        val current = Calendar.getInstance().time
        val repDay = if (current.after(close)) "tomorrow" else "today"
        val repOpen = Utils.formatDateTo12HoursTime(open)
        return context.getString(R.string.description_restaurant_closed_open_timing, repDay, repOpen).toHtml()
    }
}