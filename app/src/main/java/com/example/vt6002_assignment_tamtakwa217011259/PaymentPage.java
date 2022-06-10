package com.example.vt6002_assignment_tamtakwa217011259;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentPage extends AppCompatActivity {
    Button button;

    String SECRET_KEY = "sk_test_51L7DL4ChVUZhVA3hD9E95F7abEVv6sy3f0SI66gdtVANhGblAVDr4WzvXtCPtIBFXgZB40dXBm1q55nlNa7GaPRB00X7bghJ4f";
    String PUBLISH_KEY = "pk_test_51L7DL4ChVUZhVA3hSHAgnggsZUnSQYGJmYkdUpWvKKHYEwAPvWc3iYxqMVaW3CSJFlrUjlvoukJOySKTu63h59YL00PiC2J701";
    PaymentSheet paymentSheet;

    String customerID;
    String EphericalKey;
    String ClientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);
        PaymentConfiguration.init(this,PUBLISH_KEY);

        button = findViewById(R.id.payAction);

        paymentSheet=new PaymentSheet(this,paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PaymentFlow();
            }
        });

        TextView displayPrice = findViewById(R.id.showPriceMax);
        displayPrice.setText(Integer.toString(MySignleton.priceMax.INSTANCE.getPriceMax()));

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            customerID = object.getString("id");
                            Toast.makeText(PaymentPage.this,customerID,Toast.LENGTH_SHORT);
                            Log.d("PAY","1 "+customerID);
                            getEphericalKey(customerID);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer "+SECRET_KEY);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(PaymentPage.this);
        requestQueue.add(stringRequest);



    }
    /**
     @Description/Purpose : Payment result
     @param paymentSheetResult
     @Expected Outputs :  Payment result
     */
    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(PaymentPage.this,"Payment Success",Toast.LENGTH_SHORT);
            Log.d("PAY","onPaymentResult "+"Payment Success");
            MySignleton.priceMax.INSTANCE.setPriceMax(0);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     @Description/Purpose : get epherical key
     @param customerID
     @Expected Outputs :  epherical key
     */
    private void getEphericalKey(String customerID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
                            Toast.makeText(PaymentPage.this,EphericalKey,Toast.LENGTH_SHORT);
                            Log.d("PAY","2 "+EphericalKey);
                            getCLientSecret(customerID,EphericalKey);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer "+SECRET_KEY);
                header.put("Stripe-Version","2020-08-27");
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params =new HashMap<>();
                params.put("customer",customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(PaymentPage.this);
        requestQueue.add(stringRequest);


    }

    /**
     @Description/Purpose : get client secret
     @param customerID
     @param ephericalKey
     @Expected Outputs :  client secret
     */
    private void getCLientSecret(String customerID, String ephericalKey) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            Toast.makeText(PaymentPage.this,ClientSecret,Toast.LENGTH_SHORT);
                            Log.d("PAY","3 "+ClientSecret);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer "+SECRET_KEY);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params =new HashMap<>();
                params.put("customer",customerID);
                params.put("amount",MySignleton.priceMax.INSTANCE.getPriceMax()+"00");
                params.put("currency","hkd");
                params.put("automatic_payment_methods[enabled]","true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(PaymentPage.this);
        requestQueue.add(stringRequest);

    }

    private void PaymentFlow() {

        paymentSheet.presentWithPaymentIntent(
                ClientSecret,new PaymentSheet.Configuration("ABC Company"
                        ,new PaymentSheet.CustomerConfiguration(
                        customerID,
                        EphericalKey
                ))
        );

    }

}