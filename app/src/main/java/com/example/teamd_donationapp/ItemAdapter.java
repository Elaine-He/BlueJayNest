package com.example.teamd_donationapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by joanne.
 */
public class ItemAdapter extends ArrayAdapter<Item> {

    int resource;

    public ItemAdapter(Context ctx, int res, List<Item> items)
    {
        super(ctx, res, items);
        resource = res;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView;
        Item it = getItem(position);

        if (convertView == null) {
            itemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, itemView, true);
        } else {
            itemView = (LinearLayout) convertView;
        }

        TextView nameView = itemView.findViewById(R.id.title);
        ImageView photo = itemView.findViewById(R.id.photo_icon);
        //photo.setImageResource(it.getImage());
        //TextView categoryView = itemView.findViewById(R.id.category);
        ImageView alertIcon = itemView.findViewById(R.id.alert_icon);
        nameView.setText(it.getName());
        Glide.with(getContext()).load(it.getImageURL()).into(photo);
//        dateView.setText(it.getDone() != null ? it.getDone() : it.getDue());
        //categoryView.setText(it.getCategory());

        return itemView;
    }

}
