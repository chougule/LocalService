package com.incrotech.localservice.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.incrotech.localservice.Adapter.InquryAdapter;
import com.incrotech.localservice.Adapter.OfferAdapter;
import com.incrotech.localservice.R;
import com.incrotech.localservice.Utils.Constants;
import com.incrotech.localservice.Utils.Offer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jeet on 28/7/17.
 */

public class OfferActivity extends AppCompatActivity {


    ArrayList<Offer> ListOffer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ListOffer=new ArrayList<>();
        new GetOffer().execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    class GetOffer extends AsyncTask<String,Void,String> {

        ProgressDialog progressBar;
        String requestURL= Constants.URL+"ws_offer.php";

        public GetOffer() {
            progressBar = new ProgressDialog(OfferActivity.this);
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
                writer.write("");

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

                    for (int i=0;i<data.length();i++){

                        JSONObject object=data.getJSONObject(i);

                        Offer offer=new Offer();
                        offer.setOffer_id(object.getString("id"));
                        offer.setOffer_name(object.getString("offername"));
                        offer.setOffer_desc(object.getString("offerdesc"));
                        offer.setOfferimage(object.getString("offerimage"));

                        ListOffer.add(offer);

                    }
                }

                RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycle_offer);
                OfferAdapter adapter = new OfferAdapter(OfferActivity.this,ListOffer);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
