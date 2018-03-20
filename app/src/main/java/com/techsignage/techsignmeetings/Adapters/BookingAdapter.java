package com.techsignage.techsignmeetings.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techsignage.techsignmeetings.Applications.TechApp;
import com.techsignage.techsignmeetings.BookActivity;
import com.techsignage.techsignmeetings.Models.HourModel;
import com.techsignage.techsignmeetings.Models.Interfaces.hourCallback;
import com.techsignage.techsignmeetings.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heat on 5/18/2017.
 */

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ListViewHolder>
{
    hourCallback callback;
    final List<HourModel> selected_all = new ArrayList<>();

//    public interface SingleChoiceCallback {
//        void selectedItem(HourModel selectedModel);
//    }

    class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout rel1;
        TextView tv_hour;
        TextView tv_container;

        public ListViewHolder(View itemView) {
            super(itemView);

            rel1 = (RelativeLayout) itemView.findViewById(R.id.rel1);
            tv_hour = (TextView) itemView.findViewById(R.id.tv_hour);
            tv_container = (TextView) itemView.findViewById(R.id.tv_container);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            BookActivity bookActivity = (BookActivity)context;
            bookActivity.setTimer();

            final HourModel selected = dataList.get(getAdapterPosition());
            if(dataList.get(getAdapterPosition()).IsSelected)
            {
                if((getAdapterPosition()-1)>0 && (getAdapterPosition()+1)<dataList.size())
                {
                    if (dataList.get((getAdapterPosition()-1)).IsSelected && dataList.get((getAdapterPosition()+1)).IsSelected)
                    {
                        Toast.makeText(context, "Unselect one of the Ancestors", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                selected.IsSelected = false;
                selected_all.remove(dataList.get(getAdapterPosition()));
            }
            else
            {
                selected.IsSelected = true;
                if (selected.IsEnabled && !selected.IsBooked)
                    selected_all.add(dataList.get(getAdapterPosition()));
            }
            if(!selected.IsEnabled)
            {
                Toast.makeText(context, "Expired", Toast.LENGTH_SHORT).show();
                return;
            }
            if(selected.IsBooked)
            {
                Toast.makeText(context, "Booked", Toast.LENGTH_SHORT).show();
                return;
            }
            Boolean IsValid = true;
            //step 1 validation
            for (int i = 0; i<dataList.size(); i++)
            {
                if(dataList.get(i).IsSelected)
                {
                    IsValid = false;
                    break;
                }
            }
            //step 2 validation
            for (int i = 0; i<dataList.size(); i++)
            {
                if(i == getAdapterPosition())
                {
                    if(i>0 && (i+1)<dataList.size())
                    {
                        if ((dataList.get(i-1).IsSelected) || (dataList.get(i+1).IsSelected))
                        {
                            IsValid = true;
                        }
                        else
                        {
                            if (selected_all.size() == 1 || selected_all.size() == 0)
                            {
                                IsValid = true;
                            }
                            else
                            {
                                IsValid = false;
                            }
                        }

                    }
                }
            }

            if (selected_all.size() == 1)
            {
                IsValid = true;
            }

//            int cnt = 0;
//            for (int i = 0; i<dataList.size(); i++)
//            {
//                if(dataList.get(i).IsSelected && !selected_all.contains(dataList.get(i)))
//                {
//                    cnt++;
//                }
//            }
//
//            if (cnt == 0)
//                IsValid = true;
            if(!IsValid)
            {
                selected.IsSelected = false;
                selected_all.remove(dataList.get(getAdapterPosition()));
                Toast.makeText(context, "Select adjacent slots", Toast.LENGTH_SHORT).show();
                return;
            }

            callback.selectedItem(selected, selected_all);
            String Id = context.getResources().getResourceName(view.getId());
            if (Id.contains("rel1")) {
                //Drawable myIcon = context.getResources().getDrawable( R.drawable.right_pic );
                if (dataList.get(getAdapterPosition()).IsEnabled)
                    if(dataList.get(getAdapterPosition()).IsSelected)
                    {
                        tv_container.setBackgroundResource(R.drawable.right_pic);
                    }
                    else
                    {
                        tv_container.setBackgroundResource(R.drawable.white_bg);
                    }
                else
                    Toast.makeText(context, R.string.expired, Toast.LENGTH_SHORT).show();

                if (dataList.get(getAdapterPosition()).IsBooked)
                {
                    tv_container.setBackgroundResource(R.drawable.booked_pic);
                    Toast.makeText(context, R.string.booked, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    Context context;
    List<HourModel> dataList = new ArrayList<>();
    LayoutInflater inflater;
    //Listener listener;
    //Context con;

    public BookingAdapter(Context context, hourCallback callback) {

        this.context = context;
        this.callback = callback;
        //this.con= context;
        inflater = LayoutInflater.from(context);
    }

    public void setLst(List<HourModel> lst)
    {
        this.dataList = lst;
    }

    public List<HourModel> getLst()
    {
        return this.dataList;
    }

    @Override
    public BookingAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View convertView = inflater.inflate(R.layout.book_item, parent, false);
        return new BookingAdapter.ListViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final BookingAdapter.ListViewHolder holder, final int position) {
        try
        {
            holder.tv_hour.setText(dataList.get(position).StartHour);
            holder.tv_container.setBackgroundResource(R.drawable.white_bg);

            if (dataList.get(position).IsSelected != null)
            {
                if (dataList.get(position).IsSelected)
                {
                    holder.tv_container.setBackgroundResource(R.drawable.right_pic);
                }
            }

            if (dataList.get(position).IsEnabled != null)
            {
                if (!dataList.get(position).IsEnabled)
                {
                    holder.tv_container.setBackgroundResource(R.drawable.expired);
                }
            }

            if (dataList.get(position).IsBooked)
            {
                holder.tv_container.setBackgroundResource(R.drawable.booked_pic);
            }
        }
        catch (Exception ex)
        {
            String ss = "";
        }

    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

}