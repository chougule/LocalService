package com.incrotech.localservice.Activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.incrotech.localservice.R;
import com.incrotech.localservice.Utils.Constants;
import com.incrotech.localservice.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeet on 18/7/17.
 */

public class LoginActivity extends AppCompatActivity  {

    EditText Name,mobile,email,password;
    Button login,fblogin;
    TextView forgotpass;
    Utils utils;
    String stremail,strname;
    String User_id;
    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.getString("Id", Constants.MyPREFERENCES) != null &&
                !sharedpreferences.getString("Id", Constants.MyPREFERENCES).equals("SharedPreferences")) {

            Intent i = new Intent(LoginActivity.this, ServiceListActivity.class);
            i.putExtra("Parent", "MainScreen");
            startActivity(i);
            finish();
        }
            setContentView(R.layout.activity_login);
            utils = new Utils(this);
            init();

    }

    private void init() {

        Name= (EditText) findViewById(R.id.edt_login_name);
        mobile= (EditText) findViewById(R.id.edt_login_mobile);
        email= (EditText) findViewById(R.id.edt_login_email);
        password= (EditText) findViewById(R.id.edt_login_password);
        login= (Button) findViewById(R.id.btn_login);
        fblogin= (Button) findViewById(R.id.fblogin);
        forgotpass= (TextView) findViewById(R.id.tvlogin_forgot_password);
        loginButton = (LoginButton) findViewById(R.id.fb_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                graphRequest(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                graphRequest(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        fblogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email", "user_friends"));

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validate()){
                    if (utils.isNetworkAvailable()) {

                        stremail=email.getText().toString().trim();
                        strname=Name.getText().toString().trim();

                        HashMap<String, String> postDataParams = new HashMap<>();
                        postDataParams.put("username", Name.getText().toString().trim());
                        postDataParams.put("useremail", email.getText().toString().trim());
                        postDataParams.put("usermobile", mobile.getText().toString().trim());
                        postDataParams.put("userpass", password.getText().toString().trim());

                        new LoginUser(postDataParams).execute();

                    } else {

                        Toast.makeText(LoginActivity.this, "Please Check internet connection", Toast.LENGTH_SHORT).show();
                    }

                }else {

                }
            }
        });
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(LoginActivity.this, ForgotpswActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoginManager.getInstance().logOut();
    }

    public void graphRequest(AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String firstName = "";
                    String lastName = "";
                    String stremail = "";
                    String strmobile = "";

                    //URL profilePicture = new URL("https://graph.facebook.com/" + userId + "/picture?width=500&height=500");
                    if (object.has("first_name")) {
                        firstName = object.getString("first_name");
                    }
                    if (object.has("last_name")) {
                        lastName = object.getString("last_name");
                    }if (object.has("email")) {
                        stremail = object.getString("email");
                    }

                    Name.setText(firstName+" "+lastName);
                    email.setText(stremail);
                    forgotpass.setVisibility(View.GONE);
                    fblogin.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle b = new Bundle();
        b.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
        request.setParameters(b);
        request.executeAsync();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if (requestCode==100){
            finish();
        }*/
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private String getEmiailID(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);
        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }
    private boolean Validate() {
        boolean check=true;

        if (Name.getText().toString().length()<2){
            Name.setError("Please enter name");
            check=false;
        }else if (mobile.getText().toString().length()<10){
            mobile.setError("Please enter valid mobile number");
            check=false;
        }else if (!isValidEmail(email.getText().toString())){
            email.setError("Please enter valid email");
            check=false;
        }else if (password.getText().toString().length()<6){
            password.setError("Password length should be more than 6");
            check=false;
        }
        return check;
    }
    public final static boolean isValidEmail(CharSequence target) {

        boolean check=false;
        check=android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();

        return check;
    }

    class LoginUser extends AsyncTask<String,Void,String> {
        ProgressDialog progressBar;

        String requestURL= Constants.URL+"ws_userLogin.php";
        HashMap<String, String> postDataParams;

        public LoginUser(HashMap<String, String> postDataParams) {
            this.postDataParams = postDataParams;
            progressBar = new ProgressDialog(LoginActivity.this);
        }

        @Override
        protected  void onPreExecute(){
            super.onPreExecute();

            progressBar.setCancelable(true);
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
                writer.write(utils.getPostDataString(postDataParams));

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
                String message=jsonObject.getString("validationStatus");
                if (status.equals("0")){

                    Toast.makeText(LoginActivity.this,message,Toast.LENGTH_SHORT).show();

                }if (status.equals("1")){

                    Toast.makeText(LoginActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                    JSONArray data= jsonObject.getJSONArray("data");
                    JSONObject object=data.getJSONObject(0);
                    String id=object.getString("id");
                    String advno=object.getString("cantact_no");

                    SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("Id", id);
                    editor.putString("Advertise_No",advno);
                    editor.putString("Name",strname);
                    editor.putString("Email",stremail);
                    editor.commit();

                    Intent intent=new Intent(LoginActivity.this, ServiceListActivity.class);
                    startActivity(intent);
                    finish();

                }if (status.equals("2")){

                    Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                    JSONArray data= jsonObject.getJSONArray("data1");
                    JSONObject object=data.getJSONObject(0);
                    String id=object.getString("id");
                    String advno=object.getString("cantact_no");

                    SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("Id", id);
                    editor.putString("Advertise_No",advno);
                    editor.putString("Name",strname);
                    editor.putString("Email",stremail);
                    editor.commit();

                    Intent intent=new Intent(LoginActivity.this, ServiceListActivity.class);
                    startActivity(intent);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
