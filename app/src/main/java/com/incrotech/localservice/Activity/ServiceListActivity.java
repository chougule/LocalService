package com.incrotech.localservice.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.incrotech.localservice.Adapter.Adapter_ServiceList;
import com.incrotech.localservice.Adapter.DrawerServiceAdapter;
import com.incrotech.localservice.R;
import com.incrotech.localservice.Utils.Constants;
import com.incrotech.localservice.Utils.Services;
import com.incrotech.localservice.Utils.TextDrawable;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeet on 18/7/17.
 */

public class ServiceListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    RecyclerView recyclerView,drawer;
    String TAG,Username,UserEmail,Advertise_No;
    ArrayList<Services> Servicelist;
    FloatingActionButton whatsapp,gmail,message,share;
    TextView tv_whatsapp,tv_gmail,tv_message;
    RelativeLayout call;
    Utils utils;
    private RelativeLayout rl_fab;
    private Boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private TextDrawable.IBuilder mDrawableBuilder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicelist);
        utils=new Utils(this);
        TAG=this.getLocalClassName();
        init();
        Servicelist=new ArrayList();
        HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put("id", utils.GetUser_id());

        if (utils.isNetworkAvailable()) {

            new GetServiceList(postDataParams).execute();

            //new ValidateUser(postDataParams).execute();

        }else {
            Intent intent=new Intent(ServiceListActivity.this,AlertActivity.class);
            intent.putExtra("type","no connection");
            startActivityForResult(intent,100);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(isFabOpen){

            rl_fab.setBackgroundColor(0x00000000);
            share.startAnimation(rotate_backward);
            whatsapp.startAnimation(fab_close);
            gmail.startAnimation(fab_close);
            message.startAnimation(fab_close);
            tv_whatsapp.startAnimation(fab_close);
            tv_gmail.startAnimation(fab_close);
            tv_message.startAnimation(fab_close);
            whatsapp.setClickable(false);
            gmail.setClickable(false);
            message.setClickable(false);
            isFabOpen = false;
            recyclerView.setClickable(true);


        }else {
            super.onBackPressed();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

          if (id == R.id.nav_gallery) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {

        SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        Username=sharedpreferences.getString("Name", Constants.MyPREFERENCES);
        UserEmail=sharedpreferences.getString("Email", Constants.MyPREFERENCES);
        Advertise_No=sharedpreferences.getString("Advertise_No", Constants.MyPREFERENCES);
        recyclerView= (RecyclerView) findViewById(R.id.recycle_service);
        call= (RelativeLayout) findViewById(R.id.fab_call);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        View logo = getLayoutInflater().inflate(R.layout.layout_actionbar, null);
        getSupportActionBar().setCustomView(logo);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        DrawerLayout drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerlayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerlayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerlayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {

                TextView Name= (TextView) findViewById(R.id.tv_drawer_username);
                TextView Email= (TextView) findViewById(R.id.tv_drawer_useremail);
                TextView initial = (TextView) findViewById(R.id.iv_drawerinitial);

                String [] tmp=Username.split(" ");
                if (tmp.length>1){

                    String a= String.valueOf(tmp[0].charAt(0))+String.valueOf(tmp[1].charAt(0));
                    initial.setText(a);
                }else {
                    initial.setText(String.valueOf(tmp[0].charAt(0)));
                }

                Name.setText(Username);
                Email.setText(UserEmail);

                drawer= (RecyclerView) findViewById(R.id.recycle_drawer);
                TextView offer= (TextView) findViewById(R.id.tvdraer_offer);
                DrawerServiceAdapter adapter = new DrawerServiceAdapter(ServiceListActivity.this,Servicelist);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                drawer.setLayoutManager(mLayoutManager);
                drawer.setItemAnimator(new DefaultItemAnimator());
                drawer.setAdapter(adapter);

                offer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(ServiceListActivity.this,OfferActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        share= (FloatingActionButton) findViewById(R.id.fab_main);
        whatsapp= (FloatingActionButton) findViewById(R.id.fab_whatsapp);
        gmail= (FloatingActionButton) findViewById(R.id.fab_gmail);
        message= (FloatingActionButton) findViewById(R.id.fab_message);
        tv_gmail= (TextView) findViewById(R.id.tv_gmail);
        tv_whatsapp= (TextView) findViewById(R.id.tv_whatsapp);
        tv_message= (TextView) findViewById(R.id.tv_message);
        rl_fab= (RelativeLayout) findViewById(R.id.rl_fab);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        call.setOnClickListener(this);
        share.setOnClickListener(this);
        whatsapp.setOnClickListener(this);
        gmail.setOnClickListener(this);
        message.setOnClickListener(this);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                if(isFabOpen){

                    recyclerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            return;

                        }
                    });
                }

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public void animateFAB(){

        if(isFabOpen){

            rl_fab.setBackgroundColor(0x00000000);
            share.startAnimation(rotate_backward);
            whatsapp.startAnimation(fab_close);
            gmail.startAnimation(fab_close);
            message.startAnimation(fab_close);
            tv_whatsapp.startAnimation(fab_close);
            tv_gmail.startAnimation(fab_close);
            tv_message.startAnimation(fab_close);
            whatsapp.setClickable(false);
            gmail.setClickable(false);
            message.setClickable(false);
            isFabOpen = false;
            recyclerView.setClickable(true);
            Log.d("Raj", "close");

        } else {

            rl_fab.setBackgroundColor(getResources().getColor(R.color.fab_background));
            share.startAnimation(rotate_forward);
            whatsapp.startAnimation(fab_open);
            gmail.startAnimation(fab_open);
            message.startAnimation(fab_open);
            tv_whatsapp.startAnimation(fab_open);
            tv_gmail.startAnimation(fab_open);
            tv_message.startAnimation(fab_open);
            whatsapp.setClickable(true);
            gmail.setClickable(true);
            message.setClickable(true);
            recyclerView.setClickable(false);
            recyclerView.setEnabled(false);
            isFabOpen = true;
            Log.d("Raj","open");

        }
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();

        if (call.getId()==id){

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + Advertise_No));
            startActivity(intent);
        }
        if (share.getId()==id){
            animateFAB();
        }
        if (whatsapp.getId()==id){
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ServiceListActivity.this,"Whatsapp have not been installed.",Toast.LENGTH_SHORT).show();
            }
        }if (gmail.getId()==id){
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.google.android.gm");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ServiceListActivity.this,"email have not been installed.",Toast.LENGTH_SHORT).show();
            }
        }if (message.getId()==id){
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage(getDefaultSmsAppPackageName(ServiceListActivity.this));
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ServiceListActivity.this,"Message have not been installed.",Toast.LENGTH_SHORT).show();
            }
        }
        if (tv_whatsapp.getId()==id){
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ServiceListActivity.this,"Whatsapp have not been installed.",Toast.LENGTH_SHORT).show();
            }
        }if (tv_gmail.getId()==id){
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.google.android.gm");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ServiceListActivity.this,"email have not been installed.",Toast.LENGTH_SHORT).show();
            }
        }if (tv_message.getId()==id){
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage(getDefaultSmsAppPackageName(ServiceListActivity.this));
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ServiceListActivity.this,"Message have not been installed.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            finish();
        }
    }

    public static String getDefaultSmsAppPackageName( Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            return Telephony.Sms.getDefaultSmsPackage(context);
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms");
            final List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
            if (resolveInfos != null && !resolveInfos.isEmpty())
                return resolveInfos.get(0).activityInfo.packageName;
            return null;
        }
    }

    class ValidateUser extends AsyncTask<String,Void,String> {
        ProgressDialog progressBar;

        String requestURL= Constants.URL+"ws_user_status.php?id=1";
        HashMap<String, String> postDataParams;

        public ValidateUser(HashMap<String, String> postDataParams) {
            this.postDataParams = postDataParams;
            progressBar = new ProgressDialog(ServiceListActivity.this);
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

                }if (status.equals("1")){

                    JSONArray data= jsonObject.getJSONArray("data");
                    JSONObject object=data.getJSONObject(0);
                    String Status=object.getString("status");

                    if (Status.equals("block")) {

                        Intent i = new Intent(ServiceListActivity.this, AlertActivity.class);
                        i.putExtra("type", "activestatus");
                        startActivity(i);
                        finish();
                    }else {


                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class GetServiceList extends AsyncTask<String,Void,String> {
        ProgressDialog progressBar;

        String requestURL= Constants.URL+"ws_service_list.php";
        HashMap<String, String> postDataParams;

        public GetServiceList(HashMap<String, String> postDataParams) {
            this.postDataParams = postDataParams;
            progressBar = new ProgressDialog(ServiceListActivity.this);
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


                }if (status.equals("1")){

                    JSONArray jsonArray= jsonObject.getJSONArray("data");
                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject object = new JSONObject(jsonArray.get(i).toString());
                        Services services = new Services();
                        services.setId(object.getString("id"));
                        String imgurl=object.getString("images").replace("[","").replace("]","").replace("\"","");
                        services.setImage("http://inncrotech.site/project2/upload/"+imgurl);
                        services.setName(object.getString("servicename"));
                        services.setDescription(object.getString("description"));
                        Servicelist.add(services);
                    }

                    Adapter_ServiceList adapter = new Adapter_ServiceList(ServiceListActivity.this,Servicelist);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);
                }else {
                    Intent intent=new Intent(ServiceListActivity.this,AlertActivity.class);
                    intent.putExtra("type","no products");
                    startActivityForResult(intent,100);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
