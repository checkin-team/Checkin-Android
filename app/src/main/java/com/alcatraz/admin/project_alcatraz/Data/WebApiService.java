package com.alcatraz.admin.project_alcatraz.Data;

import com.alcatraz.admin.project_alcatraz.Session.ActiveSessionModel;
import com.alcatraz.admin.project_alcatraz.Social.Message;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebApiService {
    @GET("messages/{user_id}/")
    Call<List<Message>> getMessages(@Path("user_id") int userId);

    @POST("auth/login/")
    Call<Map<String, String>> postLogin(@Body Map<String, String> credentials);

    @GET("activeSessionModel/{active_session_id}")
    Call<ActiveSessionModel> getActiveSession(@Path("active_session_id") int activeSessionId);
}
