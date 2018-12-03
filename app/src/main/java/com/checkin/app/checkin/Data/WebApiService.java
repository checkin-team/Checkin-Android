package com.checkin.app.checkin.Data;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Menu.MenuModel;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Search.SearchModel;
import com.checkin.app.checkin.RestaurantActivity.Waiter.EventModel;
import com.checkin.app.checkin.Notifications.NotificationModel;
import com.checkin.app.checkin.Session.ActiveSessionModel;
import com.checkin.app.checkin.Shop.ShopJoin.ShopJoinModel;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.MemberModel;
import com.checkin.app.checkin.Shop.ShopReviewPOJO;
import com.checkin.app.checkin.User.Friendship.FriendshipModel;
import com.checkin.app.checkin.User.UserModel;
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
    Call<UserModel> getNonPersonalUser(@Path("user_pk") String userPk);

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

    @POST("qr/decrypt/")
    Call<ObjectNode> postDecryptQr(@Body ObjectNode data);

    @GET("users/{user_id}/sessions/active/")
    Call<ActiveSessionModel> getActiveSession(@Path("user_id") String userID);

    // region SHOP
    @POST("restaurants/create/")
    Call<GenericDetailModel> postRegisterShop(@Body ShopJoinModel model);

    @GET("restaurants/{shop_id}/")
    Call<RestaurantModel> getRestaurantDetails(@Path("shop_id") String shopId);

    @GET("restaurants/")
    Call<List<RestaurantModel>> getRestaurants();

    @GET("restaurants/{shop_id}/manage/")
    Call<RestaurantModel> getRestaurantManageDetails(@Path("shop_id") String shopId);

    @PATCH("restaurants/{shop_id}/manage/")
    Call<ObjectNode> postRestaurantManageDetails(@Path("shop_id") String shopId, @Body RestaurantModel shopData);

    @GET("restaurants/{shop_id}/members/")
    Call<List<MemberModel>> getRestaurantMembers(@Path("shop_id") String shopId);

    @POST("restaurants/{shop_id}/members/")
    Call<ObjectNode> addRestaurantMember(@Path("shop_id") String shopId, @Body MemberModel shopMember);

    @PUT("restaurants/{shop_id}/members/{user_id}/")
    Call<ObjectNode> updateRestaurantMember(@Path("shop_id") String shopId,@Path("user_id") String userId,@Body MemberModel shopMember);

    @PATCH("restaurants/{shop_id}/members/{user_id}/")
    Call<ObjectNode> updateRestaurantMemberPartial(@Path("shop_id") String shopId,@Path("user_id") String userId,@Body MemberModel shopMember);

    @DELETE("restaurants/{shop_id}/members/{user_id}/")
    Call<ObjectNode> deleteRestaurantMember(@Path("shop_id") String shopId,@Path("user_id") String userId);





    // endregion

    @GET("shops/{shop_id}/menus/available/")
    Call<MenuModel> getAvailableMenu(@Path("shop_id") String shopID);

    @GET("shops/{shop_id}/reviews/")
    Call<List<ShopReviewPOJO>> getShopReviews(@Path("shop_id") String shopID);

    @POST("sessions/{session_id}/orders/cancel/")
    Call<ObjectNode> postCancelOrder(@Path("session_id") String sessionID, @Body ObjectNode data);

    @POST("sessions/{session_id}/customers/add/")
    Call<ObjectNode> postSessionAddMember(@Path("session_id") String sessionID, @Body ObjectNode data);

    @GET("notification")
    Call<List<NotificationModel>> getNotif(@Query("last_notif_id") int lastNotifId);

    @GET("accounts/self/")
    Call<List<AccountModel>> getSelfAccounts();

    @GET("search/")
    Call<List<SearchModel>>getSearchResults(@Query("search") String query);

    //region Waiter Events
    @GET("shops/{table_id}/orders")
    Call<List<EventModel>> getItems(@Path("table_id") long tableId);

    @POST("shops/{table_id}/orders/")
    Call<ObjectNode> postItemCompleted(@Path("table_id") long tableId,@Body ObjectNode data);
}
