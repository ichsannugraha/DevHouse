package com.tubesmanpropel.devhouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.tubesmanpropel.devhouse.Model.Products;
import com.tubesmanpropel.devhouse.ViewHolder.ProductViewHolderMain;

public class UserSearchActivity extends AppCompatActivity {

    private EditText inputTxt;
    private ImageView backBtn;
    private Button searchBtn;
    private RecyclerView searchList;
    private String searchInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        inputTxt = (EditText) findViewById(R.id.mainSearchView);
        backBtn = (ImageView) findViewById(R.id.searchActBackBtn);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        searchList = (RecyclerView) findViewById(R.id.search_list);

        searchList.setLayoutManager(new LinearLayoutManager(UserSearchActivity.this));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput = inputTxt.getText().toString();

                onStart();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");


        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("nama").startAt(searchInput), Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolderMain> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolderMain>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolderMain holder, int i, @NonNull final Products model) {
                        holder.namaProdukTxt.setText(model.getNama());
                        holder.hargaProdukTxt.setText("Rp." + model.getHarga());
                        Picasso.get().load(model.getImage()).fit().centerCrop().into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(UserSearchActivity.this, ProductDetailsActivity.class);
                                i.putExtra("idProduk", model.getPid());
                                i.putExtra("sid", model.getSid());
                                startActivity(i);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolderMain onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout_main, parent, false);
                        ProductViewHolderMain holder = new ProductViewHolderMain(view);
                        return holder;
                    }
                };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}
