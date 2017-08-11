package com.incrotech.localservice.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.incrotech.localservice.R;
import com.incrotech.localservice.Utils.Offer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jeet on 28/7/17.
 */

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private  ArrayList<Offer> offers;
    private LayoutInflater mInflater;
    private Context context;

    public OfferAdapter(Context context, ArrayList<Offer> offer) {
        this.context=context;
        this.offers = offer;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_offer, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.offername.setText(offers.get(position).getOffer_name());
        holder.description.setText(offers.get(position).getOffer_desc());
        if (offers.get(position).getOfferimage().equals("")){
            holder.layout.setVisibility(View.GONE);
        }else {
            holder.layout.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load("http://inncrotech.site/project2/upload/" + offers.get(position).getOfferimage())
                    .into(holder.offerimage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                            holder.progress.setVisibility(View.GONE);
                            holder.offerimage.setImageDrawable(context.getResources().getDrawable(R.drawable.na_image));

                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView offerimage;
        TextView offername,description;
        ProgressBar progress;
        RelativeLayout layout;

        public ViewHolder(View view) {
            super(view);

            offerimage= (ImageView)view. findViewById(R.id.offer_image);
            offername= (TextView) view. findViewById(R.id.offer_name);
            description= (TextView) view. findViewById(R.id.offer_desc);
            progress = (ProgressBar) view. findViewById(R.id.offer_pdlg);
            layout = (RelativeLayout) view. findViewById(R.id.rl_offer);
        }
    }

}
