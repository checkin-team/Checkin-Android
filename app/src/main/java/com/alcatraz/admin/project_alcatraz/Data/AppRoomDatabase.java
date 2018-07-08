package com.alcatraz.admin.project_alcatraz.Data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.alcatraz.admin.project_alcatraz.Social.Chat;
import com.alcatraz.admin.project_alcatraz.Social.ChatDao;
import com.alcatraz.admin.project_alcatraz.Social.Message;
import com.alcatraz.admin.project_alcatraz.Social.MessageDao;
import com.alcatraz.admin.project_alcatraz.User.User;
import com.alcatraz.admin.project_alcatraz.User.UserDao;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;

@Database(entities = {
        Message.class, Chat.class,
        User.class
}, version = 1, exportSchema = false)
@TypeConverters(value = Converters.class)
public abstract class AppRoomDatabase extends RoomDatabase {
    private static AppRoomDatabase INSTANCE;
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    public abstract MessageDao messageModel();
    public abstract UserDao userModel();
    public abstract ChatDao chatModel();

    public static AppRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppRoomDatabase.class) {
                if (INSTANCE == null) {
                    // TODO: Don't delete database in production!!!!!!!!!!
//                    context.getApplicationContext().deleteDatabase(Constants.APP_DATABASE);
                    // ---------------------------------------------------
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class, Constants.APP_DATABASE)
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final AppRoomDatabase mDb;
        PopulateDbAsync(AppRoomDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            TestDb.populateWithTestData(mDb);
            return null;
        }
    }
}
