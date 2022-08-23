package com.example.customer_fyp;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;

import java.security.KeyStore;

public class ProductAdapter extends FirestoreRecyclerAdapter<Product, ProductAdapter.ProductHolder> {

    private OnItemClickListener listener;
    public static final String STOCK = "com.example.customer_fyp.STOCK";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {

        holder.model.setText(model.getModel());
        holder.brand.setText(model.getBrand());
        holder.stock.setText(String.valueOf(model.getStock()));
        holder.price.setText(String.valueOf(model.getPrice()));
        Glide.with(holder.image.getContext()).load(model.getImages()).into(holder.image);
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false);
        return new ProductHolder(v);
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        public TextView model;
        public TextView brand;
        public TextView stock;
        public TextView price;
        public ImageView image;
        //public ImageView editProduct;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);

            model = itemView.findViewById(R.id.model);
            brand = itemView.findViewById(R.id.brand);
            stock = itemView.findViewById(R.id.amount);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.productImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION&&listener!=null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }

