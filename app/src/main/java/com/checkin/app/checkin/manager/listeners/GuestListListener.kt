package com.checkin.app.checkin.manager.listeners

interface GuestListListener {
    fun updateName(index: Int, name: String)
    fun updateContact(index: Int, number: String)
}
