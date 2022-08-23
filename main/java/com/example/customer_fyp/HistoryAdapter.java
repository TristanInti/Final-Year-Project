package com.example.customer_fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class HistoryAdapter extends FirestoreRecyclerAdapter<History, HistoryAdapter.HistoryHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HistoryAdapter(@NonNull FirestoreRecyclerOptions<History> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HistoryHolder holder, int position, @NonNull History model) {
        holder.historyModel.setText(model.getModel());
        holder.historyBrand.setText(model.getBrand());
        holder.historyQuantity.setText(model.getQuantity());
        holder.historyID.setText(model.getItemID());
        holder.historyPrice.setText(model.getPrice());
        holder.orderEmail.setText(model.getEmail());
        Glide.with(holder.historyImage.getContext()).load(model.getImages()).into(holder.historyImage);
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history, parent, false);
        return new HistoryHolder(v);
    }

    //delete item at position
    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class HistoryHolder extends RecyclerView.ViewHolder {

        public TextView historyModel;
        public TextView historyBrand;
        public TextView historyPrice;
        public TextView historyQuantity;
        public TextView historyID;
        public TextView orderEmail;
        public ImageView historyImage;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);

            historyModel = itemView.findViewById(R.id.historyModel);
            historyBrand = itemView.findViewById(R.id.historyBrand);
            historyQuantity = itemView.findViewById(R.id.historyQuantity);
            historyID = itemView.findViewById(R.id.historyID);
            historyPrice = itemView.findViewById(R.id.historyPrice);
            orderEmail = itemView.findViewById(R.id.orderEmail);
            historyImage = itemView.findViewById(R.id.historyImg);
        }
    }
}
