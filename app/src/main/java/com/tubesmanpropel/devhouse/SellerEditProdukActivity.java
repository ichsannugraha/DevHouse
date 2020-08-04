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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tubesmanpropel.devhouse.Model.Products;
import com.tubesmanpropel.devhouse.Prevalent.Prevalent;

import java.util.HashMap;

public class SellerEditProdukActivity extends AppCompatActivity {

    private EditText mNamaProduk, mHargaProduk, mAlamatProduk, mFasilitasProduk, mDeskripsiProduk, mLuasTanahProduk, mLuasBangunanProduk;
    private Button mEditProdukBtn;
    private ImageView mGambarProduk;

    private static final int GalleryPick =1;
    private Uri imageUri;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private StorageTask uploadTask;
    private ProgressDialog loadingBar;

    private String idProduk = "";
    private String checker = "";
    private String myUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_edit_produk);

        idProduk = getIntent().getStringExtra("idProduk");

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        mGambarProduk = (ImageView) findViewById(R.id.gambarProdukEdit);
        mNamaProduk = (EditText) findViewById(R.id.namaProdukEdit);
        mHargaProduk = (EditText) findViewById(R.id.hargaProdukEdit);
        mAlamatProduk = (EditText) findViewById(R.id.alamatProdukEdit);
        mFasilitasProduk = (EditText) findViewById(R.id.fasilitasProdukEdit);
        mDeskripsiProduk = (EditText) findViewById(R.id.deskripsiProdukEdit);
        mLuasTanahProduk = (EditText) findViewById(R.id.luasTanahProdukEdit);
        mLuasBangunanProduk = (EditText) findViewById(R.id.luasBangunanProdukEdit);
        mEditProdukBtn = (Button) findViewById(R.id.editProdukBtn);
        loadingBar = new ProgressDialog(this);

        produkInfoDisplay(idProduk);


        mEditProdukBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    productInfoSaved();
                }
                else {
                    updateOnlyProductInfo(idProduk);
                }
            }
        });

        mGambarProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                openGallery();
            }
        });
    }



    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    private void updateOnlyProductInfo(String idProduk) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products");

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("luasTanah", mLuasTanahProduk.getText().toString());
        productMap.put("luasBangunan", mLuasBangunanProduk.getText().toString());
        productMap.put("deskripsi", mDeskripsiProduk.getText().toString());
        productMap.put("fasilitas", mFasilitasProduk.getText().toString());
        productMap.put("alamat", mAlamatProduk.getText().toString());
        productMap.put("harga", mHargaProduk.getText().toString());
        productMap.put("nama", mNamaProduk.getText().toString());
        ref.child(idProduk).updateChildren(productMap);

        startActivity(new Intent(SellerEditProdukActivity.this, SellerMainActivity.class));
        Toast.makeText(this, "Berhasil mengupdate info produk!", Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            imageUri = data.getData();
            mGambarProduk.setImageURI(imageUri);
        }
    }


    private void productInfoSaved() {
        if (TextUtils.isEmpty(mNamaProduk.getText().toString()))
        {
            Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mHargaProduk.getText().toString()))
        {
            Toast.makeText(this, "Harga tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mAlamatProduk.getText().toString()))
        {
            Toast.makeText(this, "Alamat tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mFasilitasProduk.getText().toString()))
        {
            Toast.makeText(this, "Fasilitas tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mDeskripsiProduk.getText().toString()))
        {
            Toast.makeText(this, "Deskripsi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mLuasTanahProduk.getText().toString()))
        {
            Toast.makeText(this, "Luas tanah tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mLuasBangunanProduk.getText().toString()))
        {
            Toast.makeText(this, "Luas bangunan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage(idProduk);
        }
    }


    private void uploadImage(final String idProduk) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Produk");
        progressDialog.setMessage("Mohon tunggu, data produk anda sedang diubah...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = ProductImagesRef
                    .child(idProduk + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if (task.isSuccessful())
                            {
                                Uri downloadUrl = (Uri) task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products");

                                HashMap<String, Object> productMap = new HashMap<>();
                                productMap.put("luasTanah", mLuasTanahProduk.getText().toString());
                                productMap.put("luasBangunan", mLuasBangunanProduk.getText().toString());
                                productMap.put("deskripsi", mDeskripsiProduk.getText().toString());
                                productMap.put("fasilitas", mFasilitasProduk.getText().toString());
                                productMap.put("alamat", mAlamatProduk.getText().toString());
                                productMap.put("harga", mHargaProduk.getText().toString());
                                productMap.put("nama", mNamaProduk.getText().toString());
                                productMap. put("image", myUrl);
                                ref.child(idProduk).updateChildren(productMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(SellerEditProdukActivity.this, SellerMainActivity.class));
                                Toast.makeText(SellerEditProdukActivity.this, "Update produk berhasil!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(SellerEditProdukActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "gambar tidak terpilih!", Toast.LENGTH_SHORT).show();
        }
    }


    private void produkInfoDisplay(String idProduk) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(idProduk).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);

                    mNamaProduk.setText(products.getNama());
                    mHargaProduk.setText(products.getHarga());
                    mAlamatProduk.setText(products.getAlamat());
                    mFasilitasProduk.setText(products.getFasilitas());
                    mDeskripsiProduk.setText(products.getDeskripsi());
                    mLuasTanahProduk.setText(products.getLuasTanah());
                    mLuasBangunanProduk.setText(products.getLuasBangunan());

                    Picasso.get().load(products.getImage()).into(mGambarProduk);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
