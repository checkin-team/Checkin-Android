package com.alcatraz.admin.project_alcatraz.Social;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alcatraz.admin.project_alcatraz.Data.ApiClient;
import com.alcatraz.admin.project_alcatraz.Data.ApiResponse;
import com.alcatraz.admin.project_alcatraz.Data.AppRoomDatabase;
import com.alcatraz.admin.project_alcatraz.Data.NetworkBoundResource;
import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.Data.RetrofitLiveData;
import com.alcatraz.admin.project_alcatraz.Data.WebApiService;
import com.alcatraz.admin.project_alcatraz.Social.ChatDao.BriefChat;

import java.util.List;

import javax.inject.Singleton;

@Singleton
public class MessageRepository {
    private static final String TAG = MessageRepository.class.getSimpleName();

    private final MessageDao mMessageModel;
    private final ChatDao mChatModel;
    private final WebApiService mWebService;
    private static MessageRepository INSTANCE;

    private MessageRepository(MessageDao messageDao, ChatDao chatDao) {
        mWebService = ApiClient.getApiService();
        mMessageModel = messageDao;
        mChatModel = chatDao;
    }

    LiveData<List<Message>> getAllMessages() {
        return mMessageModel.getAll();
    }

    public LiveData<Resource<List<Message>>> getMessagesInConversation(int userId) {
        return new NetworkBoundResource<List<Message>, List<Message>>() {

            @Override
            protected void saveCallResult(@NonNull List<Message> messages) {
                mMessageModel.insertAll(messages.toArray(new Message[0]));
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Message> data) {
                // TODO: Override to check if chat_brief_item is fresh
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<List<Message>> loadFromDb() {
                return mMessageModel.getMessagesOfChat(userId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Message>>> createCall() {
                return new RetrofitLiveData<List<Message>>(mWebService.getMessages(userId));
            }
        }.getAsLiveData();
    }

    public LiveData<List<BriefChat>> getBriefChats() {
        return mChatModel.getRecentBriefChats();
    }

    public void insertMessages(Message... messages) {
        new insertMessageAsyncTask(mMessageModel).execute(messages);
    }

    private static class insertMessageAsyncTask extends AsyncTask<Message, Void, Void> {
        private MessageDao mAsyncTaskDao;
        insertMessageAsyncTask(MessageDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Message... messages) {
            mAsyncTaskDao.insertAll(messages);
            return null;
        }
    }

    public static MessageRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MessageRepository.class) {
                if (INSTANCE == null) {
                    AppRoomDatabase db = AppRoomDatabase.getDatabase(context);
                    INSTANCE = new MessageRepository(db.messageModel(), db.chatModel());
                }
            }
        }
        return INSTANCE;
    }
}
