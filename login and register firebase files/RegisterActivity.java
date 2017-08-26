package com.makererequickresponse.mqr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    // private String name, email, student_no, password, confirmpass, course, year;
    //private EditText et_Name, et_Email, et_StdNo, et_Pass, et_ConfirmPass, et_Course, et_Year;
    //user interface stuff
    private EditText et_Email, et_Pass;
    private TextView Link;
    private Button buttonRegister;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    Registration deviceRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //if the device is registered
        deviceRegistration = new Registration(this);
        if(deviceRegistration.isRegistered()){
            startService(new Intent(this, NotificationListener.class));
        }


        et_Email = (EditText) findViewById(R.id.etEmail);
        et_Pass = (EditText) findViewById(R.id.etPass);
        Link = (TextView)findViewById(R.id.loginredir);
        buttonRegister = (Button)findViewById(R.id.registerbutton);
        progressDialog = new ProgressDialog(this);


        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //if user is already logged in...
            //start Maps activity here
            finish();
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        }

        Link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(intent);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }
    public void registerUser(){
        final String email = et_Email.getText().toString().trim();
        String password = et_Pass.getText().toString().trim();

        //checking if fields are empty
        if(TextUtils.isEmpty(email)){
            //email is empty
            toastMessage("Please enter valid email");
            //stop function execution
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password is empty
            toastMessage("Please input Password");
            //stop it again
            return;
        }
        //if validations are okay...
        //lets first show a progress bar mdagi mdagi...:-)
        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        //create user with firebase
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            toastMessage("Registered Successfully!");
                            //user successfully registered and logged in
                            //we will start the profile activity


                            if (!deviceRegistration.isRegistered()) {
                                //registering the device
                                deviceRegistration.setEmail(email);
                                deviceRegistration.registerDevice();

                                if(deviceRegistration.getRegistrationStatus() == true) {
                                    Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Choose a different email", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //if the device is already registered
                                //displaying a toast
                                Toast.makeText(RegisterActivity.this, "Already registered...", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        }else{
                            toastMessage("ERROR: Failed to register...Please try again.");
                        }
                    }
                });


    }
    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}