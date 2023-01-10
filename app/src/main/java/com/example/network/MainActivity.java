package com.example.network;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText urlET;
    private TextView response, time,retrying;
    private String url;
    long maxTime = 10*(60*1000);
    long retryTime = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlET = findViewById(R.id.idUrl);
        Button submit = findViewById(R.id.submit);
        response = findViewById(R.id.response);
        time = findViewById(R.id.time);
        retrying = findViewById(R.id.retryingIn);

        submit.setOnClickListener(view -> {
            if(urlET.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Please enter url", Toast.LENGTH_SHORT).show();
            }else {
                url = urlET.getText().toString().trim();
                checkStatus(url);
                new ConnectionCountDown(maxTime,retryTime).start();
            }
        });

    }

    public class ConnectionCountDown extends CountDownTimer{

        public ConnectionCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            checkStatus(url);
            new SecCountdown(retryTime,1000).start();
        }

        @Override
        public void onFinish() {
            retrying.setText("Finished");
        }
    }

    public static class GetConnectionStatus extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {

            URL url;
            try {
                url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();
                return code;

            } catch (Exception e) {
                e.printStackTrace();
                return  null;
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private void checkStatus(String url){
        try {
            int responseCode = new GetConnectionStatus().execute(url).get();
            response.setText("Response code : "+responseCode);
            if(responseCode==200){
                response.setTextColor(Color.GREEN);
            }else {
                response.setTextColor(Color.RED);
            }
            time.setText("Last check time : "+ new Date());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class SecCountdown extends CountDownTimer{

        public SecCountdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            long sec = l/1000;
            retrying.setText("Checking status in : "+sec+" Sec");
        }

        @Override
        public void onFinish() {

        }
    }


}