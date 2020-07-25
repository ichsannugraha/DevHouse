package com.tubesmanpropel.devhouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.tubesmanpropel.devhouse.Adapter.PagerAdapter;

public class SellerMainActivity extends AppCompatActivity {

    private ImageButton mNotificationBtn, mMessageBtn, mSettingsBtn;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private TabItem mProdukTab, mDiskusiTab;
    private PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);

        mNotificationBtn = (ImageButton) findViewById(R.id.sellerMainNotificationBtn);
        mMessageBtn = (ImageButton) findViewById(R.id.sellerMainMessageBtn);
        mSettingsBtn = (ImageButton) findViewById(R.id.sellerMainSettingsBtn);
        mViewPager = (ViewPager) findViewById(R.id.sellerViewPager);
        tabLayout = (TabLayout) findViewById(R.id.sellerTabBar);
        mProdukTab = (TabItem) findViewById(R.id.sellerProdukTab);
        mDiskusiTab = (TabItem) findViewById(R.id.sellerDiskusiTab);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    mViewPager.setCurrentItem(tab.getPosition());
                } else if (tab.getPosition() == 1) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        mNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellerMainActivity.this, SellerNotificationActivity.class);
                startActivity(i);
            }
        });

        mMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellerMainActivity.this, SellerMessageActivity.class);
                startActivity(i);
            }
        });

        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellerMainActivity.this, SellerSettingsActivity.class);
                startActivity(i);
            }
        });
    }
}
