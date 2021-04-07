package com.fabcuts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView tv_registerUser,tvforgot;
    private EditText et_phone, et_password,loginname;
    private Button loginButton;
    private String phone, pass,decrypt;
    private ProgressDialog pd;
    private DatabaseReference mref;
    static FirebaseAuth auth;
    private String phoneText,passwordText,loginnam;
    Intent newActivity;
    AlertDialog alertDialog;
    Boolean saloncheck=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login);

        saloncheck=getIntent().getBooleanExtra("salon",false);
        //Toast.makeText(this, saloncheck+"", Toast.LENGTH_SHORT).show();

        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        FirebaseApp.initializeApp(this);
        Firebase.setAndroidContext(this);
        auth=FirebaseAuth.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        et_phone=findViewById(R.id.phone);
        et_password=findViewById(R.id.password);
        loginButton=findViewById(R.id.loginButton);
        tv_registerUser=findViewById(R.id.register);
        loginname=findViewById(R.id.loginname);
        tvforgot=findViewById(R.id.tvforgot);
        pd = new ProgressDialog(LoginActivity.this);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        tv_registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();


            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });

        tvforgot.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                alertDialog = alertDialogBuilder.create();
                alertDialogBuilder.setTitle("Forgot Password!");
                alertDialogBuilder.setMessage("There was an error while initializing OTP.\nYou can register again with a new password now.");
                alertDialog.create();
                alertDialogBuilder.setPositiveButton("Register Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                    }
                });

                alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog=alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (auth.getCurrentUser()!=null&&sharedPreferences.getString("phone",null)!=null && sharedPreferences.getString("password",null)!=null){
            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
            loadData();

            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.apply();

        }
        loadData();

    }
    private void saveData() {

        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("phone",et_phone.getText().toString());
        editor.putString("password",et_password.getText().toString());
        editor.putString("name",loginname.getText().toString());
        if (saloncheck==false)
        editor.putBoolean("user",true);
        else
            editor.putBoolean("saloon",true);

        editor.apply();

    }

    public void loadData(){//to fetch user data when required
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        phoneText=sharedPreferences.getString("phone","");
        passwordText=sharedPreferences.getString("password","");
        loginnam=sharedPreferences.getString("name","");

        et_phone.setText(phoneText);
        loginname.setText(loginnam);
    }

    private void closeKeyboard() {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(loginButton.getWindowToken(), 0);

    }
    private void signIn() {
        pd.setMessage("Logging In...");

        phone=et_phone.getText().toString();
        pass = et_password.getText().toString();
        loginnam=loginname.getText().toString();
        if (!saloncheck)
        mref= FirebaseDatabase.getInstance().getReference().child("users").child(phone);
        else
            mref= FirebaseDatabase.getInstance().getReference().child("saloons").child(phone);

        if (phone.isEmpty() || pass.isEmpty()|| loginnam.isEmpty()) {
            Toast.makeText(this, "Field blank", Toast.LENGTH_SHORT).show();
        } else {
            closeKeyboard();
            pd.show();
            mref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {


                        String passkey = Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString();
                        if (!saloncheck)
                        {


                        try {
                            decrypt = EncryptDecrypt.decrypt(passkey);
                            if (pass.equals(decrypt)) {
                                saveData();
                                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("login", true);
                                editor.apply();
                                //Toast.makeText(LoginActivity.this, "User login success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,FirstActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "check credentials", Toast.LENGTH_SHORT).show();
                                pd.cancel();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                            if (passkey.equals(pass)){
                                //Toast.makeText(LoginActivity.this, "Salon login success", Toast.LENGTH_SHORT).show();
                                saveData();
                                Intent intent = new Intent(LoginActivity.this,SaloonSetUp.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                pd.cancel();
                                Toast.makeText(LoginActivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"No such User",Toast.LENGTH_SHORT).show();
                        et_phone.setText("");
                        et_password.setText("");
                        pd.cancel();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(LoginActivity.this,"error",Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
