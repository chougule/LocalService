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

import com.incrotech.localservice.Activity.InquiryActivity;
import com.incrotech.localservice.R;
import com.incrotech.localservice.Utils.Services;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jeet on 18/7/17.
 */

public class Adapter_ServiceList  extends RecyclerView.Adapter<Adapter_ServiceList.ViewHolder> {

    List<Services> services;
    Context context;

    public Adapter_ServiceList(Context context, List<Services> service) {
        this.context=context;
        this.services=service;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_listservices, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int i=0;
        if (position==1){
            i=R.mipmap.ic_launcher;
        }else {
            i=R.mipmap.ic_launcher;
        }
        String url=services.get(position).getImage();
        Picasso.with(context)
                .load(url)
                .error(i)
                .into(holder.imageView);
        holder.servicename.setText(services.get(position).getName());
        holder.servicedesc.setText(services.get(position).getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent((Activity)context, InquiryActivity.class);
                intent.putExtra("service_name",services.get(position).getName());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView servicename,servicedesc;

        public ViewHolder(View view) {
            super(view);

            imageView=view.findViewById(R.id.iv_serviceadpt_image);
            servicename=view.findViewById(R.id.tv_serviceadpt_servicename);
            servicedesc=view.findViewById(R.id.tv_serviceadpt_servicedesc);
        }
    }
}

