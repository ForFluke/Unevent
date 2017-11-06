package com.example.tawin.testmapgps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginRe extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSingIn;
    private EditText editTextloginEmail;
    private EditText editTextloginPass;
    private TextView textViewSingin;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_re);

        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),Main2Activity.class));


        }
        editTextloginEmail = (EditText) findViewById(R.id.editloginUser);
        editTextloginPass = (EditText) findViewById(R.id.editloginPass);
        buttonSingIn = (Button) findViewById(R.id.loginBut);
        textViewSingin = (TextView) findViewById(R.id.textlogin);

        progressDialog = new ProgressDialog(this);
        buttonSingIn.setOnClickListener(this);
        textViewSingin.setOnClickListener(this);
    }

    private void userLogin(){
        String email = editTextloginEmail.getText().toString().trim();
        String password = editTextloginPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)){

            Toast.makeText(this,"Please Enter Email",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Login User ....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(),Main2Activity.class));
                        }
                    }
                });
    }
    @Override
    public void onClick(View view) {


        if (view ==buttonSingIn ){
            userLogin();

        }
        if (view == textViewSingin){
            finish();
           // startActivity(new Intent(this,LoginRe.class));
        }

    }
}
