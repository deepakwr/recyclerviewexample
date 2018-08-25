package com.example.deepak.recyclerviewexample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static public String tag = "RecyclerViewExample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment();
    }



    private void loadFragment() {

        if(isConnected()) {

            findViewById(R.id.errorContainer).setVisibility(View.GONE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            CountryInfoListFragment countryInfoListFragment = new CountryInfoListFragment();
            fragmentTransaction.replace(R.id.fragmentContainer, countryInfoListFragment);
            fragmentTransaction.commit();
        }
        else
        {
            findViewById(R.id.errorContainer).setVisibility(View.VISIBLE);
        }
    }

    // check network connection
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.retry)
        {
            loadFragment();
        }
    }
}
