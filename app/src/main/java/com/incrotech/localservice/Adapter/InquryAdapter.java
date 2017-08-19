package com.incrotech.localservice.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.incrotech.localservice.Activity.EditInquiry;
import com.incrotech.localservice.R;
import com.incrotech.localservice.Utils.Inquiry;

import java.util.ArrayList;

/**
 * Created by user on 5/2/2017.
 */
public class InquryAdapter extends RecyclerView.Adapter<InquryAdapter.ViewHolder> {

    private static ArrayList<Inquiry> InquiryList;
    private LayoutInflater mInflater;
    private Context context;

    public InquryAdapter(Context context, ArrayList<Inquiry> InquiryList) {
        this.context=context;
        this.InquiryList = InquiryList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_inquirylist, parent, false);



        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.title.setText(InquiryList.get(position).getTitle());
        holder.priority.setText(InquiryList.get(position).getPriority());
        holder.description.setText(InquiryList.get(position).getDescription());
        holder.date.setText(InquiryList.get(position).getDate());
        if (!InquiryList.get(position).getStatus().equals("")){

            holder.notification.setImageDrawable(context.getResources().getDrawable(R.mipmap.notification));
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, EditInquiry.class);
                intent.putExtra("Id",InquiryList.get(position).getId());
                intent.putExtra("Name",InquiryList.get(position).getTitle());
                intent.putExtra("Priority",InquiryList.get(position).getPriority());
                intent.putExtra("Budget",InquiryList.get(position).getBudget());
                intent.putExtra("Budget_icon",InquiryList.get(position).getBudget_icon());
                intent.putExtra("Description",InquiryList.get(position).getDescription());
                intent.putExtra("Document",InquiryList.get(position).getFile());
                context.startActivity(intent);
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alert(position);
            }
        });
    }

    private void Alert(final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertLayout = inflater.inflate(R.layout.alert_viewinquiry, null);
        final AlertDialog dialog = new AlertDialog.Builder(context).create();


        Button edit=alertLayout.findViewById(R.id.btn_alertview_edit);
        ImageView cancel=alertLayout.findViewById(R.id.iv_niewinq_close);
        TextView budget=alertLayout.findViewById(R.id.tv_viewinq_budget);
        TextView name=alertLayout.findViewById(R.id.tv_viewinq_name);
        TextView desc=alertLayout.findViewById(R.id.tv_viewinq_desc);
        TextView priority=alertLayout.findViewById(R.id.tv_viewinq_priority);
        TextView date=alertLayout.findViewById(R.id.tv_viewinq_date);

        if (InquiryList.get(position).getBudget_icon().equals("0")){

            budget.setText(context.getResources().getString(R.string.dollar)+" "+InquiryList.get(position).getBudget());

        }else if (InquiryList.get(position).getBudget_icon().equals("0")){

            budget.setText(context.getResources().getString(R.string.Rs)+" "+InquiryList.get(position).getBudget());
        }
        else if (InquiryList.get(position).getBudget_icon().equals("0")){

            budget.setText(context.getResources().getString(R.string.pound)+" "+InquiryList.get(position).getBudget());
        }
        else if (InquiryList.get(position).getBudget_icon().equals("0")){

            budget.setText(context.getResources().getString(R.string.euro)+" "+InquiryList.get(position).getBudget());
        }

        name.setText(InquiryList.get(position).getTitle());
        desc.setText(InquiryList.get(position).getDescription());
        priority.setText(InquiryList.get(position).getPriority());
        date.setText(InquiryList.get(position).getDate());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, EditInquiry.class);
                intent.putExtra("Id",InquiryList.get(position).getId());
                intent.putExtra("Name",InquiryList.get(position).getTitle());
                intent.putExtra("Priority",InquiryList.get(position).getPriority());
                intent.putExtra("Budget",InquiryList.get(position).getBudget());
                intent.putExtra("Budget_icon",InquiryList.get(position).getBudget_icon());
                intent.putExtra("Description",InquiryList.get(position).getDescription());
                intent.putExtra("Document",InquiryList.get(position).getFile());
                context.startActivity(intent);
            }
        });

        dialog.setView(alertLayout);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return InquiryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,priority,description,date;
        ImageView edit,notification;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);

            title=view.findViewById(R.id.tv_inquiryadpt_title);
            priority=view.findViewById(R.id.tv_inquiryadpt_priority);
            description=view.findViewById(R.id.tv_inquiryadpt_desc);
            date=view.findViewById(R.id.tv_inquiryadpt_date);
            edit=view.findViewById(R.id.iv_adptinq_edit);
            notification=view.findViewById(R.id.iv_adptinq_notification);
            linearLayout=view.findViewById(R.id.ll_inqadpt);
        }
    }

}