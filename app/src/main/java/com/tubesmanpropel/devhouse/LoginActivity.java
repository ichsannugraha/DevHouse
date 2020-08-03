package com.tubesmanpropel.devhouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;
import com.tubesmanpropel.devhouse.Model.Users;
import com.tubesmanpropel.devhouse.Model.Sellers;
import com.tubesmanpropel.devhouse.Prevalent.Prevalent;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button mSignInBtn, mSignUpBtn;
    private EditText mUserPhone, mUserPassword;
    private TextView mLupaPassword;
    private ProgressDialog loadingBar;
    private CheckBox mSellerCheckBox;

    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignInBtn = (Button) findViewById(R.id.signInBtn);
        mSignUpBtn = (Button) findViewById(R.id.signUpBtn);
        mUserPhone = (EditText) findViewById(R.id.userPhone);
        mUserPassword = (EditText) findViewById(R.id.userPassword);
        mLupaPassword = (TextView) findViewById(R.id.lupaPasswordTxt);
        mSellerCheckBox = (CheckBox) findViewById(R.id.sellerCheckBox);

        loadingBar = new ProgressDialog(this);

        Paper.init(this);


        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });


        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey != "")
        {
            if (!TextUtils.isEmpty(UserPhoneKey)  &&  !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserPhoneKey, UserPasswordKey);

                //loadingBar.setTitle("Berhasil login!");
                //loadingBar.setMessage("Mohon tunggu.....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }



    private void LoginUser() {

        String phone = mUserPhone.getText().toString();
        String password = mUserPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Masukkan no hp anda!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Masukkan password anda!", Toast.LENGTH_SHORT).show();
        }
        else {
            //loadingBar.setTitle("Sedang Login");
            //loadingBar.setMessage("Mohon tunggu...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);
        }
    }


    private void AllowAccessToAccount(final String phone, final String password) {

        if (!mSellerCheckBox.isChecked()) {
            parentDbName = "Users";
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }
        else {
            parentDbName = "Sellers";
            Paper.book().write(Prevalent.SellerPhoneKey, phone);
            Paper.book().write(Prevalent.SellerPasswordKey, password);
        }


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    Sellers sellersData = dataSnapshot.child(parentDbName).child(phone).getValue(Sellers.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Sellers"))
                            {
                                Toast.makeText(LoginActivity.this, "Berhasil login sebagai seller!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, SellerMainActivity.class);
                                Prevalent.currentOnlineSeller = sellersData;
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "Berhasil login sebagai user!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password anda salah!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Akun dengan no hp " + phone + " tidak valid!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void AllowAccess(final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("Users").child(phone).exists())
                {
                    Users usersData = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            //Toast.makeText(LoginActivity.this, "Selamat datang!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent);
                        }
                        else
                        {
                            loadingBar.dismiss();
                            //Toast.makeText(LoginActivity.this, "Password anda salah!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    //Toast.makeText(LoginActivity.this, "Akun dengan no hp " + phone + " tidak valid!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }
}