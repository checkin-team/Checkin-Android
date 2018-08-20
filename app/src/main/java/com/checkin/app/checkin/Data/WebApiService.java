package com.checkin.app.checkin.Data;

import com.checkin.app.checkin.Session.ActiveSessionModel;
import com.checkin.app.checkin.Social.Message;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebApiService {
    @GET("messages/{user_id}/")
    Call<List<Message>> getMessages(@Path("user_id") String userId);

    @POST("auth/login/")
    Call<ObjectNode> postLogin(@Body ObjectNode credentials);

    @GET("users/{user_id}/sessions/active/")
    Call<ActiveSessionModel> getActiveSession(@Path("user_id") String userID);

    @POST("sessions/{session_id}/orders/cancel/")
    Call<ObjectNode> postCancelOrder(@Path("session_id") String sessionID, @Body ObjectNode data);

    @POST("sessions/{session_id}/customers/add/")
    Call<ObjectNode> postSessionAddMember(@Path("session_id") String sessionID, @Body ObjectNode data);
}
