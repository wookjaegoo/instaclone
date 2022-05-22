package com.example.recentverinstacloneforsuccess;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.recentverinstacloneforsuccess.Fragment.HomeFragment;
import com.example.recentverinstacloneforsuccess.Fragment.NotificationFragment;
import com.example.recentverinstacloneforsuccess.Fragment.ProfileFragment;
import com.example.recentverinstacloneforsuccess.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

//adding fragment transaction when user clicks on menu item
//하단바 메뉴 선택시 레이아웃 바꿔주는 동적 기능 할당하는 클래스 영상 setonclick~메소드
//안써서 다른거 navigationbarview로 대체함 이게더 짧고 간결하고 REselected말고 selected로 구현됨


public class MainActivity extends AppCompatActivity {


    Fragment selectFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationBarView navigationBarView= findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        Bundle intent = getIntent().getExtras();

        if(intent!=null)
        {
            String publisher = intent.getString("publisherid");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();

            editor.putString("profileid",publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }



        navigationBarView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                    return true;
                case R.id.nav_search:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SearchFragment()).commit();
                    return true;
                case R.id.nav_add:
                    selectFragment = null;
                    startActivity (new Intent( MainActivity.this, PostActivity.class));
                case R.id.nav_heart:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new NotificationFragment()).commit();
                    return true;
                case R.id.nav_profile:
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                    return true;
            }
            if (selectFragment !=null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectFragment).commit();

            }
            return false;
        });

    }

}