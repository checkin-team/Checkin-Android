package com.alcatraz.admin.project_alcatraz.Social;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.alcatraz.admin.project_alcatraz.Data.Resource;

import java.util.List;

public class MessageViewModel extends AndroidViewModel {
    private MessageRepository mRepository;
    private LiveData<List<Message>> mAllMessages;
    private LiveData<Resource<List<Message>>> mChatMessages;
    private LiveData<List<Chat>> mBriefChats;
    private SharedPreferences mPrefs;

    public MessageViewModel(@NonNull Application application) {
        super(application);
        mRepository = MessageRepository.getInstance(application);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
    }

    public LiveData<Resource<List<Message>>> getMessagesOfChat(int userId) {
        if (mChatMessages == null)
            mChatMessages = mRepository.getMessagesInConversation(userId);
        return mChatMessages;
    }

    public LiveData<List<Message>> getAllMessages() {
        if (mAllMessages == null)
            mAllMessages = mRepository.getAllMessages();
        return mAllMessages;
    }

    public LiveData<List<Chat>> getBriefChats() {
        if (mBriefChats == null)
            mBriefChats = mRepository.getBriefChats();
        return mBriefChats;
    }

    public void sendMessages(Message... messages) {
        mRepository.insertMessages(messages);
    }

    public int getChatId(int userId) {
        return userId;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MessageViewModel(mApplication);
        }
    }
}
