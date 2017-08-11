package com.incrotech.localservice.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.incrotech.localservice.Activity.EditInquiry;
import com.incrotech.localservice.Activity.InquiryActivity;
import com.incrotech.localservice.R;
import com.incrotech.localservice.Utils.Inquiry;
import com.incrotech.localservice.Utils.Services;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jeet on 26/7/17.
 */

public class DrawerServiceAdapter extends RecyclerView.Adapter<DrawerServiceAdapter.ViewHolder> {

    private static ArrayList<Services> ServicesList;
    private LayoutInflater mInflater;
    private Context context;

    public DrawerServiceAdapter(Context context, ArrayList<Services> ServicesList) {
        this.context=context;
        this.ServicesList = ServicesList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_drawerservicelist, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Picasso.with(context)
                .load(ServicesList.get(position).getImage())
                .error(R.drawable.na_image)
                .into(holder.icon);
        holder.title.setText(ServicesList.get(position).getName());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent((Activity)context, InquiryActivity.class);
                intent.putExtra("service_name",ServicesList.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ServicesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public ViewHolder(View view) {
            super(view);

            icon=view.findViewById(R.id.iv_serviceicon);
            title=view.findViewById(R.id.tv_servicename);

        }
    }
}
