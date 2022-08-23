package com.example.customer_fyp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class OrderItem extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MODEL = "com.example.customer_fyp.MODEL";
    public static final String BRAND = "com.example.customer_fyp.BRAND";
    public static final String STOCK = "com.example.customer_fyp.STOCK";
    public static final String PRICE = "com.example.customer_fyp.PRICE";
    public static final String IMG = "com.example.customer_fyp.IMG";
    public static final String DISPOSITION = "com.example.customer_fyp.DISPOSITION";

    public static final String ID = "com.example.customer_fyp.ID";

    private DrawerLayout drawer;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;

    private Uri mImageUri;
    private Uri url;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private CollectionReference orderRef = db.collection("database/customer/order");
    private DocumentReference productRef = db.collection("database/customer/order").document();
    private CollectionReference historyRef = db.collection("database/customer/history");
    private  CollectionReference file = db.collection("database/admin/fileImage");
    private CollectionReference idRef = db.collection("database/customer/productID");

    private ImageView imgView;
    private TextView mModel;
    private TextView mBrand;
    private TextView mStock;
    private TextView mPrice;
    private TextView mQuantity;
    private ImageView mIncrease;
    private ImageView mDecrease;
    private Button mButton;

    public int quantity = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        imgView = findViewById(R.id.orderImage);
        mModel = findViewById(R.id.orderModel);
        mBrand = findViewById(R.id.orderBrand);
        mStock = findViewById(R.id.orderStock);
        mPrice = findViewById(R.id.orderPrice);
        mIncrease = findViewById(R.id.increase);
        mDecrease = findViewById(R.id.decrease);
        mQuantity = findViewById(R.id.quantity);
        mButton = findViewById(R.id.orderButton);

        //Authenticate firebase
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("orders");

        //Set support for toolbar
        toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        //Set navigation view for selected item
        drawer = findViewById(R.id.draw_layout4);
        NavigationView navigationView = findViewById(R.id.nav4);
        navigationView.setNavigationItemSelectedListener(this);

        //Set Toolbar's navigation click listener to toggle the drawer when it is clicked
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Set text for navigation view for customer email
        NavigationView viewNavigation = (NavigationView) findViewById(R.id.nav4);
        View headerView = viewNavigation.getHeaderView(0);
        TextView navAdmin = (TextView) headerView.findViewById(R.id.username_admin);
        String customer = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        navAdmin.setText(customer);

        Intent intent = getIntent();

        mModel.setText(intent.getStringExtra(MODEL));
        mBrand.setText(intent.getStringExtra(BRAND));
        mStock.setText(intent.getStringExtra(STOCK));
        mPrice.setText(intent.getStringExtra(PRICE));

        Glide.with(OrderItem.this).load(Uri.parse(intent.getStringExtra(IMG))).into(imgView);

        int stock = Integer.parseInt(intent.getStringExtra(STOCK));

        if (stock == 0) {
            mButton.setEnabled(false);
        }

        mIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increment();
            }
        });

        mDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrement();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               uploadFile();
            }
        });
    }

    public void increment() {
        Intent intent = getIntent();
        int value = Integer.parseInt(intent.getStringExtra(STOCK));

        quantity++;
        if(quantity > value) {
            Toast.makeText(this, "Order exceeds stock of item!", Toast.LENGTH_SHORT).show();
            quantity = Integer.parseInt(intent.getStringExtra(STOCK));
        }
        mQuantity.setText(String.valueOf(quantity));
    }

    public void decrement() {
        quantity--;
        if(quantity<=0) {
            quantity = 1;
        }
        mQuantity.setText(String.valueOf(quantity));
    }

    public void uploadFile() {

        Intent intent = getIntent();

        String customerEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String id = productRef.getId();
        String model = intent.getStringExtra(MODEL);
        String brand = intent.getStringExtra(BRAND);
        String priceOrder = intent.getStringExtra(PRICE);
        double orderPrice = Double.parseDouble(priceOrder);
        Uri imageOrder = Uri.parse(intent.getStringExtra(IMG));
        String image = String.valueOf(imageOrder);

         double itemPrice = orderPrice*quantity;

         int stock = Integer.parseInt(intent.getStringExtra(STOCK));
         stock -= quantity;

        String price = String.valueOf(String.format("%.2f", itemPrice));

        double priceItem = Double.parseDouble(intent.getStringExtra(PRICE));

        String quantityOrder = String.valueOf(quantity);

        Order order = new Order(id,model,brand,price,quantityOrder,image, customerEmail);

        History history = new History(id,model,brand,quantityOrder,price,image, customerEmail);

        productID productID = new productID(model, brand, stock, priceItem, image);

        file.document(intent.getStringExtra(ID)).set(productID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

        historyRef.add(history).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

            }
        });

        productRef.set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(OrderItem.this, ProductPage.class);
                startActivity(intent);
                Toast.makeText(OrderItem.this, "Product sent to cart!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderItem.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.editAcc:

                Intent intent = new Intent( OrderItem.this, EditAccount.class);
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
                                            Toast.makeText(OrderItem.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(OrderItem.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(OrderItem.this, "Account deletion unsuccessful", Toast.LENGTH_SHORT).show();
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
                Intent intent1 = new Intent(OrderItem.this, ProductPage.class);
                startActivity(intent1);
                break;

            case R.id.cart:
                Intent intent2 = new Intent(OrderItem.this, Cart.class);
                startActivity(intent2);
                break;

            case R.id.orderHistory:
                Intent intent3 = new Intent(OrderItem.this, OrderHistory.class);
                startActivity(intent3);
                break;

            case R.id.trackDelivery:
                Intent intent4 = new Intent(OrderItem.this, OrderProducts.class);
                startActivity(intent4);
                break;

            case R.id.logout:

                FirebaseAuth.getInstance().signOut();
                Intent it = new Intent(OrderItem.this, MainActivity.class);
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
