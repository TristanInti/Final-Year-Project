package com.example.customer_fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class TrackingDelivery extends AppCompatActivity {

    public static final String TAG = "TrackingDelivery";
    public static final String CUSTOMER = "com.example.customer_fyp.CUSTOMER";
    public static final String ID = "com.example.customer_fyp.ID";
    public static final String ITEMQUANTITY = "com.example.customer_fyp.ITEMQUANTITY";
    public static final String TOTALITEMQUANTITY = "com.example.customer_fyp.TOTALITEMQUANTITY";
    public static final String SUBTOTAL = "com.example.customer_fyp.SUBTOTAL";
    public static final String LOCATION = "com.example.customer_fyp.LOCATION";

    private DrawerLayout drawer;

    private FirebaseAuth mAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private CollectionReference adminRef = db.collection("database");
    private CollectionReference orderRef = db.collection("database/customer/order");
    private CollectionReference confirmOrderRef = db.collection("database/customer/customerOrder");
    private DocumentReference productRef = db.collection("database/customer/order").document();
    private CollectionReference deliveryStatusRef = db.collection("database/customer/delivery");

    private MaterialTextView mCustomer;
    private MaterialTextView mSubtotal;
    private MaterialTextView mOrderID;
    private MaterialTextView mItemQuantity;
    private MaterialTextView mTotalItemQuantity;
    private Button mCancelled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_delivery);

        mAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        mCustomer = findViewById(R.id.trackCustomer);
        mSubtotal = findViewById(R.id.trackSubtotal);
        mOrderID = findViewById(R.id.trackID);
        mItemQuantity = findViewById(R.id.trackItemQuantity);
        mTotalItemQuantity = findViewById(R.id.trackTotalQuantity);
        mCancelled = findViewById(R.id.cancelButton);

        Intent intent = getIntent();

        mCustomer.setText(intent.getStringExtra(CUSTOMER));
        mSubtotal.setText(intent.getStringExtra(SUBTOTAL));
        mOrderID.setText(intent.getStringExtra(ID));
        mItemQuantity.setText(intent.getStringExtra(ITEMQUANTITY));
        mTotalItemQuantity.setText(intent.getStringExtra(TOTALITEMQUANTITY));

    }
}
