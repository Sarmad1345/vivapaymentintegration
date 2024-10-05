package com.example.vivapaymentintegration;

public class TransactionRequest {
    private String amount;
    private String cardNumber;
    private String cvv;
    private String expirationDate;
    private String currencyCode;
    private String merchantTrns;

    public TransactionRequest(String amount, String cardNumber, String cvv, String expirationDate, String currencyCode, String merchantTrns) {
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expirationDate = expirationDate;
        this.currencyCode = currencyCode;
        this.merchantTrns = merchantTrns;
    }

    // Getters and Setters...
}
