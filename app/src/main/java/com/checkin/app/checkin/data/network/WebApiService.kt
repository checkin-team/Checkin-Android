package com.checkin.app.checkin.data.network

import com.checkin.app.checkin.Inventory.Model.InventoryAvailabilityModel
import com.checkin.app.checkin.Inventory.Model.InventoryModel
import com.checkin.app.checkin.Search.SearchResultPeopleModel
import com.checkin.app.checkin.Search.SearchResultShopModel
import com.checkin.app.checkin.Shop.Private.Finance.FinanceModel
import com.checkin.app.checkin.Shop.Private.Insight.ShopInsightLoyaltyProgramModel
import com.checkin.app.checkin.Shop.Private.Insight.ShopInsightRevenueModel
import com.checkin.app.checkin.Shop.Private.Invoice.RestaurantAdminStatsModel
import com.checkin.app.checkin.Shop.Private.Invoice.RestaurantSessionModel
import com.checkin.app.checkin.Shop.Private.Invoice.ShopSessionDetailModel
import com.checkin.app.checkin.Shop.Private.Invoice.ShopSessionFeedbackModel
import com.checkin.app.checkin.Shop.Private.MemberModel
import com.checkin.app.checkin.Shop.ShopJoin.ShopJoinModel
import com.checkin.app.checkin.Waiter.models.OrderStatusModel
import com.checkin.app.checkin.Waiter.models.WaiterEventModel
import com.checkin.app.checkin.Waiter.models.WaiterStatsModel
import com.checkin.app.checkin.Waiter.models.WaiterTableModel
import com.checkin.app.checkin.accounts.AccountModel
import com.checkin.app.checkin.auth.AuthRequestModel
import com.checkin.app.checkin.auth.AuthResultModel
import com.checkin.app.checkin.home.model.*
import com.checkin.app.checkin.manager.models.*
import com.checkin.app.checkin.menu.models.*
import com.checkin.app.checkin.misc.models.GenericDetailModel
import com.checkin.app.checkin.misc.paytm.PaytmModel
import com.checkin.app.checkin.payment.models.NewPaytmTransactionModel
import com.checkin.app.checkin.payment.models.NewRazorpayTransactionModel
import com.checkin.app.checkin.payment.models.RazorpayTxnResponseModel
import com.checkin.app.checkin.restaurant.models.RestaurantModel
import com.checkin.app.checkin.restaurant.models.RestaurantServiceModel
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.models.*
import com.checkin.app.checkin.user.models.*
import com.fasterxml.jackson.databind.node.ObjectNode
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface WebApiService {
    // region USER
    // region GET
    @get:GET("users/")
    val users: Call<List<UserModel>>

    @get:GET("users/self/")
    val personalUser: Call<UserModel>

    @GET("users/{user_pk}/")
    fun getNonPersonalUser(@Path("user_pk") userPk: Long): Call<UserModel>
    // endregion

    @PATCH("users/self/")
    fun postUserData(@Body objectNode: ObjectNode): Call<UserModel>

    @Multipart
    @POST("users/self/picture/")
    fun postUserProfilePic(@Part pic: MultipartBody.Part): Call<GenericDetailModel>

    @POST("/users/self/locations/")
    fun postUserLocation(@Body location: UserLocationModel): Call<UserLocationModel>

    @POST("/users/self/referrals/")
    fun postUserReferralCode(@Body data: ObjectNode): Call<UserModel>
    // endregion

    // region SESSION
    @get:GET("sessions/active/status/")
    val activeSessionLiveStatus: Call<ActiveLiveSessionDetailModel>

    @POST("sessions/customer/new/")
    fun postNewCustomerSession(@Body data: ObjectNode): Call<QRResultModel>

    @POST("/sessions/active/concern/")
    fun postOrderConcern(@Body data: ObjectNode): Call<ObjectNode>

    @POST("sessions/active/customers/")
    fun postActiveSessionCustomers(@Body data: ObjectNode): Call<ObjectNode>

    @PUT("sessions/active/customers/self/")
    fun putActiveSessionSelfCustomer(@Body data: ObjectNode): Call<ObjectNode>

    @POST("sessions/active/customers/{user_id}/")
    fun postActiveSessionCustomerRequest(@Path("user_id") userId: Long): Call<GenericDetailModel>

    @DELETE("sessions/active/customers/{user_id}/")
    fun deleteActiveSessionCustomer(@Path("user_id") userId: Long): Call<GenericDetailModel>

    @POST("sessions/active/message/")
    fun postCustomerMessage(@Body data: ObjectNode): Call<ObjectNode>

    @POST("sessions/active/request/service/")
    fun postCustomerRequestService(@Body data: ObjectNode): Call<ObjectNode>

    @POST("sessions/active/request/checkout/")
    fun postCustomerRequestCheckout(@Body data: ObjectNode): Call<CheckoutStatusModel>

    @get:GET("sessions/active/check/")
    val activeSessionCheck: Call<SessionBasicModel>

    @get:GET("sessions/active/")
    val activeSession: Call<ActiveSessionModel>

    @get:GET("sessions/active/events/")
    val customerSessionChat: Call<List<SessionChatModel>>

    @get:GET("sessions/active/invoice/")
    val activeSessionInvoice: Call<SessionInvoiceModel>

    @get:GET("sessions/waiter/tables/active/")
    val waiterServedTables: Call<List<WaiterTableModel>>

    @get:GET("sessions/active/promo/")
    val sessionAppliedPromo: Call<SessionPromoModel>

    @get:GET("sessions/active/orders/")
    val activeSessionOrders: Call<List<SessionOrderedItemModel>>

    @GET("sessions/{session_id}/brief/")
    fun getUserSessionBrief(@Path("session_id") sessionId: Long): Call<UserTransactionBriefModel>

    @GET("sessions/{session_id}/detail/")
    fun getUserSessionDetailById(@Path("session_id") sessionId: Long): Call<UserTransactionDetailsModel>

    @get:GET("sessions/recent/users/self/")
    val userRecentCheckins: Call<List<ShopCustomerModel>>

    @POST("sessions/active/pay/paytm/")
    fun postPaytmRequest(): Call<PaytmModel>

    @get:GET("/sessions/customer/closed/")
    val customerClosedTransactions: Call<List<ClosedSessionBriefModel>>

    @GET("/sessions/customer/closed/{session_id}")
    fun getCustomerClosedSessionDetails(@Path("session_id") sessionId: Long): Call<ClosedSessionDetailsModel>
    // endregion

    // region SCHEDULED_SESSION

    @POST("sessions/customer/schedule/")
    fun postNewScheduledSession(@Body data: NewScheduledSessionModel): Call<NewScheduledSessionModel>

    @PATCH("sessions/customer/scheduled/{session_id}/edit/")
    fun patchScheduledSession(@Path("session_id") sessionId: Long, @Body data: NewScheduledSessionModel): Call<NewScheduledSessionModel>

    @get:GET("sessions/customer/scheduled/")
    val customerScheduledSessions: Call<List<ScheduledLiveSessionDetailModel>>

    @get:DELETE("sessions/customer/scheduled/cart/clear/")
    val deleteCustomerCart: Call<ObjectNode>

    @GET("sessions/customer/scheduled/{session_id}/promo/")
    fun getAvailedPromoForScheduledSession(@Path("session_id") sessionId: Long): Call<SessionPromoModel>

    @POST("sessions/customer/scheduled/{session_id}/promos/avail/")
    fun availPromoForScheduledSession(@Path("session_id") sessionId: Long, @Body data: ObjectNode): Call<SessionPromoModel>

    @DELETE("sessions/customer/scheduled/{session_id}/promos/remove/")
    fun deleteAvailedPromoForScheduledSession(@Path("session_id") sessionId: Long): Call<ObjectNode>

    @POST("sessions/customer/scheduled/{session_id}/pay/paytm/")
    fun postPaytmRequestForScheduledSession(@Path("session_id") sessionId: Long): Call<PaytmModel>

    @GET("sessions/customer/scheduled/{session_id}/detail/")
    fun getCustomerScheduledSessionDetail(@Path("session_id") sessionId: Long): Call<CustomerScheduledSessionDetailModel>

    @DELETE("sessions/customer/scheduled/{session_id}/cancel/")
    fun deleteCustomerScheduledSession(@Path("session_id") sessionId: Long): Call<ObjectNode>

    // endregion

    // region MISC
    @get:GET("accounts/self/")
    val selfAccounts: Call<List<AccountModel>>

    @GET("search/people/")
    fun getSearchPeopleResults(
            @Query("search") query: String?, @Query("friendship_status") friendshipStatus: String?): Call<List<SearchResultPeopleModel>>

    @GET("search/restaurant/")
    fun getSearchShopResults(@Query("search") query: String?, @Query("has_nonveg") hasNonVeg: Boolean?, @Query("has_alcohol") hasAlcohol: Boolean?): Call<List<SearchResultShopModel>>
    // endregion

    // region PROMOS
    @get:GET("promos/")
    val promoCodes: Call<List<PromoDetailModel>>

    @GET("promos/active/restaurants/{restaurant_id}/")
    fun getRestaurantActivePromos(@Path("restaurant_id") restaurantId: Long, @Query("filter_choice") filterChoice: String?): Call<List<PromoDetailModel>>
    // endregion

    // region AUTH
    @POST("/auth/authenticate/")
    fun postAuthenticate(@Body credentials: AuthRequestModel): Call<AuthResultModel>

    @PUT("auth/devices/self/update/")
    fun postFCMToken(@Body tokenData: ObjectNode): Call<ObjectNode>
    // endregion

    // region SHOP
    @get:GET("restaurants/")
    val restaurants: Call<List<com.checkin.app.checkin.Shop.RestaurantModel>>

    @GET("/restaurants/{restaurant_id}/profile/")
    fun getRestaurantProfile(@Path("restaurant_id") restaurantId: Long): Call<RestaurantModel>

    @GET("restaurants/nearby/")
    fun getNearbyRestaurants(@Query("city_id") cityId: Int?): Call<List<NearbyRestaurantModel>>

    @GET("restaurants/{restaurant_id}/brief/")
    fun getRestaurantBriefDetail(@Path("restaurant_id") restaurantId: Long): Call<RestaurantServiceModel>

    @POST("restaurants/create/")
    fun postRegisterShop(@Body model: ShopJoinModel): Call<GenericDetailModel>

    @GET("restaurants/{shop_id}/")
    fun getRestaurantDetails(@Path("shop_id") shopId: Long): Call<com.checkin.app.checkin.Shop.RestaurantModel>

    @Multipart
    @POST("restaurants/{shop_id}/logo/")
    fun postRestaurantLogo(
            @Path("shop_id") shopId: Long, @Part pic: MultipartBody.Part): Call<GenericDetailModel>

    @Multipart
    @POST("restaurants/{shop_id}/covers/{index}/")
    fun postRestaurantCover(
            @Path("shop_id") shopId: Long, @Path("index") index: Int, @Part pic: MultipartBody.Part): Call<GenericDetailModel>

    @DELETE("restaurants/{shop_id}/covers/{index}/")
    fun deleteRestaurantCover(@Path("shop_id") shopId: Long, @Path("index") index: Int): Call<ObjectNode>

    @GET("restaurants/{shop_id}/edit/")
    fun getRestaurantManageDetails(@Path("shop_id") shopId: Long): Call<com.checkin.app.checkin.Shop.RestaurantModel>

    @PATCH("restaurants/{shop_id}/edit/")
    fun putRestaurantManageDetails(@Path("shop_id") shopId: Long, @Body shopData: com.checkin.app.checkin.Shop.RestaurantModel): Call<ObjectNode>

    @PUT("restaurants/{shop_id}/verify/")
    fun putRestaurantContactVerify(@Path("shop_id") shopId: Long, @Body data: ObjectNode): Call<ObjectNode>

    @GET("/restaurants/{restaurant_id}/insights/revenue/")
    fun getShopInsightRevenueDetail(@Path("restaurant_id") shopId: Long): Call<ShopInsightRevenueModel>

    @GET("/restaurants/{restaurant_id}/insights/loyalty/")
    fun getShopInsightLoyaltyDetail(@Path("restaurant_id") shopId: Long): Call<ShopInsightLoyaltyProgramModel>

    // endregion

    // region SHOP_MEMBERS
    @GET("restaurants/{shop_id}/members/")
    fun getRestaurantMembers(@Path("shop_id") shopId: Long): Call<List<MemberModel>>

    @POST("restaurants/{shop_id}/members/")
    fun postRestaurantMember(@Path("shop_id") shopId: Long, @Body data: MemberModel): Call<ObjectNode>

    @PUT("restaurants/{shop_id}/members/{user_id}/")
    fun putRestaurantMember(@Path("shop_id") shopId: Long, @Path("user_id") userId: Long, @Body data: MemberModel): Call<ObjectNode>

    @DELETE("restaurants/{shop_id}/members/{user_id}/")
    fun deleteRestaurantMember(@Path("shop_id") shopId: Long, @Path("user_id") userId: Long): Call<ObjectNode>

    @GET("sessions/restaurants/{restaurant_id}/")
    fun getRestaurantSessionsById(@Path("restaurant_id") restaurantId: Long, @Query("checked_in_after") checkedOutAfter: String?, @Query("checked_in_before") checkedOutBefore: String?): Call<List<RestaurantSessionModel>>

    @GET("sessions/restaurants/{restaurant_id}/scheduled/")
    fun getScheduledSessionsById(@Path("restaurant_id") restaurantId: Long): Call<List<ShopScheduledSessionModel>>

    @GET("restaurants/{restaurant_id}/stats/admin")
    fun getRestaurantAdminStats(@Path("restaurant_id") restaurantId: Long, @Query("checked_in_after") checkedOutAfter: String?, @Query("checked_in_before") checkedOutBefore: String?): Call<RestaurantAdminStatsModel>

    @GET("sessions/manage/{session_id}/detail/")
    fun getShopSessionDetailById(@Path("session_id") sessionId: Long): Call<ShopSessionDetailModel>

    @GET("restaurants/{restaurant_id}/finance/")
    fun getRestaurantFinanceById(@Path("restaurant_id") restaurantId: Long): Call<FinanceModel>

    @POST("sessions/manage/{session_id}/contacts/")
    fun postSessionContact(@Path("session_id") sessionId: Long, @Body data: GuestContactModel): Call<ObjectNode>

    @POST("sessions/manage/{session_id}/contacts/")
    fun postSessionContacts(@Path("session_id") sessionId: Long, @Body data: List<GuestContactModel>): Call<List<GuestContactModel>>

    @GET("sessions/manage/{session_id}/contacts/")
    fun getSessionContactList(@Path("session_id") sessionId: Long): Call<List<GuestContactModel>>

    @PUT("restaurants/{restaurant_id}/finance/")
    fun setRestaurantFinanceById(@Body financeModel: FinanceModel, @Path("restaurant_id") restaurantId: Long): Call<GenericDetailModel>

    @GET("sessions/manage/{session_id}/brief/")
    fun getSessionBriefDetail(@Path("session_id") sessionId: Long): Call<SessionBriefModel>

    @GET("sessions/{session_id}/orders/")
    fun getSessionOrders(@Path("session_id") sessionId: Long): Call<List<SessionOrderedItemModel>>
    // endregion

    // region WAITER

    @POST("sessions/waiter/new/")
    fun postNewWaiterSession(@Body data: ObjectNode): Call<QRResultModel>

    @GET("restaurants/{restaurant_id}/tables/")
    fun getRestaurantTables(@Path("restaurant_id") restaurantId: Long): Call<List<RestaurantTableModel>>

    @GET("restaurants/{restaurant_id}/tables/")
    fun filterRestaurantTables(@Path("restaurant_id") restaurantId: Long, @Query("active") query: Boolean): Call<List<RestaurantTableModel>>

    @GET("sessions/{session_id}/events/waiter/")
    fun getWaiterSessionEvents(@Path("session_id") sessionId: Long): Call<List<WaiterEventModel>>

    @POST("sessions/manage/orders/{order_id}/status/")
    fun postChangeOrderStatus(@Path("order_id") orderId: Long, @Body data: ObjectNode): Call<OrderStatusModel>

    @POST("sessions/{session_id}/manage/orders/status/")
    fun postChangeOrderStatusList(@Path("session_id") sessionId: Long, @Body msOrderStatus: List<OrderStatusModel>): Call<List<OrderStatusModel>>

    @PUT("sessions/manage/events/{event_id}/done/")
    fun putSessionEventDone(@Path("event_id") eventId: Long): Call<GenericDetailModel>

    @GET("restaurants/{restaurant_id}/stats/waiter/")
    fun getRestaurantWaiterStats(@Path("restaurant_id") restaurantId: Long): Call<WaiterStatsModel>

    @POST("sessions/{session_id}/request/checkout/")
    fun postSessionRequestCheckout(@Path("session_id") sessionId: Long, @Body data: ObjectNode): Call<CheckoutStatusModel>

    // endregion

    // region MANAGER

    @GET("restaurants/{restaurant_id}/stats/manager/")
    fun getRestaurantManagerStats(@Path("restaurant_id") restaurantId: Long): Call<ManagerStatsModel>

    @GET("sessions/{session_id}/events/manager/")
    fun getManagerSessionEvents(@Path("session_id") sessionId: Long): Call<List<ManagerSessionEventModel>>

    @GET("sessions/{session_id}/manage/bill/")
    fun getManagerSessionInvoice(@Path("session_id") sessionId: Long): Call<ManagerSessionInvoiceModel>

    @PUT("sessions/{session_id}/manage/bill/")
    fun putManageSessionBill(@Path("session_id") sessionId: Long, @Body data: ObjectNode): Call<GenericDetailModel>

    @POST("sessions/manage/{session_id}/checkout/")
    fun putSessionCheckout(@Path("session_id") sessionId: Long, @Body data: ObjectNode): Call<CheckoutStatusModel>

    @POST("sessions/manage/new/")
    fun postManageInitiateSession(@Body data: ObjectNode): Call<QRResultModel>

    @POST("sessions/active/promos/avail/")
    fun postAvailPromoCode(@Body data: ObjectNode): Call<SessionPromoModel>

    @POST("sessions/manage/{session_id}/table/change/")
    fun postTableSwitch(@Path("session_id") sessionId: Long, @Body data: ObjectNode): Call<ObjectNode>

    @DELETE("sessions/active/promos/remove/")
    fun deletePromoCode(): Call<ObjectNode>

    @DELETE("sessions/active/cancel/checkout/")
    fun deleteCheckout(): Call<ObjectNode>

    @DELETE("sessions/customer/cancel/")
    fun deleteCustomerSessionJoinRequest(): Call<ObjectNode>

    @GET("sessions/manage/scheduled/{session_id}/detail/")
    fun getManageScheduledSessionDetail(@Path("session_id") sessionId: Long): Call<ShopScheduledSessionDetailModel>

    @PATCH("sessions/manage/scheduled/{session_id}/accept/")
    fun patchManageScheduledSessionAccept(@Path("session_id") sessionId: Long, @Body data: PreparationTimeModel): Call<PreparationTimeModel>

    @PATCH("sessions/manage/scheduled/{session_id}/done/")
    fun patchManageScheduledSessionDone(@Path("session_id") sessionId: Long, @Body data: ObjectNode): Call<ScheduledSessionDoneModel>

    @POST("sessions/manage/scheduled/{session_id}/reject/")
    fun postManageScheduledSessionReject(@Path("session_id") sessionId: Long, @Body data: ScheduledSessionCancelModel): Call<GenericDetailModel>

    // endregion

    // region MENU
    @get:GET("sessions/customer/scheduled/cart/")
    val sessionCartStatus: Call<CartStatusModel>

    @get:GET("sessions/customer/scheduled/cart/detail/")
    val sessionCartDetail: Call<CartDetailModel>

    @get:GET("sessions/customer/scheduled/cart/bill")
    val sessionCartBillDetail: Call<CartBillModel>

    @GET("menus/restaurants/{shop_id}/available/")
    fun getAvailableMenu(@Path("shop_id") shopId: Long): Call<MenuModel>

    @POST("sessions/active/order/")
    fun postActiveSessionOrders(@Body orderedItemModels: List<OrderedItemModel>): Call<List<NewOrderModel>>

    @POST("sessions/customer/scheduled/{session_id}/order/")
    fun postScheduledSessionOrders(@Path("session_id") sessionId: Long, @Body orderedItemModels: List<OrderedItemModel>): Call<List<NewOrderModel>>

    @GET("sessions/customer/scheduled/{session_id}/orders/")
    fun getScheduledSessionOrders(@Path("session_id") sessionId: Long): Call<List<SessionOrderedItemModel>>

    @PATCH("sessions/customer/scheduled/{session_id}/orders/{order_id}/")
    fun patchScheduledSessionOrder(@Path("session_id") sessionId: Long, @Path("order_id") orderId: Long, @Body data: ObjectNode): Call<OrderedItemModel>

    @DELETE("sessions/customer/scheduled/{session_id}/orders/{order_id}/")
    fun deleteScheduledSessionOrder(@Path("session_id") sessionId: Long, @Path("order_id") orderId: Long): Call<ObjectNode>

    @DELETE("sessions/active/orders/{order_id}/")
    fun deleteSessionOrder(@Path("order_id") order_id: Long): Call<ObjectNode>

    @POST("sessions/{session_id}/manage/order/")
    fun postSessionManagerOrders(@Path("session_id") sessionId: Long, @Body orderedItemModels: List<OrderedItemModel>): Call<List<NewOrderModel>>

    @GET("menus/restaurants/{restaurant_id}/manage/available/")
    fun getAvailableRestaurantMenu(@Path("restaurant_id") restaurantId: Long): Call<InventoryModel>

    @GET("menus/items/trending/restaurants/{restaurant_id}/")
    fun getRestaurantTrendingItem(@Path("restaurant_id") restaurantId: Long): Call<List<TrendingDishModel>>

    @GET("menus/items/recommended/restaurants/{restaurant_id}/")
    fun getRestaurantRecommendedItems(@Path("restaurant_id") restaurantId: Long): Call<List<TrendingDishModel>>

    @POST("menus/restaurants/{restaurant_id}/manage/items/")
    fun postChangeMenuAvailability(@Path("restaurant_id") restaurantId: Long, @Body msOrderStatus: List<InventoryAvailabilityModel>): Call<List<InventoryAvailabilityModel>>

    // endregion

    // region PAYMENT
    @POST("payments/callback/paytm/")
    fun postPaytmCallback(@Body data: ObjectNode): Call<ObjectNode>

    @POST("payments/callback/razorpay/")
    fun postRazorpayCallback(@Body data: RazorpayTxnResponseModel): Call<ObjectNode>

    @POST("payments/pay/paytm/sessions/{session_id}/")
    fun postNewPaytmTransaction(@Path("session_id") sessionId: Long): Call<NewPaytmTransactionModel>

    @POST("payments/pay/razorpay/sessions/{session_id}/")
    fun postNewRazorpayTransaction(@Path("session_id") sessionId: Long): Call<NewRazorpayTransactionModel>
    //endregion

    //region REVIEW

    @GET("reviews/sessions/{session_id}/")
    fun getRestaurantSessionReviews(@Path("session_id") sessionId: Long): Call<List<ShopSessionFeedbackModel>>

    @POST("reviews/sessions/{session_id}/")
    fun postCustomerReview(@Path("session_id") sessionId: Long, @Body review: NewReviewModel): Call<ObjectNode>

    // endregion

    //region LOCATION

    @get:GET("location/cities/")
    val getCities: Call<List<CityLocationModel>>

    //endregion
}
