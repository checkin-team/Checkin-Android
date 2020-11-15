package com.checkin.app.checkin.manager.holders

import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.manager.listeners.GuestContactChangeListener
import com.checkin.app.checkin.manager.models.GuestContactModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.utility.setText

@EpoxyModelClass(layout = R.layout.item_manager_add_list)
abstract class GuestInfoModelWithHolder : EpoxyModelWithHolder<GuestInfoModelWithHolder.Holder>() {
    @EpoxyAttribute
    var index: Int = 0

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    internal lateinit var listener: GuestContactChangeListener

    @EpoxyAttribute
    internal lateinit var model: GuestContactModel

    override fun createNewHolder(): Holder = Holder(listener)

    override fun bind(holder: Holder) = holder.apply {
        countNo = index
    }.bindData(model)

    class Holder(val listener: GuestContactChangeListener) : BaseEpoxyHolder<GuestContactModel>() {
        @BindView(R.id.et_manager_guest_contact)
        internal lateinit var etGuestContact: EditText

        @BindView(R.id.et_manager_guest_name)
        internal lateinit var etGuestName: EditText

        @BindView(R.id.tv_manager_guest_count)
        internal lateinit var tvGuestCount: TextView

        var countNo: Int = 0

        lateinit var nameWatcher: TextWatcher
        lateinit var contactWatcher: TextWatcher

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            nameWatcher = etGuestName.doAfterTextChanged {
                listener.updateName(countNo, it?.toString() ?: return@doAfterTextChanged)
            }
            contactWatcher = etGuestContact.doAfterTextChanged {
                listener.updateContact(countNo, it?.toString() ?: return@doAfterTextChanged)
            }
        }

        override fun bindData(data: GuestContactModel) {
            etGuestName.setText(data.name ?: "", nameWatcher)
            etGuestContact.setText(data.phone, contactWatcher)
            tvGuestCount.text = "Guest $countNo"
        }
    }
}