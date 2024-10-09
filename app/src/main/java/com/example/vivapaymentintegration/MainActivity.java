package com.example.vivapaymentintegration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.braintreepayments.cardform.view.CardForm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MainActivity extends AppCompatActivity {
    private CardForm cardForm;
    private Button btnPay, btnSelectPayment;
    private RadioGroup radioGroupPayment;
    private String selectedAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardForm = findViewById(R.id.card_form);
        btnPay = findViewById(R.id.btn_pay);
        btnSelectPayment = findViewById(R.id.btn_select_payment);
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        // Step 1: Select Payment Amount
        btnSelectPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupPayment.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(MainActivity.this, "Please select a payment amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedId);
                selectedAmount = selectedRadioButton.getText().toString().replace("€", "");  // Remove the euro symbol
                // Show the card form and pay button after selecting the amount
                cardForm.setVisibility(View.VISIBLE);
                btnPay.setVisibility(View.VISIBLE);
                btnSelectPayment.setVisibility(View.GONE);  // Hide the payment selection button
            }
        });

        // Step 2: Setup Card Form
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .setup(MainActivity.this);
        // Step 3: Handle the Pay Button
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardForm.isValid()) {
                    String cardNumber = cardForm.getCardNumber();
                    String expirationDate = cardForm.getExpirationMonth() + "/" + cardForm.getExpirationYear();
                    String cvv = cardForm.getCvv();
                    // Process payment
                    try {
                        createTransaction(cardNumber, expirationDate, cvv);
                    } catch (Exception e) {
                        showError("Transaction failed: " + e.getMessage());
                    }
                } else {
                    showError("Invalid card data");
                }
            }
        });
    }

    private void createTransaction(String cardNumber, String expirationDate, String cvv) {
        VivaWalletAPI apiService = ApiClient.getClient().create(VivaWalletAPI.class);
        // Prepare the request data with the selected payment amount
        TransactionRequest request = new TransactionRequest(selectedAmount, cardNumber, cvv, expirationDate, "978", "Test Transaction");
        // Make the API call
        Call<TransactionResponse> call = apiService.createTransaction(request);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                if (response.isSuccessful()) {
                    TransactionResponse transactionResponse = response.body();
                    if (transactionResponse != null && "F".equals(transactionResponse.getStatusId())) {
                        showSuccess();
                    } else {
                        showError("Transaction Failed: " + transactionResponse.getMessage());
                    }
                } else {
                    showError("Transaction Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, Throwable t) {
                showError("Network Error: " + t.getMessage());
            }
        });
    }

    private void showSuccess() {
        Toast.makeText(MainActivity.this, "Transaction Successful!", Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
