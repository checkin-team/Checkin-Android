package com.alcatraz.admin.project_alcatraz.Social;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alcatraz.admin.project_alcatraz.Data.ApiClient;
import com.alcatraz.admin.project_alcatraz.Data.ApiResponse;
import com.alcatraz.admin.project_alcatraz.Data.AppDatabase;
import com.alcatraz.admin.project_alcatraz.Data.BaseRepository;
import com.alcatraz.admin.project_alcatraz.Data.NetworkBoundResource;
import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.Data.RetrofitLiveData;
import com.alcatraz.admin.project_alcatraz.Data.WebApiService;

import java.util.List;

import javax.inject.Singleton;

import io.objectbox.Box;
import io.objectbox.Property;
import io.objectbox.android.ObjectBoxLiveData;

@Singleton
public class MessageRepository extends BaseRepository {
    private static final String TAG = MessageRepository.class.getSimpleName();
    private Box<Message> mMessageModel;
    private Box<Chat> mChatModel;
    private final WebApiService mWebService;
    private static MessageRepository INSTANCE;

    private MessageRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        mMessageModel = AppDatabase.getMessageModel(context);
        mChatModel = AppDatabase.getChatModel(context);
    }

    LiveData<List<Message>> getAllMessages() {
        return new ObjectBoxLiveData<>(mMessageModel.query().build());
    }

    public LiveData<Resource<List<Message>>> getMessagesInConversation(int userId) {
        return new NetworkBoundResource<List<Message>, List<Message>>() {

            @Override
            protected void saveCallResult(@NonNull List<Message> messages) {
                mMessageModel.put(messages);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Message> data) {
                // TODO: Override to check if chat_brief_item is fresh
                return data == null;
            }

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Message>> loadFromDb() {
                return new ObjectBoxLiveData<>(mMessageModel.query().equal(Message_.chatId, userId).build());
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Message>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getMessages(userId));
            }
        }.getAsLiveData();
    }

    public LiveData<List<Chat>> getBriefChats() {
        return null;
    }

    public static MessageRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (MessageRepository.class) {
                if (INSTANCE == null) {
                    Context context = application.getApplicationContext();
                    INSTANCE = new MessageRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public void insertMessages(Message... messages) {
        mMessageModel.put(messages);
    }
}
