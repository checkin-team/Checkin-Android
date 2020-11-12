package com.checkin.app.checkin.manager.epoxy

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.manager.listeners.GuestListListener
import com.checkin.app.checkin.manager.models.GuestDetailsModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_manager_add_list)
abstract class GuestInfoModelWithHolder : EpoxyModelWithHolder<GuestInfoModelWithHolder.Holder>() {
    @EpoxyAttribute
    var index: Int = 0

    @EpoxyAttribute
    internal lateinit var listener: GuestListListener

    @EpoxyAttribute
    internal lateinit var model: GuestDetailsModel

    private val nameWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener.updateName(index, s.toString())
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private val contactWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener.updateContact(index, s.toString())
        }

        override fun afterTextChanged(s: Editable?) {
        }

    }

    override fun bind(holder: Holder) {
        holder.guestName.removeTextChangedListener(nameWatcher)
        holder.guestContact.removeTextChangedListener(contactWatcher)
        holder.guestName.setText(model.name)
        holder.guestContact.setText(model.contact)
        holder.guestName.setSelection(model.name.length)
        holder.guestContact.setSelection(model.contact.length)
        holder.guestName.addTextChangedListener(nameWatcher)
        holder.guestContact.addTextChangedListener(contactWatcher)
        holder.guestCount.text = "Guest ${index + 1}"

    }

    override fun unbind(holder: Holder) {
        holder.guestName.removeTextChangedListener(nameWatcher)
        holder.guestContact.removeTextChangedListener(contactWatcher)
    }

    class Holder : BaseEpoxyHolder<GuestDetailsModel>() {
        @BindView(R.id.et_manager_guest_contact)
        internal lateinit var guestContact: EditText

        @BindView(R.id.et_manager_guest_name)
        internal lateinit var guestName: EditText

        @BindView(R.id.tv_manager_guest_count)
        internal lateinit var guestCount: TextView

        override fun bindData(data: GuestDetailsModel) {

        }

    }
}