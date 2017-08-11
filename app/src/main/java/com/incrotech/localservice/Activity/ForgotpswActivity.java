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


public class ForgotpswActivity extends AppCompatActivity {
    private Button btn_forgot;
    EditText emil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");

        Utils aClass=new Utils(ForgotpswActivity.this);
        aClass.CheckPermission();
        emil= (EditText) findViewById(R.id.edt_forgot_email);
        btn_forgot = (Button)findViewById(R.id.btn_forgotpass_submit);
        btn_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (emil.getText().toString().length()<10){
                    emil.setError("Enter Valid Email id or Mobile Number ");
                }else {

                    Utils aClass=new Utils(ForgotpswActivity.this);

                    if(aClass.isNetworkAvailable()){

                        HashMap<String,String> postDataParams=new HashMap<>();

                        postDataParams.put("useremail",emil.getText().toString().trim());

                        new ForgotPassword(postDataParams).execute();

                    }else {

                        Toast.makeText(ForgotpswActivity.this,"Please Check internet connection",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class ForgotPassword extends AsyncTask<String,Void,String> {
        ProgressDialog progressBar;

        String requestURL= Constants.URL+"ws_forgotpassword.php";
        HashMap<String, String> postDataParams;

        public ForgotPassword(HashMap<String, String> postDataParams) {
            this.postDataParams = postDataParams;
            progressBar = new ProgressDialog(ForgotpswActivity.this);

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
                if (status.equals("0")){

                Toast.makeText(ForgotpswActivity.this,"Email id not Matched",Toast.LENGTH_SHORT).show();

                }if (status.equals("1")){

                    JSONObject data= jsonObject.getJSONObject("data");
                    String id=data.getString("id");

                    Intent intent=new Intent(ForgotpswActivity.this, NewPasswordActivity.class);
                    intent.putExtra("Id",id);
                    intent.putExtra("useremail",emil.getText().toString().trim());
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
