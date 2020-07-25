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
import com.tubesmanpropel.devhouse.Model.Users;
import com.tubesmanpropel.devhouse.Prevalent.Prevalent;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button mSignInBtn, mSignUpBtn;
    private EditText mUserPhone, mUserPassword;
    private TextView mUserLinkTxt, mSellerLinkTxt;
    private ProgressDialog loadingBar;

    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignInBtn = (Button) findViewById(R.id.signInBtn);
        mSignUpBtn = (Button) findViewById(R.id.signUpBtn);
        mUserPhone = (EditText) findViewById(R.id.userPhone);
        mUserPassword = (EditText) findViewById(R.id.userPassword);
        mUserLinkTxt = (TextView) findViewById(R.id.userLinkTxt);
        mSellerLinkTxt = (TextView) findViewById(R.id.sellerLinkTxt);

        loadingBar = new ProgressDialog(this);

        Paper.init(this);


        mSellerLinkTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignInBtn.setText("Sign In Seller");
                mSellerLinkTxt.setVisibility(View.INVISIBLE);
                mUserLinkTxt.setVisibility(View.VISIBLE);
                parentDbName = "Sellers";
            }
        });

        mUserLinkTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignInBtn.setText("Sign In");
                mSellerLinkTxt.setVisibility(View.VISIBLE);
                mUserLinkTxt.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });


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
            loadingBar.setTitle("Sedang Login");
            loadingBar.setMessage("Mohon tunggu...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);
        }
    }


    private void AllowAccessToAccount(final String phone, final String password) {

        Paper.book().write(Prevalent.UserPhoneKey, phone);
        Paper.book().write(Prevalent.UserPasswordKey, password);

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Sellers"))
                            {
                                Toast.makeText(LoginActivity.this, "Berhasil login sebagai seller!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, SellerMainActivity.class);
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


}