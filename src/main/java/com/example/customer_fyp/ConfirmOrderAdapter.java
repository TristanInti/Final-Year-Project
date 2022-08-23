package com.example.customer_fyp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.TwitterAuthCredential;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class ConfirmOrderAdapter extends FirestoreRecyclerAdapter<ConfirmOrder, ConfirmOrderAdapter.ConfirmOrderHolder> {

    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ConfirmOrderAdapter(@NonNull FirestoreRecyclerOptions<ConfirmOrder> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ConfirmOrderHolder holder, int position, @NonNull ConfirmOrder model) {
        holder.mOrderID.setText(model.getOrderID());
        holder.mItemQuantity.setText(String.valueOf(model.getItemQuantity()));
        holder.mTotalItemQuantity.setText(model.getTotalItemQuantity());
        holder.mSubtotal.setText(model.getSubtotal());
    }

    @NonNull
    @Override
    public ConfirmOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery, parent, false);
        return new ConfirmOrderHolder(v);
    }

    //to delete item
    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public class ConfirmOrderHolder extends RecyclerView.ViewHolder {

        public TextView mOrderID;
        public TextView mItemQuantity;
        public TextView mTotalItemQuantity;
        public TextView mSubtotal;

        public ConfirmOrderHolder(@NonNull View itemView) {
            super(itemView);

            mOrderID = itemView.findViewById(R.id.idValue);
            mItemQuantity = itemView.findViewById(R.id.quantityValue);
            mTotalItemQuantity = itemView.findViewById(R.id.totalQuantityValue);
            mSubtotal = itemView.findViewById(R.id.priceValue);

            //set on click on the item in the position
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
