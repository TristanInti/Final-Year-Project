package com.example.customer_fyp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class OrderProducts extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private ConfirmOrderAdapter adapter;

    private FirebaseAuth mAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private CollectionReference adminRef = db.collection("database");
    private CollectionReference orderRef = db.collection("database/customer/order");
    private CollectionReference confirmOrderRef = db.collection("database/customer/customerOrder");
    private DocumentReference productRef = db.collection("database/customer/order").document();
    private CollectionReference trackOrderRef = db.collection("database/customer/trackOrder");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        //for firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //reference for the storage location
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        //Initialize and set support for toolbar
        Toolbar toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);

        //Set a listener that will be notified when a menu item is selected.
        drawer = findViewById(R.id.draw_layout6);
        NavigationView navigationView = findViewById(R.id.nav6);
        navigationView.setNavigationItemSelectedListener(this);

        //Toggle for action bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Get headerview and settext of the navigation to customer email
        NavigationView viewNavigation = (NavigationView) findViewById(R.id.nav6);
        View headerView = viewNavigation.getHeaderView(0);
        TextView navAdmin = (TextView) headerView.findViewById(R.id.username_admin);
        String customer = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        navAdmin.setText(customer);

        setRecyclerView();
    }

    //set up recycler view
    private void setRecyclerView() {

        Query query = confirmOrderRef.orderBy("subtotal", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ConfirmOrder> options = new FirestoreRecyclerOptions.Builder<ConfirmOrder>().setQuery(query, ConfirmOrder.class).build();

        adapter = new ConfirmOrderAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView5);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new WrapContent(this, LinearLayoutManager.VERTICAL, false));
        //Spacing
        Spacing itemDecorator = new Spacing(25);
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //itemtouch helper to delete the data on swipe
        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                        AlertDialog.Builder build;
                        build = new AlertDialog.Builder(viewHolder.itemView.getContext());

                        build.setMessage("Are you sure you want to delete this product?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        adapter.deleteItem(viewHolder.getAbsoluteAdapterPosition());
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        adapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                                    }
                                });

                        AlertDialog alert = build.create();
                        alert.setTitle("Delete Product Confirmation");
                        alert.show();
                    }
                }).attachToRecyclerView(recyclerView);

        //On click for adapter
        adapter.setOnItemClickListener(new ConfirmOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                String customer = adapter.getItem(position).getCustomer();
                String id = adapter.getItem(position).getOrderID();
                int itemQuantity = adapter.getItem(position).getItemQuantity();
                String totalItemQuantity = adapter.getItem(position).getTotalItemQuantity();
                String subtotal = adapter.getItem(position).getSubtotal();
                long location = adapter.getItemId(position);

                Intent intent = new Intent(OrderProducts.this, TrackingDelivery.class);

                intent.putExtra(TrackingDelivery.CUSTOMER, customer);
                intent.putExtra(TrackingDelivery.ID, id);
                intent.putExtra(TrackingDelivery.ITEMQUANTITY, String.valueOf(itemQuantity));
                intent.putExtra(TrackingDelivery.TOTALITEMQUANTITY, totalItemQuantity);
                intent.putExtra(TrackingDelivery.SUBTOTAL, subtotal);
                intent.putExtra(TrackingDelivery.LOCATION, String.valueOf(location));

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    //Navigation's for the menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.editAcc:

                Intent intent = new Intent(OrderProducts.this, EditAccount.class);
                startActivity(intent);

                break;

            case R.id.delAcc:

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(this);

                builder.setMessage("Are you sure you want to delete your account?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseUser admin = FirebaseAuth.getInstance().getCurrentUser();

                                admin.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(OrderProducts.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(OrderProducts.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(OrderProducts.this, "Account deletion unsuccessful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle("Delete Account Confirmation");
                alert.show();

                break;

            case R.id.home:
                Intent intent1 = new Intent(OrderProducts.this, ProductPage.class);
                startActivity(intent1);
                break;

            case R.id.cart:
                Intent intent2 = new Intent(OrderProducts.this, Cart.class);
                startActivity(intent2);
                break;

            case R.id.orderHistory:
                Intent intent3 = new Intent(OrderProducts.this, OrderHistory.class);
                startActivity(intent3);
                break;

            case R.id.trackDelivery:
                Intent intent4 = new Intent(OrderProducts.this, OrderProducts.class);
                startActivity(intent4);
                break;

            case R.id.logout:

                FirebaseAuth.getInstance().signOut();
                Intent it = new Intent(OrderProducts.this, MainActivity.class);
                startActivity(it);

                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
