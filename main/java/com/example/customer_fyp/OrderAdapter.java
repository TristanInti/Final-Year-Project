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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;


public class OrderAdapter extends FirestoreRecyclerAdapter<Order, OrderAdapter.OrderHolder>{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("database/customer/order");

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public OrderAdapter(@NonNull FirestoreRecyclerOptions<Order> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderHolder holder, int position, @NonNull Order model) {
        holder.orderModel.setText(model.getOrderModel());
        holder.orderBrand.setText(model.getOrderBrand());
        holder.orderPrice.setText(model.getOrderPrice());
        holder.orderQuantity.setText(model.getQuantity());
        holder.idItem.setText(model.getItemID());
        Glide.with(holder.orderImage.getContext()).load(model.getImages()).into(holder.orderImage);
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order, parent, false);
        return new OrderHolder(v);
    }

    //delete item at position
    public void deleteProduct(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {

        public TextView orderModel;
        public TextView orderBrand;
        public TextView orderPrice;
        public TextView orderQuantity;
        public TextView idItem;
        public ImageView orderImage;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);

            orderModel = itemView.findViewById(R.id.modelOrder);
            orderBrand = itemView.findViewById(R.id.brandOrder);
            orderPrice = itemView.findViewById(R.id.priceOrder);
            orderQuantity = itemView.findViewById(R.id.quantityOrder);
            idItem = itemView.findViewById(R.id.idItem);
            orderImage = itemView.findViewById(R.id.orderImg);
        }
    }
}


