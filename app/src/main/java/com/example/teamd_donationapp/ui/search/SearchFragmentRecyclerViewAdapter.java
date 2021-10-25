package com.example.teamd_donationapp.ui.search;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.teamd_donationapp.Item;
import com.example.teamd_donationapp.ItemRecieverDisplayActivity;
import com.example.teamd_donationapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchFragmentRecyclerViewAdapter extends RecyclerView.Adapter<SearchFragmentRecyclerViewAdapter.ViewHolder> implements Filterable {
    private List<Item> localDataSet; // list of strings, change as needed
    private List<Item> filteredData;
    private LayoutInflater inflater;
    private Context context;

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Item> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()){
                filteredList.addAll(localDataSet);
            }
            else{
                for(Item i: localDataSet) {
                    Log.d("item", i.getName());
                    if(i.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(i);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values =filteredList;

            return filterResults;
        }

        //run on ui thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData.clear();
            filteredData.addAll((Collection<? extends Item>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTextView;
        private final ImageView mImageView;
        private final ImageView notificationIcon;
        private final TextView mTextView2;

        public ViewHolder(View v) {
            super(v);
            mTextView =(TextView) v.findViewById(R.id.itemName);
            mImageView = (ImageView) v.findViewById(R.id.itemImage);
            notificationIcon = (ImageView) v.findViewById(R.id.itemNotificationIcon);
            mTextView2 = (TextView) v.findViewById(R.id.itemDescription);
        }

        public TextView getTextView() {
            return mTextView;
        }

        public TextView getmTextView2() {
            return mTextView2;
        }

        public ImageView getImageView() {
            return mImageView;
        }

        public ImageView getNotificationIcon() {return notificationIcon;}

    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public SearchFragmentRecyclerViewAdapter(Context context, List<Item> dataSet) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.localDataSet = dataSet;
        this.filteredData = new ArrayList<>(dataSet);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = inflater.inflate(R.layout.home_frag_recycler_layout, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        Item item = filteredData.get(position);

        if (item.getStatus().equals("claimed") || item.getStatus().equals("finished")) { // don't show claimed items
            return;
        }

        viewHolder.getTextView().setText(item.getName()); // item name
        viewHolder.getmTextView2().setText(item.getDescription()); //item description
        Picasso.get().load(item.getImageURL()).fit().centerCrop().into(viewHolder.mImageView); // item picture

//        if (item.getStatus().equals("claimed")) { // draw claimed icon
//            viewHolder.getNotificationIcon().setImageResource(R.drawable.ic_notifications_black_24dp);
//        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch new activity
                Intent intent = new Intent(context, ItemRecieverDisplayActivity.class);
                intent.putExtra("item", item);
                context.startActivity(intent);
                Toast.makeText(context, "opening item details", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public Item getItem(int index) {
        return filteredData.get(index);
    }
}