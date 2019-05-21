package com.checkin.app.checkin.Data;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Auth.AuthResultModel;
import com.checkin.app.checkin.Inventory.Model.InventoryAvailabilityModel;
import com.checkin.app.checkin.Inventory.Model.InventoryModel;
import com.checkin.app.checkin.Menu.Model.MenuModel;
import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Misc.paytm.PaytmModel;
import com.checkin.app.checkin.Search.SearchResultPeopleModel;
import com.checkin.app.checkin.Search.SearchResultShopModel;
import com.checkin.app.checkin.Shop.Private.Finance.FinanceModel;
import com.checkin.app.checkin.Shop.Private.Invoice.RestaurantSessionModel;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopSessionDetailModel;
import com.checkin.app.checkin.Shop.Private.MemberModel;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.ShopJoin.ShopJoinModel;
import com.checkin.app.checkin.User.ShopCustomerModel;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Waiter.Model.OrderStatusModel;
import com.checkin.app.checkin.Waiter.Model.SessionContactModel;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.checkin.app.checkin.Waiter.Model.WaiterStatsModel;
import com.checkin.app.checkin.Waiter.Model.WaiterTableModel;
import com.checkin.app.checkin.manager.model.ManagerSessionEventModel;
import com.checkin.app.checkin.manager.model.ManagerSessionInvoiceModel;
import com.checkin.app.checkin.manager.model.ManagerStatsModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.checkin.app.checkin.session.model.ActiveSessionModel;
import com.checkin.app.checkin.session.model.CheckoutStatusModel;
import com.checkin.app.checkin.session.model.PromoDetailModel;
import com.checkin.app.checkin.session.model.QRResultModel;
import com.checkin.app.checkin.session.model.RestaurantTableModel;
import com.checkin.app.checkin.session.model.SessionBasicModel;
import com.checkin.app.checkin.session.model.SessionBriefModel;
import com.checkin.app.checkin.session.model.SessionInvoiceModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.checkin.app.checkin.session.model.SessionPromoModel;
import com.checkin.app.checkin.session.model.TrendingDishModel;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebApiService {
    // region AUTH
    @POST("auth/login/")
    Call<AuthResultModel> postLogin(@Body ObjectNode credentials);

    @POST("auth/register/")
    Call<AuthResultModel> postRegister(@Body ObjectNode credentials);

    @PUT("auth/devices/self/update/")
    Call<ObjectNode> postFCMToken(@Body ObjectNode tokenData);
    // endregion

    // region USER
    // region GET
    @GET("users/")
    Call<List<UserModel>> getUsers();

    @GET("users/{user_pk}/")
    Call<UserModel> getNonPersonalUser(@Path("user_pk") long userPk);

    @GET("users/self/")
    Call<UserModel> getPersonalUser();
    // endregion

    @PATCH("users/self/")
    Call<UserModel> postUserData(@Body ObjectNode objectNode);

    @Multipart
    @POST("users/self/picture/")
    Call<GenericDetailModel> postUserProfilePic(@Part MultipartBody.Part pic);
    // endregion

    // region SHOP
    @POST("restaurants/create/")
    Call<GenericDetailModel> postRegisterShop(@Body ShopJoinModel model);

    @GET("restaurants/{shop_id}/")
    Call<RestaurantModel> getRestaurantDetails(@Path("shop_id") long shopId);

    @GET("restaurants/")
    Call<List<RestaurantModel>> getRestaurants();

    @Multipart
    @POST("restaurants/{shop_id}/logo/")
    Call<GenericDetailModel> postRestaurantLogo(
            @Path("shop_id") long shopId, @Part MultipartBody.Part pic);

    @Multipart
    @POST("restaurants/{shop_id}/covers/{index}/")
    Call<GenericDetailModel> postRestaurantCover(
            @Path("shop_id") long shopId, @Path("index") int index, @Part MultipartBody.Part pic);

    @DELETE("restaurants/{shop_id}/covers/{index}/")
    Call<ObjectNode> deleteRestaurantCover(@Path("shop_id") long shopId, @Path("index") int index);

    @GET("restaurants/{shop_id}/edit/")
    Call<RestaurantModel> getRestaurantManageDetails(@Path("shop_id") long shopId);

    @PATCH("restaurants/{shop_id}/edit/")
    Call<ObjectNode> putRestaurantManageDetails(@Path("shop_id") long shopId, @Body RestaurantModel shopData);

    @PUT("restaurants/{shop_id}/verify/")
    Call<ObjectNode> putRestaurantContactVerify(@Path("shop_id") long shopId, @Body ObjectNode data);

    // region SHOP_MEMBERS
    @GET("restaurants/{shop_id}/members/")
    Call<List<MemberModel>> getRestaurantMembers(@Path("shop_id") long shopId);

    @POST("restaurants/{shop_id}/members/")
    Call<ObjectNode> postRestaurantMember(@Path("shop_id") long shopId, @Body MemberModel data);

    @PUT("restaurants/{shop_id}/members/{user_id}/")
    Call<ObjectNode> putRestaurantMember(@Path("shop_id") long shopId, @Path("user_id") long userId, @Body MemberModel data);

    @DELETE("restaurants/{shop_id}/members/{user_id}/")
    Call<ObjectNode> deleteRestaurantMember(@Path("shop_id") long shopId, @Path("user_id") long userId);

    @GET("sessions/restaurants/{restaurant_id}/")
    Call<List<RestaurantSessionModel>> getRestaurantSessionsById(@Path("restaurant_id") long restaurantId, @Query("checked_out_after") String checkedOutAfter, @Query("checked_out_before") String checkedOutBefore);

    @GET("sessions/{session_id}/detail/")
    Call<ShopSessionDetailModel> getShopSessionDetailById(@Path("session_id") long sessionId);

    @GET("restaurants/{restaurant_id}/finance/")
    Call<FinanceModel> getRestaurantFinanceById(@Path("restaurant_id") long restaurantId);

    @PUT("restaurants/{restaurant_id}/finance/")
    Call<GenericDetailModel> setRestaurantFinanceById(@Body FinanceModel financeModel, @Path("restaurant_id") long restaurantId);
    // endregion

    // endregion

    // region SESSION

    @GET("sessions/active/check/")
    Call<SessionBasicModel> getActiveSessionCheck();

    @POST("sessions/customer/new/")
    Call<QRResultModel> postNewCustomerSession(@Body ObjectNode data);

    @GET("sessions/active/")
    Call<ActiveSessionModel> getActiveSession();

    @POST("/sessions/active/concern/")
    Call<ObjectNode> postOrderConcern(@Body ObjectNode data);

    @POST("sessions/active/customers/")
    Call<ObjectNode> postActiveSessionCustomers(@Body ObjectNode data);

    @PUT("sessions/active/customers/self/")
    Call<ObjectNode> putActiveSessionSelfCustomer(@Body ObjectNode data);

    @POST("sessions/active/customers/{user_id}/")
    Call<GenericDetailModel> postActiveSessionCustomerRequest(@Path("user_id") String userId);

    @DELETE("sessions/active/customers/{user_id}/")
    Call<GenericDetailModel> deleteActiveSessionCustomer(@Path("user_id") String userId);

    @GET("sessions/active/events/")
    Call<List<SessionChatModel>> getCustomerSessionChat();

    @POST("sessions/active/message/")
    Call<ObjectNode> postCustomerMessage(@Body ObjectNode data);

    @POST("sessions/active/request/service/")
    Call<ObjectNode> postCustomerRequestService(@Body ObjectNode data);

    @POST("sessions/active/request/checkout/")
    Call<CheckoutStatusModel> postCustomerRequestCheckout(@Body ObjectNode data);

    @POST("sessions/active/pay/paytm/")
    Call<PaytmModel> postPaytmRequest();

    @GET("sessions/active/invoice/")
    Call<SessionInvoiceModel> getActiveSessionInvoice();

    @GET("sessions/{session_id}/brief/")
    Call<SessionBriefModel> getSessionBriefDetail(@Path("session_id") long sessionId);

    @GET("sessions/{session_id}/orders/")
    Call<List<SessionOrderedItemModel>> getSessionOrders(@Path("session_id") long sessionId);

    @GET("sessions/recent/users/self/")
    Call<List<ShopCustomerModel>> getUserRecentCheckins();
    // endregion

    // region WAITER

    @POST("sessions/waiter/new/")
    Call<QRResultModel> postNewWaiterSession(@Body ObjectNode data);

    @GET("restaurants/{restaurant_id}/tables/")
    Call<List<RestaurantTableModel>> getRestaurantTables(@Path("restaurant_id") long restaurantId, @Query("active") boolean query);

    @GET("sessions/{session_id}/events/waiter/")
    Call<List<WaiterEventModel>> getWaiterSessionEvents(@Path("session_id") long sessionId);

    @GET("sessions/waiter/tables/active/")
    Call<List<WaiterTableModel>> getWaiterServedTables();

    @POST("sessions/manage/orders/{order_id}/status/")
    Call<OrderStatusModel> postChangeOrderStatus(@Path("order_id") long orderId, @Body ObjectNode data);

    @POST("sessions/manage/orders/status/")
    Call<List<OrderStatusModel>> postChangeOrderStatusList(@Body List<OrderStatusModel> msOrderStatus);

    @PUT("sessions/manage/events/{event_id}/done/")
    Call<GenericDetailModel> putSessionEventDone(@Path("event_id") long eventId);

    @GET("restaurants/{restaurant_id}/stats/waiter/")
    Call<WaiterStatsModel> getRestaurantWaiterStats(@Path("restaurant_id") long restaurantId);

    @POST("sessions/{session_id}/request/checkout/")
    Call<CheckoutStatusModel> postSessionRequestCheckout(@Path("session_id") long sessionId, @Body ObjectNode data);

    // endregion

    // region MANAGER

    @GET("restaurants/{restaurant_id}/stats/manager/")
    Call<ManagerStatsModel> getRestaurantManagerStats(@Path("restaurant_id") long restaurantId);

    @GET("sessions/{session_id}/events/manager/")
    Call<List<ManagerSessionEventModel>> getManagerSessionEvents(@Path("session_id") long sessionId);

    @GET("sessions/{session_id}/manage/bill/")
    Call<ManagerSessionInvoiceModel> getManagerSessionInvoice(@Path("session_id") long sessionId);

    @PUT("sessions/{session_id}/manage/bill/")
    Call<GenericDetailModel> putManageSessionBill(@Path("session_id") long sessionId, @Body ObjectNode data);

    @POST("sessions/{session_id}/checkout/")
    Call<CheckoutStatusModel> putSessionCheckout(@Path("session_id") long sessionId);

    @POST("sessions/manage/new/")
    Call<QRResultModel> postManageInitiateSession(@Body ObjectNode data);

    @GET("sessions/active/promo/")
    Call<SessionPromoModel> getSessionAppliedPromo();

    @POST("sessions/active/promos/avail/")
    Call<SessionPromoModel> postAvailPromoCode(@Body ObjectNode data);

    @DELETE("sessions/active/promos/remove/")
    Call<ObjectNode> deletePromoCode();

    @DELETE("sessions/active/cancel/checkout/")
    Call<ObjectNode> deleteCheckout();

    // endregion

    // region MENU
    @GET("menus/restaurants/{shop_id}/available/")
    Call<MenuModel> getAvailableMenu(@Path("shop_id") long shopId);

    @POST("sessions/active/order/")
    Call<ArrayNode> postActiveSessionOrders(@Body List<OrderedItemModel> orderedItemModels);

    @GET("sessions/active/orders/")
    Call<List<SessionOrderedItemModel>> getActiveSessionOrders();

    @DELETE("sessions/active/orders/{order_id}/")
    Call<ObjectNode> deleteSessionOrder(@Path("order_id") long order_id);

    @POST("sessions/{session_id}/manage/order/")
    Call<ArrayNode> postSessionManagerOrders(@Path("session_id") long sessionId, @Body List<OrderedItemModel> orderedItemModels);

    @GET("menus/restaurants/{restaurant_id}/manage/available/")
    Call<InventoryModel> getAvailableRestaurantMenu(@Path("restaurant_id") long restaurantId);

    @GET("menus/items/trending/restaurants/{restaurant_id}/")
    Call<List<TrendingDishModel>> getRestaurantTrendingItem(@Path("restaurant_id") long restaurantId);

    @POST("menus/restaurants/{restaurant_id}/manage/items/")
    Call<List<InventoryAvailabilityModel>> postChangeMenuAvailability(@Path("restaurant_id") long restaurantId, @Body List<InventoryAvailabilityModel> msOrderStatus);

    // endregion

    // region MISC
    @GET("accounts/self/")
    Call<List<AccountModel>> getSelfAccounts();

    @GET("search/people/")
    Call<List<SearchResultPeopleModel>> getSearchPeopleResults(
            @Query("search") String query, @Query("friendship_status") String friendshipStatus);

    @GET("search/restaurant/")
    Call<List<SearchResultShopModel>> getSearchShopResults(@Query("search") String query, @Query("has_nonveg") Boolean hasNonVeg, @Query("has_alcohol") Boolean hasAlcohol);
    // endregion

    @POST("sessions/{session_id}/contacts/")
    Call<ObjectNode> postSessionContact(@Path("session_id") long sessionId, @Body SessionContactModel data);

    @GET("sessions/{session_id}/contacts/")
    Call<List<SessionContactModel>> getSessionContactList(@Path("session_id") long sessionId);

    // region payments
    @POST("payments/callback/paytm/")
    Call<ObjectNode> postPaytmCallback(@Body ObjectNode data);

    // endregion

    // region promos
    @GET("promos/")
    Call<List<PromoDetailModel>> getPromoCodes();
}
