package com.tubesmanpropel.devhouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.tubesmanpropel.devhouse.Model.Products;
import com.tubesmanpropel.devhouse.ViewHolder.ProductViewHolderMain;

import java.text.DecimalFormat;

public class UserHomeFragment extends Fragment {

    private SliderLayout sliderLayout;

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private SearchView searchView;
    private RelativeLayout mainLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_home, container, false);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        searchView = rootView.findViewById(R.id.mainSearchView);
        recyclerView = rootView.findViewById(R.id.userRecyclerView);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UserSearchActivity.class);
                startActivity(i);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef.orderByChild("time"), Products.class)
                        .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolderMain> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolderMain>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolderMain holder, int i, @NonNull final Products model) {
                        holder.editImageBtn.setVisibility(View.INVISIBLE);
                        holder.namaProdukTxt.setText(model.getNama());
                        holder.hargaProdukTxt.setText("Rp." + formatNumber(model.getHarga()));
                        Picasso.get().load(model.getImage()).fit().centerCrop().into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(), ProductDetailsActivity.class);
                                i.putExtra("idProduk", model.getPid());
                                i.putExtra("sid", model.getSid());
                                i.putExtra("userType", "User");
                                //i.putExtra("hargaProduk", model.getHarga());
                                //i.putExtra("deskripsiProduk", model.getDeskripsi());
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

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    private static String formatNumber(String number) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0");
        return formatter.format(Double.parseDouble(number));
    }
}