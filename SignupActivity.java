package com.me.you.youandme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {


    EditText edEmail, edPassword, edName;
    Button btSignUp, btCancel;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edEmail = (EditText) findViewById(R.id.edSignupEmail);
        edPassword = (EditText) findViewById(R.id.edSignupPassword);
        edName = (EditText) findViewById(R.id.edSignupName);
        btSignUp = (Button) findViewById(R.id.btSUSignUp);
        btCancel = (Button) findViewById(R.id.btSUCancel);
        firebaseAuth = FirebaseAuth.getInstance();

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = edEmail.getText().toString().trim();
                final String password = edPassword.getText().toString().trim();
                final String name = edName.getText().toString().trim();
                if (validate(email, password, name)) {
                    final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Sign up successful", Toast.LENGTH_LONG).show();
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                            intent.putExtra("email", email);
                                            intent.putExtra("password", password);
                                            startActivityForResult(intent, 1);
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Sign up fail", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validate(String email, String password, String name) {
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Enter valid email address");
            return false;
        }
        if(password.length() < 6) {
            edPassword.setError("Password must be 6 characters or more");
            return false;
        }
        if(name.length() < 3) {
            edName.setError("Name must be 3 characters or more");
            return false;
        }
        return true;
    }

}
