package com.example.customer_fyp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private FirebaseAuth mAuth;

    private ProductAdapter adapter;

    public static final String EMAIL = "Email";
    public static final String PASSWORD = "Password";
    public static final String TAG = "MainActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private CollectionReference adminRef = db.collection("database");
    private  CollectionReference accRef = db.collection("database/admin/product");
    private  CollectionReference file = db.collection("database/admin/fileImage");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        //Authentication for Firebase
        mAuth = FirebaseAuth.getInstance();

        //Reference for storage location
        storage = FirebaseStorage.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        //Set support for toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set a listener that will be notified when a menu item is selected.
        drawer = findViewById(R.id.draw_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Toggle for action bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Navigation view to settext to customer email
        NavigationView viewNavigation = (NavigationView)findViewById(R.id.nav_view);
        View headerView = viewNavigation.getHeaderView(0);
        TextView navAdmin = (TextView) headerView.findViewById(R.id.username_admin);
        String customer = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        navAdmin.setText(customer);

        setUpRecyclerView();
    }

    //Recycler view
    private void setUpRecyclerView() {
        Query query = file.orderBy("price", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();

        adapter = new ProductAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new WrapContent(this, LinearLayoutManager.VERTICAL, false));
        Spacing itemDecorator = new Spacing(25);
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //On click adapter
        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                Product product = documentSnapshot.toObject(Product.class);

                String id = adapter.getSnapshots().getSnapshot(position).getId();
                String disposition = adapter.getItem(position).toString();
                String modelOrder = adapter.getItem(position).getModel();
                String brandOrder = adapter.getItem(position).getBrand();
                int orderStock = adapter.getItem(position).getStock();
                String stockOrder = String.valueOf(orderStock);
                int orderPrice = adapter.getItem(position).getPrice();
                String priceOrder = String.valueOf(orderPrice);
                String imgOrder = adapter.getItem(position).getImages();
                Uri orderImg = Uri.parse(imgOrder);

                Intent intent = new Intent(ProductPage.this, OrderItem.class);

                intent.putExtra(OrderItem.MODEL, modelOrder);
                intent.putExtra(OrderItem.BRAND, brandOrder);
                intent.putExtra(OrderItem.STOCK, stockOrder);
                intent.putExtra(OrderItem.PRICE, priceOrder);
                intent.putExtra(OrderItem.IMG, orderImg.toString());
                intent.putExtra(OrderItem.DISPOSITION, disposition);
                intent.putExtra(OrderItem.ID, id);

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

    //Navigation Menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.editAcc:

                Intent intent = new Intent(ProductPage.this, EditAccount.class);
                startActivity(intent);

                break;

            case R.id.delAcc:

                //Alert dialog to confirm deletion of account
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
                                            Toast.makeText(ProductPage.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ProductPage.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(ProductPage.this, "Account deletion unsuccessful", Toast.LENGTH_SHORT).show();
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
            Intent intent1 = new Intent(ProductPage.this, ProductPage.class);
            startActivity(intent1);
            break;

            case R.id.cart:
                Intent intent2 = new Intent(ProductPage.this, Cart.class);
                startActivity(intent2);
                break;

            case R.id.orderHistory:
                Intent intent3 = new Intent(ProductPage.this, OrderHistory.class);
                startActivity(intent3);
                break;

            case R.id.trackDelivery:
                Intent intent4 = new Intent(ProductPage.this, OrderProducts.class);
                startActivity(intent4);
                break;

            case R.id.logout:

                FirebaseAuth.getInstance().signOut();
                Intent it = new Intent(ProductPage.this, MainActivity.class);
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
