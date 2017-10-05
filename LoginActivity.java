package com.me.you.youandme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    Button btSignIn;
    TextView btSignUp;
    EditText edEmail, edPassword;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeObject();
        handleEvent();

    }

    private void initializeObject() {
        btSignIn = (Button) findViewById(R.id.btSignIn);
        btSignUp = (TextView) findViewById(R.id.btSignUp);
        edEmail = (EditText) findViewById(R.id.edEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null) {
                    //login successful
//                    Log.d("successful",  "onAuthStateChanged:signed_in:" + user.getUid());

//                    finish();
                }
            }
        };
        database = FirebaseDatabase.getInstance();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            edEmail.setText(extras.getString("email"));
            edPassword.setText(extras.getString("password"));
        }
    }

    private void handleEvent() {

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                if(validateForm(email, password)) {
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (!task.isSuccessful()) {
                                Log.w("Sign in fail", "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, "The email or password you entered is invalid", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Log.d("Sign in", "signInWithEmail:onComplete:" + task.isSuccessful());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
    }

    private boolean validateForm(String email, String password) {
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You must enter both email and password fields", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the previous Activity
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            String email = data.getStringExtra("email");
            String password = data.getStringExtra("password");
            edEmail.setText(email);
            edPassword.setText(password);
        }
    }
}
