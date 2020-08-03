package com.tubesmanpropel.devhouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tubesmanpropel.devhouse.Model.Products;
import com.tubesmanpropel.devhouse.Model.Sellers;
import com.tubesmanpropel.devhouse.Prevalent.Prevalent;

import java.text.DecimalFormat;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView mNamaSeller, mNamaProduk, mHargaProduk, mDeskripsiProduk;
    private TextView mAlamatProduk, mFasilitasProduk, mLuasTanahProduk, mTanggalUploadProduk;
    private ImageView mGambarProduk;
    private Button mPhoneBtn, mFavoriteBtn;
    private ProgressDialog loadingBar;

    private String idProduk = "";
    private String phoneSeller = "";
    private String userType = "";
    private String message;
    private String messageProduk = "";

    private DatabaseReference favoritesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites");
        idProduk = getIntent().getStringExtra("idProduk");
        phoneSeller = getIntent().getStringExtra("sid").substring(0);
        userType = getIntent().getStringExtra("userType");

        mTanggalUploadProduk = (TextView) findViewById(R.id.tanggalUploadDetail);
        mNamaSeller = (TextView) findViewById(R.id.namaSellerDetail);
        mNamaProduk = (TextView) findViewById(R.id.namaProdukDetail);
        mHargaProduk = (TextView) findViewById(R.id.hargaProdukDetail);
        mDeskripsiProduk = (TextView) findViewById(R.id.deskripsiProdukDetail);
        mAlamatProduk = (TextView) findViewById(R.id.alamatProdukDetail);;
        mFasilitasProduk = (TextView) findViewById(R.id.fasilitasProdukDetail);;
        mLuasTanahProduk = (TextView) findViewById(R.id.luasTanahProdukDetail);;
        mGambarProduk = (ImageView) findViewById(R.id.gambarProdukDetail);

        mPhoneBtn = (Button) findViewById(R.id.phoneProdukDetail);
        mFavoriteBtn = (Button) findViewById(R.id.favoriteProdukDetail);
        loadingBar = new ProgressDialog(this);

        if (userType.equals("Seller")) {
            mFavoriteBtn.setEnabled(false);
        }
        else {
            checkFavorite(idProduk);
        }

        getProductDetails(idProduk);


        mFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorite();
            }
        });

        mPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean installed = appInstallCheck("com.whatsapp");

                if (installed) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://api.whatsapp.com/send?phone="+"62"+phoneSeller+"&text="+message));
                    startActivity(i);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Whatsapp tidak terinstall!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void saveToRecent() {

    }


    private void addToFavorite(){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Favorites").child(Prevalent.currentOnlineUser.getPhone()).child(idProduk).exists())) {
                    HashMap<String, Object> favoritedataMap = new HashMap<>();
                    favoritedataMap.put("pid", idProduk);
                    favoritedataMap.put("sid", phoneSeller);

                    RootRef.child("Favorites").child(Prevalent.currentOnlineUser.getPhone()).child(idProduk).updateChildren(favoritedataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mFavoriteBtn.setText("Favorited");
                                        Toast.makeText(ProductDetailsActivity.this, "Berhasil menambah ke favorite!", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                    else {
                                        loadingBar.dismiss();
                                        Toast.makeText(ProductDetailsActivity.this, "Terjadi kesalahan, silakan coba kembali!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Favorites").child(Prevalent.currentOnlineUser.getPhone()).child(idProduk)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        mFavoriteBtn.setText("Favorite");
                                        Toast.makeText(ProductDetailsActivity.this, "Telah dihapus dari favorite!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    //Toast.makeText(ProductDetailsActivity.this, "Akun anda sudah memiliki akun seller!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    //Toast.makeText(SellerRegisterActivity.this, "Gagal membuat akun seller!", Toast.LENGTH_SHORT).show();

                    //Intent intent = new Intent(SellerRegisterActivity.this, MainActivity.class);
                    //startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void checkFavorite(String idProduk) {

        favoritesRef.child(Prevalent.currentOnlineUser.getPhone()).child(idProduk).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mFavoriteBtn.setText("Favorited");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getProductDetails(final String idProduk) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        final DatabaseReference sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        productsRef.child(idProduk).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);

                    mTanggalUploadProduk.setText(products.getDate());
                    mNamaProduk.setText(products.getNama());
                    mHargaProduk.setText("Rp. " + formatNumber(products.getHarga()));
                    mDeskripsiProduk.setText(products.getDeskripsi());
                    mAlamatProduk.setText(products.getAlamat());
                    mFasilitasProduk.setText(products.getFasilitas());
                    mLuasTanahProduk.setText(products.getLuasTanah() + " m2");

                    messageProduk = products.getNama();
                    message = "Hai, saya tertarik dengan informasi '" + messageProduk + "' di aplikasi DevHouse. Mohon informasinya, terimakasih!";
                    //mNamaSeller.setText(products.getSname());
                    Picasso.get().load(products.getImage()).fit().centerCrop().into(mGambarProduk);

                    sellersRef.child(products.getSid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Sellers sellers = dataSnapshot.getValue(Sellers.class);

                                mNamaSeller.setText(sellers.getUsername());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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


    private static String formatNumber(String number) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0");
        return formatter.format(Double.parseDouble(number));
    }
}
