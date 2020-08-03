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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private Button mSignUpBtn;
    private EditText mUsernameTxt, mPasswordTxt, mEmailTxt, mPhoneTxt;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mSignUpBtn = (Button) findViewById(R.id.signUp);
        mUsernameTxt = (EditText) findViewById(R.id.usernameTxt);
        mPasswordTxt = (EditText) findViewById(R.id.passwordTxt);
        mEmailTxt = (EditText) findViewById(R.id.emailTxt);
        mPhoneTxt = (EditText) findViewById(R.id.phoneTxt);
        loadingBar = new ProgressDialog(this);


        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }


    private void CreateAccount() {
        String username = mUsernameTxt.getText().toString();
        String password = mPasswordTxt.getText().toString();
        String email = mEmailTxt.getText().toString();
        String phone = mPhoneTxt.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Masukkan username anda!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Masukkan password anda!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Masukkan email anda!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Masukkan no hp anda!", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Membuat Akun");
            loadingBar.setMessage("Mohon Tunggu Sebentar.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(username, password, email, phone);
        }
    }


    private void ValidatePhoneNumber(final String username, final String password, final String email, final String phone) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phone).exists())) {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("email", email);
                    userdataMap.put("password", password);
                    userdataMap.put("username", username);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                                        startActivity(i);
                                    }
                                    else {
                                        loadingBar.dismiss();
                                        Toast.makeText(SignUpActivity.this, "Terjadi kesalahan, silakan coba kembali!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "Nomor HP " + phone + " sudah terpakai!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(SignUpActivity.this, "Silakan gunakan nomor yang lain!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
