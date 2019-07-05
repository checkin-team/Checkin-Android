package com.checkin.app.checkin.Manager.Model;

import com.checkin.app.checkin.session.model.SessionInvoiceModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagerSessionInvoiceModel extends SessionInvoiceModel {
    public ManagerSessionInvoiceModel() {
    }

    public double getDiscountPercent() {
        Double subtotal = getBill().getSubtotal();
        Double discount = getBill().getDiscount();
        if (discount != null && subtotal != null)
            return (discount / subtotal) * 100;
        return 0d;
    }

    public String formatDiscountPercent() {
        return String.format(Locale.getDefault(), "%.2f", getDiscountPercent());
    }
}
