package com.fabcuts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SaloonRegistration extends AppCompatActivity {

    private EditText et_username, et_password,et_phone,et_saloon,etcity;
    private Button registerButton;
    private String user, pass,phone,city,encrypt;
    int secretcode=808080;
    int usersecret=0;
    DatabaseReference mref;
    TextView tvexistingsalon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_saloon_registration);

        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toast.makeText(this, "Please turn on your GPS", Toast.LENGTH_SHORT).show();

        et_phone=findViewById(R.id.phone);
        et_username =  findViewById(R.id.username);
        et_password =  findViewById(R.id.password);
        et_saloon=findViewById(R.id.secret);
        etcity=findViewById(R.id.etcity);
        registerButton =  findViewById(R.id.registerButton);
        tvexistingsalon=findViewById(R.id.tvexistingsalon);
        tvexistingsalon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class)
                .putExtra("salon",true));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();

                user=et_username.getText().toString();
                pass=et_password.getText().toString();
                phone=et_phone.getText().toString();
                city=etcity.getText().toString();

                usersecret= Integer.parseInt(et_saloon.getText().toString());
                if (usersecret==secretcode){
                    if (user.equals("")) {
                        et_username.setError("can't be blank");
                    } else if (pass.equals("")) {
                        et_password.setError("can't be blank");
                    }
                    else if (user.length() < 2) {
                        et_username.setError("Wrong UserName suspected");
                    }
                    else if (pass.length() < 5) {
                        et_password.setError("at least 5 characters long");
                    }
                    else if(phone.length()<10||phone.isEmpty()){
                        et_phone.setError("Enter valid mobile number");
                    }
                    else if(city.equals("")){
                        et_phone.setError("Enter city");
                    }
                    else {

                        if (isNetworkAvailable(SaloonRegistration.this)){
                            Intent i=new Intent(SaloonRegistration.this,UserLocation.class);
                            i.putExtra("phone",phone);
                            i.putExtra("username",user);
                            i.putExtra("password",pass);
                            i.putExtra("city",city);
                            saveData();
                            startActivity(i);

                            //registerUser(pass);
                        }
                        else
                            Toast.makeText(SaloonRegistration.this,"network error",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(SaloonRegistration.this,"Wrong secret",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void registerUser(String s){

        mref= FirebaseDatabase.getInstance().getReference().child("saloons").child(phone);

        try {
            encrypt= EncryptDecrypt.encrypt(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mref.child("password").setValue(encrypt);
        mref.child("Name").setValue(user);
        mref.child("mobile").setValue(phone);
        mref.child("city").setValue(city);

    }

    private void saveData() {

        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("name",user);
        editor.putString("phone",et_phone.getText().toString());
        editor.putString("password",et_password.getText().toString());

        editor.apply();

    }
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(registerButton.getWindowToken(), 0);

    }
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo()!=null
                &&connectivityManager.getActiveNetworkInfo().isConnected();
    }



}
