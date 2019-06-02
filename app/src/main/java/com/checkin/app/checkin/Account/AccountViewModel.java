package com.checkin.app.checkin.Account;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;

import java.util.List;

public class AccountViewModel extends BaseViewModel {
    private AccountRepository mRepository;
    private SourceMappedLiveData<Resource<List<AccountModel>>> mAccounts = createNetworkLiveData();
    private MutableLiveData<AccountModel> mCurrentAccount = new MutableLiveData<>();

    public AccountViewModel(@NonNull Application application) {
        super(application);
        mRepository = AccountRepository.getInstance(application);
    }

    public LiveData<Resource<List<AccountModel>>> getAccounts() {
        fetchAccounts();
        return mAccounts;
    }

    private void fetchAccounts() {
        mAccounts.addSource(mRepository.getSelfAccounts(), mAccounts::setValue);
    }

    public LiveData<AccountModel> getCurrentAccount() {
        return mCurrentAccount;
    }

    public void setCurrentAccount(AccountModel account) {
        mCurrentAccount.setValue(account);
    }

    @Override
    public void updateResults() {
        fetchAccounts();
    }
}
