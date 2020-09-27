package com.checkin.app.checkin.home.model

data class ReferralResultModel(
        val balance: Float,
        val gainedCreditsToday: Float,
        val offersList: List<ReferralModel>,
        val upcomingList: List<ReferralModel>,
        val rewardsList: List<ReferralModel>
) {
    fun formatToday(): String {
        return "+₹ $gainedCreditsToday credits today"
    }
}