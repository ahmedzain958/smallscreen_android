package com.techsignage.techsignmeetings.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.techsignage.techsignmeetings.Models.UnitModel;
import com.techsignage.techsignmeetings.R;

import java.util.List;

/**
 * Created by heat on 8/6/2017.
 */

public class RoomsAdapter extends ArrayAdapter<UnitModel> {

    private List<UnitModel> objects;
    private Context context;

    public RoomsAdapter(Context context, int resourceId,
                         List<UnitModel> objects) {
        super(context, resourceId, objects);
        this.objects = objects;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View row=inflater.inflate(R.layout.spinner_item, parent, false);
        TextView label = (TextView)row.findViewById(R.id.txt1);
        label.setText(objects.get(position).UNIT_NAME);

        if (position == 0) {//Special style for dropdown header
            label.setTextColor(context.getResources().getColor(R.color.black));
        }

        return row;
    }
}
