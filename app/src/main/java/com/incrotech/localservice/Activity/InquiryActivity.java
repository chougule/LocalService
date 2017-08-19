package com.incrotech.localservice.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.incrotech.localservice.Adapter.InquryAdapter;
import com.incrotech.localservice.R;
import com.incrotech.localservice.Utils.Constants;
import com.incrotech.localservice.Utils.DBHelper;
import com.incrotech.localservice.Utils.Inquiry;
import com.incrotech.localservice.Utils.Utils;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
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
 * Created by jeet on 18/7/17.
 */
public class InquiryActivity extends ActionBarActivity implements View.OnClickListener{

    private Button list,post,seldoc,submit;
    private RecyclerView recyclerView;
    private Spinner priorityspinner,budgetspinner;
    private EditText jobtitle,edt_budget,edt_decription;
    private TextView tvdocname;
    private View line;
    private static final int READ_REQUEST_CODE = 42;
    LinearLayout layoutpost;
    RelativeLayout call;
    private ScrollView scrollView;
    RelativeLayout relativeLayout;
    String [] priority={"High", "Medium","Low"};
    Utils utils;
    String TAG="InquiryActivity";
    ArrayList<String> currency;
    String service_name,service_id;
    SQLiteDatabase db;
    DBHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry);
        utils=new Utils(this);
        helper=new DBHelper(this);
        db = helper.getWritableDatabase();
        currency=new ArrayList<>();
        currency.add(getResources().getString(R.string.dollar));
        currency.add(getResources().getString(R.string.Rs));
        currency.add(getResources().getString(R.string.pound));
        currency.add(getResources().getString(R.string.euro));
        Intent intent=getIntent();
        if (intent!=null){

            service_name=intent.getStringExtra("service_name");
            service_id=intent.getStringExtra("service_id");
        }
        init();
        SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_id=sharedpreferences.getString("Id", Constants.MyPREFERENCES);
        HashMap<String, String> postDataParams=new HashMap<>();
        postDataParams.put("service_id",service_id);
        postDataParams.put("user_id",user_id);
        new GetInquiryList(postDataParams).execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (recyclerView.getVisibility()==View.VISIBLE){

            SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_id=sharedpreferences.getString("Id", Constants.MyPREFERENCES);
            HashMap<String, String> postDataParams=new HashMap<>();
            postDataParams.put("service_id",service_id);
            postDataParams.put("user_id",user_id);
            new GetInquiryList(postDataParams).execute();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void init() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(service_name);

        priorityspinner= (Spinner) findViewById(R.id.spin_priority);
        budgetspinner= (Spinner) findViewById(R.id.spin_currency);
        jobtitle= (EditText) findViewById(R.id.edt_inqjobtitle);
        edt_budget= (EditText) findViewById(R.id.edt_budgettext);
        edt_decription=(EditText)findViewById(R.id.edt_inq_desc);
        tvdocname= (TextView) findViewById(R.id.tv_inq_docname);
        line=findViewById(R.id.view_line);
        list= (Button) findViewById(R.id.btn_inquiry_list);
        post= (Button) findViewById(R.id.btn_inquiry_post);
        seldoc= (Button) findViewById(R.id.btn_seldoc);
        submit= (Button) findViewById(R.id.btn_inq_submit);
        call= (RelativeLayout) findViewById(R.id.rl_inquiry_call);
        recyclerView= (RecyclerView) findViewById(R.id.recycle_inquiry);
        layoutpost= (LinearLayout) findViewById(R.id.ll_inquiry_post);
        scrollView= (ScrollView) findViewById(R.id.scroll_inquiry);
        relativeLayout= (RelativeLayout) findViewById(R.id.rl_inqlist);
        list.requestFocus();
        post.setBackgroundResource(0);

      /*  priorityspinner.setFocusable(true);
        priorityspinner.setFocusableInTouchMode(true);
        priorityspinner.requestFocus();

        budgetspinner.setFocusable(true);
        budgetspinner.setFocusableInTouchMode(true);
        budgetspinner.requestFocus();*/

        ArrayAdapter dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, priority);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priorityspinner.setAdapter(dataAdapter);

        ArrayAdapter budgetAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, currency);
        budgetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        budgetspinner.setAdapter(budgetAdapter);

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

        call.setOnClickListener(this);
        list.setOnClickListener(this);
        post.setOnClickListener(this);
        seldoc.setOnClickListener(this);
        submit.setOnClickListener(this);

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                    finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        int id=view.getId();

        if (call.getId()==id){
            SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
            String Advertise_No=sharedpreferences.getString("Advertise_No", Constants.MyPREFERENCES);

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + Advertise_No));
            startActivity(intent);
        }

        if (id==list.getId()){

            list.setBackgroundResource(R.drawable.rounded_white);
            post.setBackgroundColor(0);
            relativeLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            line.setVisibility(View.VISIBLE);

           /* recyclerView= (RecyclerView) findViewById(R.id.recycle_inquiry);
            InquryAdapter adapter = new InquryAdapter(InquiryActivity.this,GetInquiry());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);*/

        }else if(id==post.getId()){

            jobtitle.requestFocus();
            post.setBackgroundResource(R.drawable.rounded_white);
            list.setBackgroundResource(0);
            relativeLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            line.setVisibility(View.GONE);

        }else if(id==seldoc.getId()){

            if(isReadStoragePermissionGranted()&&isWriteStoragePermissionGranted()) {

                performFileSearch();
            }

        }else if(id==submit.getId()){

            if (Validate()){

                if(utils.isNetworkAvailable()){

                    SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
                    String user_id=sharedpreferences.getString("Id", Constants.MyPREFERENCES);

                    HashMap<String, String> postDataParams = new HashMap<>();
                    postDataParams.put("user_id", user_id);
                    postDataParams.put("service_id", service_id);
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
                tvdocname.setError(null);
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

    class GetInquiryList extends AsyncTask<String,Void,String> {
        ProgressDialog progressBar;

        String requestURL= Constants.URL+"ws_inquiry_list.php";
        HashMap<String, String> postDataParams;

        public GetInquiryList(HashMap<String, String> postDataParams) {
            this.postDataParams=postDataParams;
            progressBar = new ProgressDialog(InquiryActivity.this);
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
                String message=jsonObject.getString("message");
                if (status.equals("0")){

                    line.setVisibility(View.GONE);
                    TextView noinq= (TextView) findViewById(R.id.tv_noinq);
                    noinq.setVisibility(View.VISIBLE);

                }if (status.equals("1")){
                    ArrayList<Inquiry> ListInquiry = new ArrayList<Inquiry>();
                    JSONArray data= jsonObject.getJSONArray("data");
                    for (int i=0;i<data.length();i++){

                        JSONObject object=data.getJSONObject(i);

                        Inquiry inquiry=new Inquiry();
                        inquiry.setId(object.getString("id"));
                        inquiry.setTitle(object.getString("jobtitle"));
                        inquiry.setPriority(object.getString("priority"));
                        inquiry.setBudget(object.getString("budget"));
                        inquiry.setBudget_icon(object.getString("bgt_icon"));
                        inquiry.setDescription(object.getString("description"));
                        inquiry.setFile(object.getString("files").replace("[","").replace("]","").replace("\"",""));
                        inquiry.setDate(object.getString("date"));
                        inquiry.setStatus(object.getString("status"));

                        ListInquiry.add(inquiry);

                    }
                    TextView noinq ;
                    if (ListInquiry.size()==0)
                    {
                        line.setVisibility(View.GONE);
                        noinq= (TextView) findViewById(R.id.tv_noinq);
                        noinq.setVisibility(View.VISIBLE);
                    }else {
                        line.setVisibility(View.VISIBLE);
                        noinq= (TextView) findViewById(R.id.tv_noinq);
                        noinq.setVisibility(View.GONE);
                    }

                    recyclerView= (RecyclerView) findViewById(R.id.recycle_inquiry);
                    InquryAdapter adapter = new InquryAdapter(InquiryActivity.this,ListInquiry);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class PostInquiry extends AsyncTask<String,Void,String> {
        ProgressDialog progressBar;
        HashMap<String, String> PostDataParams;
        String requestURL= Constants.URL+"ws_inquery.php";

        public PostInquiry(HashMap<String, String> postDataParams) {
            this.PostDataParams=postDataParams;
            progressBar = new ProgressDialog(InquiryActivity.this);
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
                String post_id=jsonObject.getString("id");
                String message=jsonObject.getString("message");
                if (status.equals("0")){

                    Toast.makeText(InquiryActivity.this,message,Toast.LENGTH_SHORT).show();

                }if (status.equals("1")){

                    PostDataParams.put("Post_id",post_id);
                    Toast.makeText(InquiryActivity.this,message,Toast.LENGTH_SHORT).show();
                //    StoreInquiryInLocal(PostDataParams);
                    ClearScreen();
                    OpenList();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void OpenList() {

        list.setBackgroundResource(R.drawable.rounded_white);
        post.setBackgroundColor(0);
        relativeLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        line.setVisibility(View.VISIBLE);

        SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_id=sharedpreferences.getString("Id", Constants.MyPREFERENCES);
        HashMap<String, String> postDataParams=new HashMap<>();
        postDataParams.put("service_id",service_id);
        postDataParams.put("user_id",user_id);
        new GetInquiryList(postDataParams).execute();

    }

    private void ClearScreen() {

        jobtitle.setText("");
        priorityspinner.setSelection(0);
        budgetspinner.setSelection(0);
        edt_budget.setText("");
        edt_decription.setText("");
        tvdocname.setText("");

    }

   /* private ArrayList<Inquiry> GetInquiry(){
        ArrayList<Inquiry> ListInquiry=new ArrayList<>();

        String query="Select * from Local_Services";

        Cursor cursor=db.rawQuery(query,new String[]{});
        if (cursor!=null){

            if (cursor.getCount()>0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){

                    Inquiry inquiry=new Inquiry();
                    inquiry.setId(cursor.getString(cursor.getColumnIndex("Id")));
                    inquiry.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
                    inquiry.setPriority(cursor.getString(cursor.getColumnIndex("Priority")));
                    inquiry.setBudget(cursor.getString(cursor.getColumnIndex("Budget_amount")));
                    inquiry.setBudget_icon(cursor.getString(cursor.getColumnIndex("Budget")));
                    inquiry.setDescription(cursor.getString(cursor.getColumnIndex("Description")));
                    inquiry.setFile(cursor.getString(cursor.getColumnIndex("File")));
                    inquiry.setDate("1/1/2017");

                    ListInquiry.add(inquiry);
                    cursor.moveToNext();
                }
            }
        }

        return ListInquiry;
    }

    private void StoreInquiryInLocal(HashMap<String, String> postDataParams) {

        ContentValues cv=new ContentValues();
        cv.put("Id",postDataParams.get("Post_id"));
        cv.put("Title",postDataParams.get("jobtitle"));
        cv.put("Priority",postDataParams.get("priority"));
        cv.put("Budget",postDataParams.get("bgt_icon"));
        cv.put("Budget_amount",postDataParams.get("budget"));
        cv.put("Description",postDataParams.get("description"));
        cv.put("File",postDataParams.get("document"));

        long a=db.insert("Local_Services",null,cv);
        Log.d(TAG+"CV",String.valueOf(a));
    }*/
}

