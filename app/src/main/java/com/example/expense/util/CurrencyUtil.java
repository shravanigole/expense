package com.example.expense.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public static String formatAmount(double amount) {
        return currencyFormatter.format(amount);
    }

    public static double parseAmount(String amountString) {
        try {
            return Double.parseDouble(amountString.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}