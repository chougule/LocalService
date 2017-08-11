package com.incrotech.localservice.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.incrotech.localservice.R;

/**
 * Created by jeet on 17/7/17.
 */

public class AlertActivity extends Activity {

    String type;
    TextView head,msg;
    Button ok;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alert);

        Intent intent=getIntent();
        if (intent!=null){

            type=intent.getStringExtra("type");
        }

        head= (TextView) findViewById(R.id.tv_alert);
        msg= (TextView) findViewById(R.id.tv_alert_msg);

        if (type.equals("no products")){
            head.setText("No Services");
            msg.setText("No services found in this category please wait for adding new services in this category");
        }else if(type.equals("no connection")) {
            head.setText("No Internet Connection");
            msg.setText("We cannot detect internet connection. Please check your internet connection and try again");
        }else if(type.equals("activestatus")){
            head.setText("Activation Status");
            msg.setText("Your Activation has been canceled please contact administrator");
        }
        ok= (Button) findViewById(R.id.btn_alert_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
