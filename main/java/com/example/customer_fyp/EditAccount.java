package com.example.customer_fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditAccount extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    EditText etEmailAddress, etPassword, etConfirmPassword;
    Button btnEditEmail, btnEditPassword,btnLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editaccount);

        etEmailAddress= (EditText) findViewById(R.id.etEmailAddress);
        etPassword= (EditText) findViewById(R.id.etPassword);
        etConfirmPassword= (EditText) findViewById(R.id.etConfirmPassword);
        btnEditEmail = (Button) findViewById(R.id.btnEditPassword);
        btnEditPassword = (Button) findViewById(R.id.btnEditPassword);
        TextView btn = findViewById(R.id.alreadyHaveAccount);

        //for firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //Button to edit password
        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser admin = FirebaseAuth.getInstance().getCurrentUser();

                String editEmail = etEmailAddress.getText().toString();
                String editPassword = etPassword.getText().toString();
                String editConfirmPassword = etConfirmPassword.getText().toString();

                //update email
                admin.updateEmail(editEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            String editPassword = etPassword.getText().toString();
                            String editConfirmPassword = etConfirmPassword.getText().toString();

                            FirebaseUser admin = FirebaseAuth.getInstance().getCurrentUser();

                            //update password
                            admin.updatePassword(editPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Customer email and password updated");
                                        Toast.makeText(EditAccount.this, "Customer email and password updated", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(EditAccount.this, ProductPage.class);
                                        startActivity(intent);
                                    } else {
                                        Log.w(TAG,"Update failed. Please check email/password");
                                        Toast.makeText(EditAccount.this, "Update failed. Please check email/password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            return;
                        }
                    }
                });
            }
        });


    }
}
