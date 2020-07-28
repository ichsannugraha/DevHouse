package com.tubesmanpropel.devhouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tubesmanpropel.devhouse.Model.Products;
import com.tubesmanpropel.devhouse.Model.Sellers;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView mNamaSeller, mNamaProduk, mHargaProduk, mDeskripsiProduk;
    private ImageView mGambarProduk;
    private ImageButton mPhoneBtn, mRecycleBtn, mFavoriveBtn;

    private String idProduk = "";
    private String phoneSeller = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        idProduk = getIntent().getStringExtra("idProduk");
        phoneSeller = getIntent().getStringExtra("sid").substring(0);;

        mNamaSeller = (TextView) findViewById(R.id.namaSellerDetail);
        mNamaProduk = (TextView) findViewById(R.id.namaProdukDetail);
        mHargaProduk = (TextView) findViewById(R.id.hargaProdukDetail);
        mDeskripsiProduk = (TextView) findViewById(R.id.deskripsiProdukDetail);

        mGambarProduk = (ImageView) findViewById(R.id.gambarProdukDetail);

        mPhoneBtn = (ImageButton) findViewById(R.id.phoneProdukDetail);
        mRecycleBtn = (ImageButton) findViewById(R.id.recycleProdukDetail);
        mFavoriveBtn = (ImageButton) findViewById(R.id.favoriteProdukDetail);

        getProductDetails(idProduk);


        mPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean installed = appInstallCheck("com.whatsapp");

                if (installed) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://api.whatsapp.com/send?phone="+"62"+phoneSeller+"&text="));
                    startActivity(i);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Whatsapp tidak terinstall!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void getProductDetails(String idProduk) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(idProduk).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);
                    Sellers sellers = dataSnapshot.getValue(Sellers.class);

                    mNamaProduk.setText(products.getNama());
                    mHargaProduk.setText("Rp. " + products.getHarga());
                    mDeskripsiProduk.setText(products.getDeskripsi());
                    mNamaSeller.setText(products.getSname());
                    Picasso.get().load(products.getImage()).fit().centerCrop().into(mGambarProduk);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private boolean appInstallCheck(String url) {

        PackageManager packageManager = getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
