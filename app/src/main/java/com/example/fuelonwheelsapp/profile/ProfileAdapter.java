package com.example.fuelonwheelsapp.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuelonwheelsapp.R;

import java.util.ArrayList;



public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private final ArrayList<ProfileItem> profileItems;
    private OnItemClickListener onItemClickListener =null;

    public interface OnItemClickListener{
        public void onClick(View view,int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            imageView = (ImageView) view.findViewById(R.id.profile_item_imageView);
            textView = (TextView) view.findViewById(R.id.profile_item_textView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(view,getAdapterPosition());
                }
            });
        }
        public ImageView getImageView() {
            return imageView;
        }
        public TextView getTextView() {
            return textView;
        }
    }

    public ProfileAdapter(ArrayList<ProfileItem> dataSet,OnItemClickListener clickListener) {
        this.profileItems = dataSet;
        this.onItemClickListener = clickListener;
        //notify state changed
    }
    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.profile_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        holder.getImageView().setImageResource(profileItems.get(position).getIconId());
        holder.getTextView().setText(profileItems.get(position).getItem());
    }

    @Override
    public int getItemCount() {
        return profileItems.size();
    }
}
