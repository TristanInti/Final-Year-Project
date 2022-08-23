package com.example.customer_fyp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Cart extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public final String TAG = "Cart";

    private DrawerLayout drawer;

    private FirebaseAuth mAuth;

    private OrderAdapter adapter;

    private TextView shipping;
    private TextView subtotal;
    private TextView total;
    private Button mButton;

    private double itemTotal = 0.00;
    private double shippingCost = 5.00;
    private double totalCost = 0.00;
    private double subtotalCost = 0.00;
    private int quantityTotal = 0;
    private int totalItem = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private CollectionReference adminRef = db.collection("database");
    private CollectionReference orderRef = db.collection("database/customer/order");
    private DocumentReference confirmOrderRef = db.collection("database/customer/customerOrder").document();
    private DocumentReference productRef = db.collection("database/customer/order").document();
    private CollectionReference historyRef = db.collection("database/customer/history");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        shipping = findViewById(R.id.shipCost);
        total = findViewById(R.id.totalCost);
        subtotal = findViewById(R.id.subtotalCost);
        mButton =findViewById(R.id.confirmOrder);

        //for firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //reference for the storage location
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        //Initialize and set support for toolbar
        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);

        //Set a listener that will be notified when a menu item is selected.
        drawer = findViewById(R.id.draw_layout5);
        NavigationView navigationView = findViewById(R.id.nav5);
        navigationView.setNavigationItemSelectedListener(this);

        //Toggle for action bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Get headerview and settext of the navigation to customer email
        NavigationView viewNavigation = (NavigationView) findViewById(R.id.nav5);
        View headerView = viewNavigation.getHeaderView(0);
        TextView navAdmin = (TextView) headerView.findViewById(R.id.username_admin);
        String customer = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        navAdmin.setText(customer);

        setUpRecyclerView();

        getPriceData();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Alert dialog to ask for confirmation before order is placed
                AlertDialog.Builder builder;
                builder =new AlertDialog.Builder(Cart.this);
                builder.setMessage("Confirm Order?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //get data from orderRef
                        orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot :task.getResult()) {

                                    String customerEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    int itemQuantity = task.getResult().size();
                                    double sub = subtotalCost;
                                    String itemPrice = String.valueOf(String.format("%.2f", sub));
                                    String orderID = confirmOrderRef.getId();
                                    String totalItemQuantity = String.valueOf(quantityTotal);

                                    ConfirmOrder confirmOrder = new ConfirmOrder(customerEmail, itemQuantity, itemPrice, orderID, totalItemQuantity);

                                    //save order once confirmed into customerOrderRef
                                    confirmOrderRef.set(confirmOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(Cart.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    clear();
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
                alert.setTitle("Confirm Order");
                alert.show();
            }
        });
    }

    //clear recyclerview and data once order is placed
    public void clear() {

        orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String documentData = "";
                        String documentID = document.getId();
                        documentData+= documentID;

                        orderRef.document(documentData).delete();
                        Intent intent = new Intent(Cart.this, Cart.class);
                        startActivity(intent);
                    }
                } else {
                    return;
                }
            }
        });
    }

    //get the price of the ordered items
    private void getPriceData() {
        orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String totalItemQuantity = document.getString("quantity");
                        totalItem = Integer.parseInt(totalItemQuantity);
                        quantityTotal += totalItem;

                        String itemPrice = document.getString("orderPrice");
                        itemTotal = Double.parseDouble(itemPrice);
                        totalCost += itemTotal;
                        total.setText(String.valueOf(String.format("%.2f", totalCost)));

                        shipping.setText(String.valueOf(String.format("%.2f", shippingCost)));

                        subtotalCost = totalCost+shippingCost;

                        subtotal.setText(String.valueOf(String.format("%.2f", subtotalCost)));

                    }
                } else {
                        return;
                }
            }
        });
    }

    //set the recyclerview of orderRef data
    private void setUpRecyclerView() {
        Query query = orderRef.orderBy("orderPrice", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Order> options = new FirestoreRecyclerOptions.Builder<Order>().setQuery(query, Order.class).build();

        adapter = new OrderAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView4);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new WrapContent(this, LinearLayoutManager.VERTICAL, false));
        Spacing itemDecorator = new Spacing(25);
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //itemtouch helper to delete the data on swipe
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder build;
                build = new AlertDialog.Builder(viewHolder.itemView.getContext());

                build.setMessage("Are you sure you want to delete this order?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                adapter.deleteProduct(viewHolder.getAbsoluteAdapterPosition());
                                Intent intent = new Intent(Cart.this, Cart.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                adapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                            }
                        });
                AlertDialog alert = build.create();
                alert.setTitle("Delete Order Confirmation");
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

    //Navigation menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.editAcc:

                Intent intent = new Intent(Cart.this, EditAccount.class);
                startActivity(intent);

                break;

            case R.id.delAcc:

                //Dialog to confirm account deletion
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
                                            Toast.makeText(Cart.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Cart.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(Cart.this, "Account deletion unsuccessful", Toast.LENGTH_SHORT).show();
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
                Intent intent1 = new Intent(Cart.this, ProductPage.class);
                startActivity(intent1);
                break;

            case R.id.cart:
                Intent intent2 = new Intent(Cart.this, Cart.class);
                startActivity(intent2);
                break;

            case R.id.orderHistory:
                Intent intent3 = new Intent(Cart.this, OrderHistory.class);
                startActivity(intent3);
                break;

            case R.id.trackDelivery:
                Intent intent4 = new Intent(Cart.this, OrderProducts.class);
                startActivity(intent4);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent it = new Intent(Cart.this, MainActivity.class);
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