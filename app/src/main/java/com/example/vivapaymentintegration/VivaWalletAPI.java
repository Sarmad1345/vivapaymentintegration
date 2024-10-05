package com.example.vivapaymentintegration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface VivaWalletAPI {
    @POST("nativecheckout/v2/transactions")
    Call<TransactionResponse> createTransaction(@Body TransactionRequest request);
}
