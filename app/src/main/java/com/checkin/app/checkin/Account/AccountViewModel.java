package com.checkin.app.checkin.Account;

import android.app.Application;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

public class AccountViewModel extends BaseViewModel {
    private AccountRepository mRepository;
    private MediatorLiveData<Resource<List<AccountModel>>> mAccounts = new MediatorLiveData<>();
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

    public void setCurrentAccount(AccountModel account) {
        mCurrentAccount.setValue(account);
    }

    public LiveData<AccountModel> getCurrentAccount() {
        return mCurrentAccount;
    }

    @Override
    public void updateResults() {
        fetchAccounts();
    }
}
