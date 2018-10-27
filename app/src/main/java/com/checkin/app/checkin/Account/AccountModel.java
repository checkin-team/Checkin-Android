package com.checkin.app.checkin.Account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AccountModel {
    @JsonProperty("pk")
    private String id;

    private ACCOUNT_TYPE accountType;

    private String pic;

    private String name;

    private ObjectNode detail;

    public enum ACCOUNT_TYPE {
        USER(201),
        RESTAURANT_MANAGER(202), RESTAURANT_WAITER(203), RESTAURANT_COOK(204);

        public final int id;
        ACCOUNT_TYPE(int id) {
            this.id = id;
        }

        public static ACCOUNT_TYPE getById(int id) {
            for (ACCOUNT_TYPE type: ACCOUNT_TYPE.values()) {
                if (type.id == id)
                    return type;
            }
            return USER;
        }
    }

    AccountModel() {}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ACCOUNT_TYPE getAccountType() {
        return accountType;
    }

    public String getPic() {
        return pic;
    }

    public ObjectNode getDetail() {
        return detail;
    }

    @JsonProperty("acc_type")
    public void setAccountType(int accType) {
        this.accountType = ACCOUNT_TYPE.getById(accType);
    }

    @Nullable
    public static AccountModel getByAccountType(@NonNull List<AccountModel> accounts, ACCOUNT_TYPE accountType) {
        for (AccountModel item: accounts) {
            if (item.getAccountType() == accountType)
                return item;
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
