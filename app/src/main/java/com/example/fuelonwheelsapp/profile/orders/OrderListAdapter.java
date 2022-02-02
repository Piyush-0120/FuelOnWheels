package com.example.fuelonwheelsapp.profile.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuelonwheelsapp.R;

import java.util.ArrayList;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder>{
    private final ArrayList<Order> orderItems;
    private OnItemClickListener onItemClickListener = null;

    public interface OnItemClickListener{
        public void onClick(View view,int position);
    }

    public OrderListAdapter(ArrayList<Order> orderItems,OnItemClickListener onItemClickListener) {
        this.orderItems = orderItems;
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView orderId;
        private final TextView dateTime;
        private final TextView toAddress;
        private final TextView fromAddress;
        private final TextView amount;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            dateTime = (TextView) view.findViewById(R.id.tv_dateTime);
            toAddress = (TextView) view.findViewById(R.id.tv_to_address);
            fromAddress = (TextView) view.findViewById(R.id.tv_from_address);
            amount = (TextView) view.findViewById(R.id.tv_amount);
            orderId = (TextView) view.findViewById(R.id.tv_orderId);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(view,getAdapterPosition());
                }
            });
        }

        public TextView getDateTime() { return dateTime;}
        public TextView getToAddress() { return toAddress; }
        public TextView getFromAddress() { return fromAddress; }
        public TextView getAmount() { return amount; }
        public TextView getOrderId() { return orderId; }

    }

    @NonNull
    @Override
    public OrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.order_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.ViewHolder holder, int position) {
        holder.getFromAddress().setText(orderItems.get(position).getFuelLocation());
        holder.getToAddress().setText(orderItems.get(position).getUserLocation());
        holder.getAmount().setText(orderItems.get(position).getTotalAmount());
        holder.getDateTime().setText(orderItems.get(position).getDateTime());
        holder.getOrderId().setText(orderItems.get(position).getOrderId());
        // TODO : format the date time text view
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

}
