package com.checkin.app.checkin.session.scheduled.fragments

import android.content.DialogInterface
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.parentActivityDelegate
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment

class ChooseQrOrScheduleBottomSheetFragment : BaseBottomSheetFragment() {
    override val rootLayout: Int = R.layout.fragment_session_new_qr_or_schedule
    val listener: NewSessionCreationInteraction by parentActivityDelegate()

    @OnClick(R.id.container_session_choose_qr)
    fun chooseQr() {
        dismiss()
        listener.onChooseQr()
    }

    override fun onCancel(dialog: DialogInterface) {
        listener.onCancelChooseNewSession()
    }

    @OnClick(R.id.container_session_choose_schedule)
    fun chooseScheduler() {
        dismiss()
        listener.onChooseSchedule()
    }

    companion object {
        const val FRAGMENT_TAG = "new_session"
    }
}

interface NewSessionCreationInteraction {
    fun onChooseQr()
    fun onChooseSchedule()
    fun onCancelChooseNewSession()
}