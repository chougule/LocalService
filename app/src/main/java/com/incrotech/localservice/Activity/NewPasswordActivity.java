package com.incrotech.localservice.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.incrotech.localservice.R;
import com.incrotech.localservice.Utils.Constants;
import com.incrotech.localservice.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class NewPasswordActivity extends AppCompatActivity {
    private Button newpswd;
    EditText newpass,confirmpass;
    String Id,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpasswod);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Password");


        Intent intent=getIntent();
        if (intent!=null){

            Id=intent.getStringExtra("Id");
            email=intent.getStringExtra("useremail");
        }

        newpass= (EditText) findViewById(R.id.edt_newpassword);
        confirmpass= (EditText) findViewById(R.id.edt_confirmpass);
        newpswd = (Button)findViewById(R.id.btn_newpass_submit);
        newpswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newpass.length()<6){

                    newpass.setError("Password length should be more than 6");
                }

                if (newpass.getText().toString().length()==0){

                    newpass.setError("Please enter new password");

                }else if (confirmpass.getText().toString().length()==0){

                    confirmpass.setError("Please enter confirm password");

                }else if (!newpass.getText().toString().trim().equals(confirmpass.getText().toString().trim())){

                    confirmpass.setError("Password not match");

                }else {

                    Utils aClass=new Utils(NewPasswordActivity.this);

                    if(aClass.isNetworkAvailable()){

                        HashMap<String,String> postDataParams=new HashMap<>();
                        postDataParams.put("useremail",email);
                        postDataParams.put("userpass",newpass.getText().toString().trim());
                        postDataParams.put("password",newpass.getText().toString().trim());
                        postDataParams.put("user_id",Id);

                        new ForgotPassword(postDataParams).execute();

                    }else {

                        Toast.makeText(NewPasswordActivity.this,"Please Check internet connection",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    class ForgotPassword extends AsyncTask<String,Void,String> {

        ProgressDialog progressBar;
        String requestURL= Constants.URL+"ws_forgotpassword.php";
        HashMap<String, String> postDataParams;

        public ForgotPassword(HashMap<String, String> postDataParams) {
            this.postDataParams = postDataParams;
            progressBar = new ProgressDialog(NewPasswordActivity.this);

        }

        @Override
        protected  void onPreExecute(){
            super.onPreExecute();

            progressBar.setCancelable(true);//you can cancel it by pressing back button
            progressBar.setMessage("Loading ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(0);//initially progress is 0
            progressBar.setMax(100);//sets the maximum value 100
            progressBar.show();//displays the progress bar
        }

        @Override
        protected String doInBackground(String... params) {

            URL url;
            String response = "";
            try {
                url = new URL(requestURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                    Log.d("POst",postDataParams.toString());
                    Log.d("Response",response);
                }
                else {
                    response="";
                }
            } catch (Exception e) {
                e.printStackTrace();
                progressBar.dismiss();

            }

            return response;

        }

        @Override
        protected void onPostExecute(String s){

            super.onPostExecute(s);
            try {
                progressBar.dismiss();
                JSONObject jsonObject=new JSONObject(s);
                String status=jsonObject.getString("status");
                String message=jsonObject.getString("message");
                Toast.makeText(NewPasswordActivity.this,message,Toast.LENGTH_SHORT).show();
                if (status.equals("1")){

                    Intent intent=new Intent(NewPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
