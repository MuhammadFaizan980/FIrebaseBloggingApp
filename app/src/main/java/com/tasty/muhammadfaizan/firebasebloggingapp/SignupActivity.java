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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText txtEmail;
    EditText txtPass;
    EditText txtConfirmPass;
    Button btnSignup;
    Button btnGoBack;
    ProgressBar pBarS;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        initViews();
        sendUserToFB();
    }

    private void sendUserToFB() {
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pBarS.setVisibility(View.VISIBLE);
                String email = txtEmail.getText().toString().trim();
                final String pass = txtPass.getText().toString().trim();
                String rePass = txtConfirmPass.getText().toString().trim();

                if (email.equals("") || pass.equals("") || rePass.equals("")) {
                    pBarS.setVisibility(View.INVISIBLE);
                    if (email.equals("")) {
                        txtEmail.requestFocus();
                        txtEmail.setError("Cannot be empty");
                    } else if (pass.equals("")) {
                        txtPass.requestFocus();
                        txtPass.setError("Cannot be empty");
                    } else {
                        txtConfirmPass.requestFocus();
                        txtConfirmPass.setError("Cannot be empty");
                    }
                } else {

                    if (!pass.equals(rePass)) {
                        pBarS.setVisibility(View.INVISIBLE);
                        Toast.makeText(SignupActivity.this, "Password fields do not match", Toast.LENGTH_LONG).show();
                    } else {
                        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Password").setValue(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                pBarS.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                                    startActivity(new Intent(SignupActivity.this, UserProfile.class));
                                    SignupActivity.this.finish();
                                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    pBarS.setVisibility(View.INVISIBLE);
                                    Toast.makeText(SignupActivity.this, "User already exists", Toast.LENGTH_LONG).show();
                                } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                    pBarS.setVisibility(View.INVISIBLE);
                                    Toast.makeText(SignupActivity.this, "Password must contain at least 6 characters", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                SignupActivity.this.finish();
            }
        });
    }

    private void initViews() {
        txtEmail = findViewById(R.id.signupName);
        txtPass = findViewById(R.id.signupPass);
        txtConfirmPass = findViewById(R.id.confirmPass);
        pBarS = findViewById(R.id.pBarS);
        btnSignup = findViewById(R.id.btnSignup);
        btnGoBack = findViewById(R.id.goBack);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        pBarS.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        SignupActivity.this.finish();
    }
}
