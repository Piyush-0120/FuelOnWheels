package com.example.fuelonwheelsapp.profile.account;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fuelonwheelsapp.databinding.FragmentAccountItemBinding;
import com.example.fuelonwheelsapp.placeholder.PlaceholderContent.PlaceholderItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AccountRecyclerViewAdapter extends RecyclerView.Adapter<AccountRecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener{

        public void onClick(View view,int position);
    }
    private final List<PlaceholderItem> mValues;
    private OnItemClickListener onItemClickListener = null;

    public AccountRecyclerViewAdapter(List<PlaceholderItem> items,OnItemClickListener clickListener) {
        mValues = items;
        onItemClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentAccountItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mContentType.setText(mValues.get(position).contentType);
        holder.mContentDescription.setText(mValues.get(position).contentDescription);
        holder.mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(view,holder.getBindingAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //public final TextView mId;
        public final TextView mContentType;
        public final TextView mContentDescription;
        public final ImageView mNextBtn;
        public PlaceholderItem mItem;

        public ViewHolder(FragmentAccountItemBinding binding) {
            super(binding.getRoot());
            mContentType = binding.contentType;
            mContentDescription = binding.contentDescription;
            mNextBtn = binding.Next;
        }
    }
}