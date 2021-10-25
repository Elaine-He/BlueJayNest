package com.example.teamd_donationapp.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.teamd_donationapp.Item;
import com.example.teamd_donationapp.ItemGiverDisplayActivity;
import com.example.teamd_donationapp.ItemRecieverAcceptedActivity;
import com.example.teamd_donationapp.ItemRecieverDisplayActivity;
import com.example.teamd_donationapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ClaimedHomeFragmentRecyclerViewAdapter extends RecyclerView.Adapter<ClaimedHomeFragmentRecyclerViewAdapter.ViewHolder> {
    private List<Item> localDataSet; // list of strings, change as needed
    private LayoutInflater inflater;
    private Context context;

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

        public ImageView getImageView() {
            return mImageView;
        }

        public ImageView getNotificationIcon() {return notificationIcon;}

        public TextView getmTextView2() {
            return mTextView2;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public ClaimedHomeFragmentRecyclerViewAdapter(Context context, List<Item> dataSet) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.localDataSet = dataSet;
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
        Item item = localDataSet.get(position);
        viewHolder.getTextView().setText(item.getName()); // item name
        viewHolder.getmTextView2().setText(item.getDescription());
        Picasso.get().load(item.getImageURL()).fit().centerCrop().into(viewHolder.mImageView); // item picture

        if (item.getStatus() != null && item.isAccepted().equals("true")) { // draw claimed icon
            viewHolder.getNotificationIcon().setImageResource(R.drawable.ic_notifications_black_24dp);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch new activity
                if (item.isAccepted().equals("true")) {
                    Intent intent = new Intent(context, ItemRecieverAcceptedActivity.class);
                    intent.putExtra("item", item);
                    context.startActivity(intent);
                    Toast.makeText(context, "opening item details", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(context, ItemRecieverDisplayActivity.class);
                    intent.putExtra("item", item);
                    context.startActivity(intent);
                    Toast.makeText(context, "opening item details", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public Item getItem(int index) {
        return localDataSet.get(index);
    }
}