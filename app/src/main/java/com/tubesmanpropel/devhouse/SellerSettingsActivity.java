package com.tubesmanpropel.devhouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class SellerSettingsActivity extends AppCompatActivity {

    CardView mTambahProduk, mEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_settings);

        //getSupportActionBar().setTitle("Pengaturan");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTambahProduk = (CardView) findViewById(R.id.tambahProdukCV);
        mEditProfile = (CardView) findViewById(R.id.editProfileCV);

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
    }
}
