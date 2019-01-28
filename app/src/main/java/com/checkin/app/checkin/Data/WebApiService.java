package com.checkin.app.checkin.Data;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Menu.Model.MenuModel;
import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Notifications.NotificationModel;
import com.checkin.app.checkin.Review.ShopReview.ShopReviewModel;
import com.checkin.app.checkin.Search.SearchResultPeopleModel;
import com.checkin.app.checkin.Search.SearchResultShopModel;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionModel;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.QRResultModel;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Session.Model.SessionBriefModel;
import com.checkin.app.checkin.Session.Model.SessionInvoiceModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Shop.RecentCheckin.Model.RecentCheckinModel;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.ShopJoin.ShopJoinModel;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.Finance.FinanceModel;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.Invoice.RestaurantSessionModel;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.Invoice.ShopSessionDetailModel;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.Invoice.ShopSessionFeedbackModel;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.MemberModel;
import com.checkin.app.checkin.User.Friendship.FriendshipModel;
import com.checkin.app.checkin.User.NonPersonalProfile.ShopCustomerModel;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.checkin.app.checkin.Waiter.Model.WaiterTableModel;
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
    @POST("auth/login/")
    Call<ObjectNode> postLogin(@Body ObjectNode credentials);

    @POST("auth/register/")
    Call<ObjectNode> postRegister(@Body ObjectNode credentials);

    @PUT("auth/devices/self/update/")
    Call<ObjectNode> postFCMToken(@Body ObjectNode tokenData);

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
    Call<ObjectNode> postUserData(@Body ObjectNode objectNode);

    @Multipart
    @POST("users/self/picture/")
    Call<ObjectNode> postUserProfilePic(@Part MultipartBody.Part pic);
    // endregion

    // region FRIENDSHIP
    @GET("friends/self/")
    Call<List<FriendshipModel>> getSelfFriends();

    @GET("friends/{user_pk}/")
    Call<List<FriendshipModel>> getUserFriends(@Path("user_pk") long userPk);

    @POST("friends/add/")
    Call<ObjectNode> addFriend(@Body ObjectNode data);

    @DELETE("friends/remove/{user_pk}/")
    Call<ObjectNode> removeFriend(@Path("user_pk") long userPk);

    @POST("friends/requests/{request_pk}/accept/")
    Call<ObjectNode> acceptFriendRequest(@Path("request_pk") String requestPk);

    @POST("friends/requests/{request_pk}/cancel/")
    Call<ObjectNode> cancelFriendRequest(@Path("request_pk") String requestPk);

    @POST("friends/requests/{request_pk}/reject/")
    Call<ObjectNode> rejectFriendRequest(@Path("request_pk") String requestPk);
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
    Call<ObjectNode> postRestaurantLogo(
            @Path("shop_id") long shopId, @Part MultipartBody.Part pic);

    @Multipart
    @POST("restaurants/{shop_id}/covers/{index}/")
    Call<ObjectNode> postRestaurantCover(
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

    @GET("sessions/{session_id}/feedbacks/")
    Call<List<ShopSessionFeedbackModel>> getShopSessionFeedbackById(@Path("session_id") long sessionId);

    @GET("restaurants/{restaurant_id}/finance/")
    Call<FinanceModel> getRestaurantFinanceById(@Path("restaurant_id") long restaurantId);

    @PUT("restaurants/{restaurant_id}/finance/")
    Call<GenericDetailModel> setRestaurantFinanceById(@Body FinanceModel financeModel, @Path("restaurant_id") long restaurantId);
    // endregion

    // endregion

    // region SESSION

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

    @DELETE("sessions/active/customers/{user_id}/")
    Call<ObjectNode> deleteActiveSessionCustomer(@Path("user_id") String userId);

    @GET("sessions/recent/restaurants/{shop_id}/")
    Call<RecentCheckinModel> getRecentCheckins(@Path("shop_id") long shopId);

    @GET("sessions/recent/users/{user_id}/")
    Call<List<ShopCustomerModel>> getUserCheckins(@Path("user_id") long userId);

    @GET("sessions/active/events/customer/")
    Call<List<SessionChatModel>> getCustomerSessionChat();

    @POST("sessions/active/message/")
    Call<ObjectNode> postCustomerMessage(@Body ObjectNode data);

    @POST("sessions/active/request/service/")
    Call<ObjectNode> postCustomerRequestService(@Body ObjectNode data);

    @POST("sessions/active/request/checkout/")
    Call<ObjectNode> postCustomerRequestCheckout(@Body ObjectNode data);

    @GET("sessions/active/invoice/")
    Call<SessionInvoiceModel> getActiveSessionInvoice();

    @GET("sessions/{session_id}/brief/")
    Call<SessionBriefModel> getSessionBriefDetail(@Path("session_id") long sessionId);

    @GET("sessions/{session_id}/orders/")
    Call<List<SessionOrderedItemModel>> getSessionOrders(@Path("session_id") long sessionId);

    @GET("sessions/{session_id}/manager/events")
    Call<List<WaiterEventModel>> getManagerSessionEvents(@Path("session_id") long sessionId);

    // endregion

    // region WAITER

    @POST("sessions/waiter/new/")
    Call<QRResultModel> postNewWaiterSession(@Body ObjectNode data);

    @GET("sessions/active/restaurants/{restaurant_id}/")
    Call<List<RestaurantTableModel>> getRestaurantActiveTables(@Path("restaurant_id") long restaurantId);

    @GET("sessions/{session_id}/waiter/events/")
    Call<List<WaiterEventModel>> getWaiterSessionEvents(@Path("session_id") long sessionId);

    @GET("sessions/waiter/tables/active/")
    Call<List<WaiterTableModel>> getWaiterServedTables();

    @POST("sessions/manage/orders/{order_id}/status/")
    Call<ObjectNode> postChangeOrderStatus(@Path("order_id") long orderId, @Body ObjectNode data);

    @PUT("sessions/manage/events/{event_id}/done/")
    Call<GenericDetailModel> putSessionEventDone(@Path("event_id") long eventId);

    // endregion

    // region MENU
    @GET("menus/restaurants/{shop_id}/available/")
    Call<MenuModel> getAvailableMenu(@Path("shop_id") String shopId);

    @POST("sessions/active/order/")
    Call<ArrayNode> postActiveSessionOrders(@Body List<OrderedItemModel> orderedItemModels);

    @GET("sessions/active/orders/")
    Call<List<SessionOrderedItemModel>> getActiveSessionOrders();

    @DELETE("sessions/active/orders/{order_id}/")
    Call<ObjectNode> deleteSessionOrder(@Path("order_id") String order_id);

    @POST("sessions/{session_id}/manage/order/")
    Call<ArrayNode> postSessionManagerOrders(@Path("session_id") long sessionId, @Body List<OrderedItemModel> orderedItemModels);

    // endregion

    // region REVIEWS
    @GET("reviews/restaurants/{shop_id}/")
    Call<List<ShopReviewModel>> getRestaurantReviews(@Path("shop_id") long shopId);

    @POST("reviews/{review_id}/react/")
    Call<ObjectNode> postReviewReact(@Path("review_id") long reviewId);

    @POST("reviews/sessions/{session_id}/")
    Call<ObjectNode> postCustomerReview(@Path("session_id") String sessionId, @Body NewReviewModel review);

    @Multipart
    @POST("images/reviews/upload/")
    Call<GenericDetailModel> postCustomerReviewPic(@Part MultipartBody.Part pic, @Part("use_case") RequestBody data);

    @DELETE("images/{image_id}/")
    Call<GenericDetailModel> deleteImage(@Path("image_id") String imageId);

    // endregion

    @GET("notification")
    Call<List<NotificationModel>> getNotif(@Query("last_notif_id") int lastNotifId);

    @GET("accounts/self/")
    Call<List<AccountModel>> getSelfAccounts();

    @GET("search/people/")
    Call<List<SearchResultPeopleModel>> getSearchPeopleResults(
            @Query("search") String query, @Query("friendship_status") String friendshipStatus);

    @GET("search/restaurant/")
    Call<List<SearchResultShopModel>> getSearchShopResults(
            @Query("search") String query, @Query("has_nonveg") Boolean hasNonVeg, @Query("has_alcohol") Boolean hasAlcohol
    );
}
