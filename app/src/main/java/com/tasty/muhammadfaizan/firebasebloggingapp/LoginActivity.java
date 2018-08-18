package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText txtName;
    EditText txtPass;
    Button btnLogin;
    Button btnLoginSignup;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing Views
        initViews();

        //Signing in the user
        takeUserToFB();

        //take user to sign up activity
        goToSignUp();
    }

    private void takeUserToFB() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pBar.setVisibility(View.VISIBLE);
                String email = txtName.getText().toString();
                String pass = txtPass.getText().toString();

                if (email.equals("") || pass.equals("")) {
                    pBar.setVisibility(View.INVISIBLE);
                    if (email.equals("")) {
                        txtName.setError("Please enter a username");
                    } else {
                        txtPass.setError("Please Enter a password");
                    }
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                pBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                LoginActivity.this.finish();
                            }
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                pBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                pBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, "User with these credentials does not exist, consider signing up first", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        pBar.setVisibility(View.GONE);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            this.finish();
        }
    }

    private void initViews() {
        firebaseAuth = FirebaseAuth.getInstance();
        txtName = findViewById(R.id.txtName);
        txtPass = findViewById(R.id.txtPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginSignup = findViewById(R.id.btnLoginSignup);
        pBar = findViewById(R.id.pBar);
    }

    private void goToSignUp() {
        btnLoginSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                LoginActivity.this.finish();
            }
        });
    }
}
