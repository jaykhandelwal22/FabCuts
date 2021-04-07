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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_username, et_password,et_phone;
    private Button registerButton;
    private String user, pass,phone;
    private TextView login;
    RadioGroup rgsex;
    RadioButton rbmale,rbfemale;
    int selectedId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_register);

        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        et_phone=findViewById(R.id.phone);
        et_username =  findViewById(R.id.username);
        et_password =  findViewById(R.id.password);
        rgsex=findViewById(R.id.rgsex);
        rbmale=findViewById(R.id.rbmale);
        rbfemale=findViewById(R.id.rbfemale);

        registerButton =  findViewById(R.id.registerButton);
        login =  findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, SaloonRegistration.class));
                finish();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                user=et_username.getText().toString();
                pass=et_password.getText().toString();
                phone=et_phone.getText().toString();
                if (user.equals("")) {
                    et_username.setError("can't be blank");
                } else if (pass.equals("")) {
                    et_password.setError("can't be blank");
                }
                else if (user.length() < 5) {
                    et_username.setError("at least 5 characters long");
                }
                else if (pass.length() < 5) {
                    et_password.setError("at least 5 characters long");
                }
                else if(phone.length()<10||phone.isEmpty()){
                    et_phone.setError("Enter valid mobile number");
                }
                else if(selectedId==0){
                    Toast.makeText(RegisterActivity.this, "Please select your Gender", Toast.LENGTH_SHORT).show();
                }
                else {

                    if (isNetworkAvailable(RegisterActivity.this)){
                        Intent i=new Intent(RegisterActivity.this,Phone_Auth.class);
                        i.putExtra("phone",phone);
                        i.putExtra("username",user);
                        i.putExtra("password",pass);
                        i.putExtra("gender",selectedId);
                        saveData();
                        startActivity(i);
                        finish();
                    }
                    else
                        Toast.makeText(RegisterActivity.this,"network error",Toast.LENGTH_SHORT).show();
                }
            }
        });

        rgsex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch(checkedId){

                    case R.id.rbmale:
                        selectedId=1;
                        //Toast.makeText(UploadPicture.this, ""+selectedId, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rbfemale:
                        selectedId=2;
                        // Toast.makeText(UploadPicture.this, ""+selectedId, Toast.LENGTH_SHORT).show();
                        break;
                        //Toast.makeText(UploadPicture.this, ""+selectedId, Toast.LENGTH_SHORT).show();

                }
            }
        });
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

    public void tnc(View view) {
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(RegisterActivity.this);
        alertdialog.setTitle("Terms and Conditions");
        alertdialog.setMessage("message");
        alertdialog.setNeutralButton("     Okay I understand that", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final TextView etaddress = new TextView(RegisterActivity.this);

        alertdialog.show();
    }

}
