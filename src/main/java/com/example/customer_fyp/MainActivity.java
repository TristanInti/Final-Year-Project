package com.example.customer_fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import kotlin.contracts.Returns;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private Button mButton;
    EditText etEmailAddress, etPassword;

    String emailText;
    String passwordText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference customerRef = db.collection("database").document("customer").collection("account").document();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Returns an instance of this class corresponding to the default FirebaseApp instance
        mAuth =FirebaseAuth.getInstance();

        etEmailAddress = (EditText) findViewById(R.id.etEmailAddress);
        etPassword = (EditText) findViewById(R.id.etPassword);
        mButton = findViewById(R.id.btnLogin);
        TextView btn = findViewById(R.id.tvSignUp);

         emailText = etEmailAddress.getText().toString();
         passwordText = etPassword.getText().toString();

        //Button to sign in into account
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    signIn();
            }
        });

        //Button to register account
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });
    }

    private void signIn() {

        emailText = etEmailAddress.getText().toString();
        passwordText = etPassword.getText().toString();

        //Sign in with email and password using FirebaseAuth
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(MainActivity.this,"Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent intet = new Intent(MainActivity.this, ProductPage.class);
                            startActivity(intet);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed. Wrong email/password",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            return;
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

    }
}