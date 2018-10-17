package com.example.mugandaimo.crosstown;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Context context;
    private FirebaseAuth mAuth;
    private TextInputLayout et_username;
    private TextInputLayout et_email;
    private TextInputLayout et_password;
    private Button create;
    private Toolbar toolbar;
    private ProgressDialog registrationProgress;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;
        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        registrationProgress = new ProgressDialog(context);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_username = (TextInputLayout) findViewById(R.id.username);
        et_email = (TextInputLayout) findViewById(R.id.email);
        et_password = (TextInputLayout) findViewById(R.id.password);
        create = (Button) findViewById(R.id.create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getEditText().getText().toString();
                String email = et_email.getEditText().getText().toString().trim();
                String password = et_password.getEditText().getText() .toString();

                if(!TextUtils.isEmpty(username) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    registrationProgress.setTitle("Creating User");
                    registrationProgress.setMessage("Please wait...");
                    registrationProgress.setCanceledOnTouchOutside(false);
                    registrationProgress.show();
                    registerUser(username,email,password);
                }
            }
        });
    }

    private void registerUser(final String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUser.getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name",username);
                    userMap.put("status","Hi. I'm using Lapit Chat App");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");
                    userMap.put("device_token",deviceToken);

                    db.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                registrationProgress.dismiss();
                                Intent mainIntent = new Intent(context,MainActivity.class)  ;
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });
                } else{
                    registrationProgress.hide();
                    Toast.makeText(context,"An error occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
