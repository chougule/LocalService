package com.incrotech.localservice.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.incrotech.localservice.Activity.InquiryActivity;
import com.incrotech.localservice.R;
import com.incrotech.localservice.Utils.Services;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jeet on 16/8/17.
 */

public class ServiceAdapter extends BaseAdapter {

    List<Services> services;
    Context context;

    public ServiceAdapter(Context context, List<Services> service) {

        this.context=context;
        this.services=service;
    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertview, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (convertview==null){
            viewHolder =new ViewHolder();
            LayoutInflater layoutInflater=LayoutInflater.from(context);
            convertview=layoutInflater.inflate(R.layout.adapter_listservices,viewGroup,false);

            viewHolder.imageView=convertview.findViewById(R.id.iv_serviceadpt_image);
            viewHolder.servicename=convertview.findViewById(R.id.tv_serviceadpt_servicename);
            viewHolder.servicedesc=convertview.findViewById(R.id.tv_serviceadpt_servicedesc);

            convertview.setTag(viewHolder);
        }else {

            viewHolder = (ViewHolder) convertview.getTag();
        }
            String url = services.get(position).getImage();
            Picasso.with(context)
                    .load(url)
                    .error(R.mipmap.ic_launcher)
                    .into(viewHolder.imageView);
            viewHolder.servicename.setText(services.get(position).getName());
            viewHolder.servicedesc.setText(services.get(position).getDescription());


            return convertview;

    }

    public class ViewHolder{

        ImageView imageView;
        TextView servicename,servicedesc;
    }
}
