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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class OrderHistory extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;

    private FirebaseAuth mAuth;

    private HistoryAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private CollectionReference adminRef = db.collection("database");
    private CollectionReference orderRef = db.collection("database/customer/order");
    private DocumentReference confirmOrderRef = db.collection("database/customer/customerOrder").document();
    private DocumentReference productRef = db.collection("database/customer/order").document();
    private CollectionReference historyRef = db.collection("database/customer/history");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderhistory);

        //for firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //reference for the storage location
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        //Initialize and set support for toolbar
        Toolbar toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);

        //Set a listener that will be notified when a menu item is selected.
        drawer = findViewById(R.id.draw_layout7);
        NavigationView navigationView = findViewById(R.id.nav7);
        navigationView.setNavigationItemSelectedListener(this);

        //Toggle for action bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Get headerview and settext of the navigation to customer email
        NavigationView viewNavigation = (NavigationView) findViewById(R.id.nav7);
        View headerView = viewNavigation.getHeaderView(0);
        TextView navAdmin = (TextView) headerView.findViewById(R.id.username_admin);
        String customer = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        navAdmin.setText(customer);

        setUpRecyclerView();
    }

    //recycler view
    private void setUpRecyclerView() {
        Query query = historyRef.orderBy("price", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<History> options = new FirestoreRecyclerOptions.Builder<History>().setQuery(query, History.class).build();

        adapter = new HistoryAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView6);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new WrapContent(this, LinearLayoutManager.VERTICAL, false));
        //spacing
        Spacing itemDecorator = new Spacing(25);
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //delete item on item swipe
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                //alert dialog to ask user to confirm deletion
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

    //navigation menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.editAcc:

                Intent intent = new Intent(OrderHistory.this, EditAccount.class);
                startActivity(intent);

                break;

            case R.id.delAcc:

                //Alert dialog to delete account
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
                                            Toast.makeText(OrderHistory.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(OrderHistory.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(OrderHistory.this, "Account deletion unsuccessful", Toast.LENGTH_SHORT).show();
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
                Intent intent1 = new Intent(OrderHistory.this, ProductPage.class);
                startActivity(intent1);
                break;

            case R.id.cart:
                Intent intent2 = new Intent(OrderHistory.this, Cart.class);
                startActivity(intent2);
                break;

            case R.id.orderHistory:
                Intent intent3 = new Intent(OrderHistory.this, OrderHistory.class);
                startActivity(intent3);
                break;

            case R.id.trackDelivery:
                Intent intent4 = new Intent(OrderHistory.this, OrderProducts.class);
                startActivity(intent4);
                break;

            case R.id.logout:

                FirebaseAuth.getInstance().signOut();
                Intent it = new Intent(OrderHistory.this, MainActivity.class);
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
