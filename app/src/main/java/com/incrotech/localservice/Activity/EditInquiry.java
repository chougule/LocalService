package com.incrotech.localservice.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.incrotech.localservice.R;
import com.incrotech.localservice.Utils.Constants;
import com.incrotech.localservice.Utils.DBHelper;
import com.incrotech.localservice.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeet on 21/7/17.
 */

public class EditInquiry extends ActionBarActivity implements View.OnClickListener {

    String [] priority={"High", "Medium","Low"};
    ArrayList<String> currency;
    private Spinner priorityspinner,budgetspinner;
    String TAG="Edit InquiryActivity";
    private Button seldoc,submit;
    private EditText jobtitle,edt_budget,edt_decription;
    private TextView tvdocname;
    private static final int READ_REQUEST_CODE = 42;
    Utils utils;
    String postid;
    SQLiteDatabase db;
    DBHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_editinquiry);

        helper=new DBHelper(this);
        db = helper.getWritableDatabase();
        utils=new Utils(this);
        currency=new ArrayList<>();
        currency.add(getResources().getString(R.string.dollar));
        currency.add(getResources().getString(R.string.Rs));
        currency.add(getResources().getString(R.string.pound));
        currency.add(getResources().getString(R.string.euro));
        init();
        GetIntent();
    }

    private void init() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Post");

        priorityspinner= (Spinner) findViewById(R.id.spinpriority);
        budgetspinner = (Spinner) findViewById(R.id.spineditbudget);
        jobtitle= (EditText) findViewById(R.id.edt_edtinq_name);
        edt_budget= (EditText) findViewById(R.id.edt_edtbudgettext);
        edt_decription=(EditText)findViewById(R.id.edt_edtinq_desc);
        tvdocname= (TextView) findViewById(R.id.tv_edtinq_docname);

        seldoc= (Button) findViewById(R.id.btn_edtseldoc);
        submit= (Button) findViewById(R.id.btn_edtinq_submit);

        ArrayAdapter dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, priority);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priorityspinner.setAdapter(dataAdapter);

        ArrayAdapter budgetAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, currency);
        budgetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        budgetspinner.setAdapter(budgetAdapter);

        budgetspinner.setFocusableInTouchMode(true);
        budgetspinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {

                    if (budgetspinner.getWindowToken() != null) {
                        budgetspinner.performClick();
                    }
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }

        });

        priorityspinner.setFocusableInTouchMode(true);
        priorityspinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {

                    if (priorityspinner.getWindowToken() != null) {
                        priorityspinner.performClick();
                    }

                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }

        });

        seldoc.setOnClickListener(this);
        submit.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void GetIntent() {

        Intent intent=getIntent();
        if (intent!=null){

            postid=intent.getStringExtra("Id");
            String Name=intent.getStringExtra("Name");
            String Priority=intent.getStringExtra("Priority");
            String Budget=intent.getStringExtra("Budget");
            String Budget_icon=intent.getStringExtra("Budget_icon");
            String Description=intent.getStringExtra("Description");
            String Doc=intent.getStringExtra("Document");

            jobtitle.setText(Name);
            for (int i=0;i<priority.length;i++){
                if (Priority.equals(priority[i])){
                    priorityspinner.setSelection(i);
                }
            }

            budgetspinner.setSelection(Integer.parseInt(Budget_icon));
            edt_budget.setText(Budget);
            edt_decription.setText(Description);
            tvdocname.setText(Doc);
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();

        if(id==seldoc.getId()){

            if(isReadStoragePermissionGranted()&&isWriteStoragePermissionGranted()) {

                performFileSearch();
            }

        }else if(id==submit.getId()){

            if (Validate()){

                if(utils.isNetworkAvailable()){

                    HashMap<String, String> postDataParams = new HashMap<>();
                    postDataParams.put("id", postid);
                    //postDataParams.put("service_id", postid);
                    postDataParams.put("jobtitle", jobtitle.getText().toString().trim());
                    postDataParams.put("priority", priorityspinner.getSelectedItem().toString().trim());
                    postDataParams.put("bgt_icon", String.valueOf(budgetspinner.getSelectedItemPosition()));
                    postDataParams.put("budget", edt_budget.getText().toString().trim());
                    postDataParams.put("description", edt_decription.getText().toString().trim());
                    postDataParams.put("document", tvdocname.getText().toString().trim());

                    new PostInquiry(postDataParams).execute();

                }
            }
        }
    }

    private boolean Validate() {

        boolean check=true;

        if(jobtitle.getText().toString().length()<1){

            jobtitle.setError("Please Select Job Title");
            check=false;

        }else if (edt_budget.getText().toString().length()<1){

            edt_budget.setError("Please Enter Budget Amount");
            check=false;

        }else if (edt_decription.getText().toString().length()<1){

            edt_decription.setError("Please Enter Description");
            check=false;
        }/*else if (tvdocname.getText().toString().length()<1){

            tvdocname.setError("Please Select Document");
            check=false;
        }*/

        return check;
    }

    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                tvdocname.setText(uri.getPath());
            }
        }
    }


    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }

    class PostInquiry extends AsyncTask<String,Void,String> {
        ProgressDialog progressBar;
        HashMap<String, String> PostDataParams;
        String requestURL= Constants.URL+"ws_inquery_update.php";

        public PostInquiry(HashMap<String, String> postDataParams) {
            this.PostDataParams=postDataParams;
            progressBar = new ProgressDialog(EditInquiry.this);
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
                writer.write(utils.getPostDataString(PostDataParams));

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
                String message=jsonObject.getString("message");
                if (status.equals("0")){

                    Toast.makeText(EditInquiry.this,message,Toast.LENGTH_SHORT).show();

                }if (status.equals("1")){

                    /*StoreInquiryInLocal(PostDataParams);*/
                    Toast.makeText(EditInquiry.this,message,Toast.LENGTH_SHORT).show();
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    private void StoreInquiryInLocal(HashMap<String, String> postDataParams) {

        ContentValues cv=new ContentValues();
        cv.put("Title",postDataParams.get("jobtitle"));
        cv.put("Priority",postDataParams.get("priority"));
        cv.put("Currency",postDataParams.get("bgt_icon"));
        cv.put("Budget",postDataParams.get("bgt_icon"));
        cv.put("Budget_amount",postDataParams.get("budget"));
        cv.put("Description",postDataParams.get("description"));
        cv.put("File",postDataParams.get("document"));
        long a=db.update("Local_Services",cv,"Id = "+postDataParams.get("service_id"),new String[]{});
        Log.d(TAG+"CV",String.valueOf(a));
    }
}
