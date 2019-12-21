package com.checkin.app.checkin.session.scheduled

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.minus
import com.checkin.app.checkin.Utility.parentActivityDelegate
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.session.models.ScheduledSessionDetailModel
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.*

class SchedulerBottomSheetFragment : BaseBottomSheetFragment(), TimePickerDialog.OnTimeSetListener {
    override val rootLayout = R.layout.fragment_session_scheduler

    @BindView(R.id.ll_container_scheduler_dates)
    internal lateinit var llDates: LinearLayout
    @BindView(R.id.tv_scheduler_decrement_people)
    internal lateinit var tvDecrementPeople: TextView
    @BindView(R.id.tv_scheduler_increment_people)
    internal lateinit var tvIncrementPeople: TextView
    @BindView(R.id.tv_session_scheduler_people_count)
    internal lateinit var tvPeopleCount: TextView
    @BindView(R.id.btn_scheduler_confirm_schedule)
    internal lateinit var btnConfirm: Button
    @BindView(R.id.tv_scheduler_time_picker)
    internal lateinit var tvTimePicker: TextView

    private var mPeopleCount = 1
    private val mNextCalendars: List<Calendar> = (0 until 5).map { Calendar.getInstance().apply { add(Calendar.DATE, it) } }
    private var mPickedCalendar: Calendar = mNextCalendars[0]
    private var mHolders: List<DateHolder> = emptyList()
    private val mTimePickerDialog = TimePickerDialog.newInstance(
            this, mPickedCalendar[Calendar.HOUR_OF_DAY],
            mPickedCalendar[Calendar.MINUTE], true
    ).apply {
        title = "What time?"
        val minCalendar = Calendar.getInstance()
        val nowMinutes = minCalendar[Calendar.MINUTE]
        val diffMinutes = listOf(15 - nowMinutes, 30 - nowMinutes, 45 - nowMinutes, 60 - nowMinutes).find { it >= 0 }
                ?: 0
        minCalendar.add(Calendar.MINUTE, diffMinutes)
        mPickedCalendar = minCalendar
        setMinTime(minCalendar[Calendar.HOUR_OF_DAY], minCalendar[Calendar.MINUTE], 0)
        setTimeInterval(1, 15)
    }
    private val mListener: SchedulerInteraction by parentActivityDelegate()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mPeopleCount = savedInstanceState?.getInt(SS_PEOPLE_COUNT, 1)
                ?: arguments?.getInt(SS_PEOPLE_COUNT, 1) ?: 1
        mPickedCalendar = (savedInstanceState?.getSerializable(SS_PICKED_CALENDAR)
                ?: arguments?.getSerializable(SS_PICKED_CALENDAR) ?: mNextCalendars[0]) as Calendar
        mHolders = listOf(
                DateHolder(mNextCalendars[0], view.findViewById<ViewStub>(R.id.stub_scheduled_date_1).inflate()),
                DateHolder(mNextCalendars[1], view.findViewById<ViewStub>(R.id.stub_scheduled_date_2).inflate()),
                DateHolder(mNextCalendars[2], view.findViewById<ViewStub>(R.id.stub_scheduled_date_3).inflate()),
                DateHolder(mNextCalendars[3], view.findViewById<ViewStub>(R.id.stub_scheduled_date_4).inflate()),
                DateHolder(mNextCalendars[4], view.findViewById<ViewStub>(R.id.stub_scheduled_date_5).inflate())
        )
        setupUi()
    }

    private fun setupUi() {
        var dayIndex = mPickedCalendar - mNextCalendars[0]
        if (dayIndex > 4) {
            mPickedCalendar = mNextCalendars[0]
            dayIndex = 0
        }
        mHolders[dayIndex].select()
        updateCount(mPeopleCount)
        updateTime(mPickedCalendar[Calendar.HOUR_OF_DAY], mPickedCalendar[Calendar.MINUTE])
        tvDecrementPeople.setOnClickListener { updateCount(mPeopleCount - 1) }
        tvIncrementPeople.setOnClickListener { updateCount(mPeopleCount + 1) }
        tvTimePicker.setOnClickListener { mTimePickerDialog.show(childFragmentManager, null) }
        btnConfirm.setOnClickListener { mListener.onSchedulerSet(mPickedCalendar.time, mPeopleCount) }
    }

    private fun updateCount(newCount: Int) {
        if (newCount <= 0) return
        mPeopleCount = newCount
        tvPeopleCount.text = mPeopleCount.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SS_PEOPLE_COUNT, mPeopleCount)
        outState.putSerializable(SS_PICKED_CALENDAR, mPickedCalendar)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        mPickedCalendar.set(
                mPickedCalendar[Calendar.YEAR], mPickedCalendar[Calendar.MONTH], mPickedCalendar[Calendar.DAY_OF_MONTH],
                hourOfDay, minute, second
        )
        updateTime(hourOfDay, minute)
    }

    private fun updateTime(hourOfDay: Int, minute: Int) {
        tvTimePicker.text = "${hourOfDay.toString().padStart(2, '0')} : ${minute.toString().padStart(2, '0')}"
    }

    private fun resetDateSelection() {
        mHolders.forEach { it.resetSelection() }
    }

    private fun updateCalendar(calendar: Calendar) {
        mPickedCalendar.set(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
    }

    override fun onCancel(dialog: DialogInterface) {
        mListener.onCancelScheduler()
    }

    inner class DateHolder(private val calendar: Calendar, view: View) {
        @BindView(R.id.tv_scheduler_item_date)
        internal lateinit var tvDate: TextView
        @BindView(R.id.tv_scheduler_item_day)
        internal lateinit var tvDay: TextView

        init {
            ButterKnife.bind(this, view)
            initUi()
        }

        private fun initUi() {
            tvDate.text = SimpleDateFormat("dd", Locale.getDefault()).format(calendar.time)
            tvDay.text = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)

            tvDate.setOnClickListener {
                val oldValue = it.isSelected
                resetDateSelection()
                if (!oldValue) {
                    it.isSelected = true
                    tvDate.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                    tvDay.setTextColor(ContextCompat.getColor(context!!, R.color.brownish_grey))
                }
                updateCalendar(calendar)
            }
        }

        fun select() {
            tvDate.performClick()
        }

        fun resetSelection() {
            tvDate.isSelected = false
            tvDate.setTextColor(ContextCompat.getColor(context!!, R.color.brownish_grey))
            tvDate.setTextColor(ContextCompat.getColor(context!!, R.color.pinkish_grey))
        }
    }

    companion object {
        const val FRAGMENT_TAG = "scheduler"

        const val SS_PEOPLE_COUNT = "saved.people_count"
        const val SS_PICKED_CALENDAR = "saved.picked_time"

        fun newInstance(scheduled: ScheduledSessionDetailModel) = SchedulerBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putInt(SS_PEOPLE_COUNT, scheduled.countPeople)
                if (scheduled.plannedDatetime > Calendar.getInstance().time)
                    putSerializable(SS_PICKED_CALENDAR, scheduled.plannedDatetime.toCalendar())
            }
        }
    }
}

interface SchedulerInteraction {
    fun onSchedulerSet(selectedDate: Date, countPeople: Int)
    fun onCancelScheduler()
}

fun Date.toCalendar() = Calendar.getInstance().apply { time = this@toCalendar }