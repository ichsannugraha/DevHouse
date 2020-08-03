package com.tubesmanpropel.devhouse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tubesmanpropel.devhouse.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerTambahProdukActivity extends AppCompatActivity {

    private String namaProd, hargaProd, alamatProd, fasilitasProd, deskripsiProd, luasTanahProd;
    private String saveCurrentDate, saveCurrentTime;
    private EditText mNamaProd, mHargaProd, mAlamatProd, mFasilitasProd, mDeskripsiProd, mLuasTanahProd;
    private Button mTambahProd;
    private ImageView mImageProd1;
    private static final int GalleryPick =1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_tambah_produk);

        //getSupportActionBar().setTitle("Tambah Produk");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        mImageProd1 = (ImageView) findViewById(R.id.gambarProduk1);
        mNamaProd = (EditText) findViewById(R.id.namaProduk);
        mHargaProd = (EditText) findViewById(R.id.hargaProduk);
        mAlamatProd = (EditText) findViewById(R.id.alamatProduk);
        mFasilitasProd = (EditText) findViewById(R.id.fasilitasProduk);
        mDeskripsiProd = (EditText) findViewById(R.id.deskripsiProduk);
        mLuasTanahProd = (EditText) findViewById(R.id.luasTanahProduk);
        mTambahProd = (Button) findViewById(R.id.tambahProdukBtn);
        loadingBar = new ProgressDialog(this);


        mImageProd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        mTambahProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
    }



    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            imageUri = data.getData();
            mImageProd1.setImageURI(imageUri);
        }
    }


    private void ValidateProductData() {
        namaProd = mNamaProd.getText().toString();
        hargaProd = mHargaProd.getText().toString();
        alamatProd = mAlamatProd.getText().toString();
        fasilitasProd = mFasilitasProd.getText().toString();
        deskripsiProd = mDeskripsiProd.getText().toString();
        luasTanahProd = mLuasTanahProd.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Gambar produk tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(namaProd)) {
            Toast.makeText(this, "Nama produk tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(hargaProd)) {
            Toast.makeText(this, "Harga produk tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(alamatProd)) {
            Toast.makeText(this, "Alamat produk tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(fasilitasProd)) {
            Toast.makeText(this, "Fasilitas produk tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(deskripsiProd)) {
            Toast.makeText(this, "Deskripsi produk tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(luasTanahProd)) {
            Toast.makeText(this, "Luas tanah produk tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreProductInformation();
        }
    }


    private void StoreProductInformation() {
        loadingBar.setTitle("Sedang menambah produk");
        loadingBar.setMessage("Mohon tunggu sebentar...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = ProductImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(SellerTambahProdukActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(SellerTambahProdukActivity.this, "Berhasil mengunggah gambar produk!", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }


    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("luasTanah", luasTanahProd);
        productMap.put("deskripsi", deskripsiProd);
        productMap.put("image", downloadImageUrl);
        productMap.put("fasilitas", fasilitasProd);
        productMap.put("alamat", alamatProd);
        productMap.put("harga", hargaProd);
        productMap.put("nama", namaProd);
        productMap.put("sid", Prevalent.currentOnlineSeller.getPhone());

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(SellerTambahProdukActivity.this, SellerSettingsActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(SellerTambahProdukActivity.this, "Berhasil menambah produk!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(SellerTambahProdukActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
