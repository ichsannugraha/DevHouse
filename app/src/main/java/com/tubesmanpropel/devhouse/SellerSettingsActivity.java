package com.tubesmanpropel.devhouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.paperdb.Paper;


public class SellerSettingsActivity extends AppCompatActivity {

    CardView mTambahProduk, mEditProfile, mLogoutProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_settings);

        mTambahProduk = (CardView) findViewById(R.id.tambahProdukCV);
        mEditProfile = (CardView) findViewById(R.id.editProfileCV);
        mLogoutProfile = (CardView) findViewById(R.id.logoutProfileCV);

        mTambahProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellerSettingsActivity.this, SellerTambahProdukActivity.class);
                startActivity(i);
            }
        });

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellerSettingsActivity.this, SellerEditProfileActivity.class);
                startActivity(i);
            }
        });

        mLogoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();

                Intent i = new Intent(SellerSettingsActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(SellerSettingsActivity.this, SellerMainActivity.class);
        startActivity(i);
    }
}
