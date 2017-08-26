package com.makererequickresponse.mqr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText et_Email, et_Pass;
    private Button LoginButton;
    private TextView Link;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make hii kitu fullscreen hivi,
        //si hatutaki kucheki battery pale juu? eeh!
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        et_Email = (EditText)findViewById(R.id.etEmail);
        et_Pass = (EditText)findViewById(R.id.etPassword);
        LoginButton = (Button)findViewById(R.id.loginbutton);
        Link = (TextView)findViewById(R.id.joinQR);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //if user is already logged in...
            //start Maps activity here
            finish();
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        }

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });


        Link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterIntro.class);
                finish();
                LoginActivity.this.startActivity(intent);
            }
        });
    }

    private void userLogin(){
        String email = et_Email.getText().toString().trim();
        String password = et_Pass.getText().toString().trim();

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
        progressDialog.setMessage("Logging In... Please wait.");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //check completion if task is successful
                        if(task.isSuccessful()){
                            //start Maps activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        }else{
                            toastMessage("Incorrect Email or Password");
                        }
                    }
                });
    }

    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
