package com.fj.app.apk.share;

import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickItemListener {

    private static final String TAG = "MainActivity";

    public static final String NIGHT_MODE = "night_mode";

    private SharedPreferences mSharedPref;
    private RecyclerView mRecyclerView;
    private AppAdapter mAdapter;
    private PackageManager pm;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // init shared preferences
        mSharedPref = getPreferences(Context.MODE_PRIVATE);

        if (isNightModeEnabled()) {
            setAppTheme(R.style.AppTheme_Base_Night);
        } else {
            setAppTheme(R.style.AppTheme_Base_Light);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<App> apps = new ArrayList<>();

        /* Get app list */
        pm = this.getPackageManager();
        List<ApplicationInfo> packages =  pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            String name;
            /* Use package name if app label is empty */
            if ((name = String.valueOf(pm.getApplicationLabel(packageInfo))).isEmpty()) {
                name = packageInfo.packageName;
            }
            Drawable icon = pm.getApplicationIcon(packageInfo);
            String apkPath = packageInfo.sourceDir;
            long apkSize = new File(packageInfo.sourceDir).length();

            apps.add(new App(name, icon, apkPath, apkSize));
        }

        Collections.sort(apps, new Comparator<App>() {
            @Override
            public int compare(App app1, App app2) {
                return app1.getName().toLowerCase().compareTo(app2.getName().toLowerCase());
            }
        });

        mRecyclerView = findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        //setting adapter
        mAdapter = new AppAdapter(this,apps, this);
        mRecyclerView.setAdapter(mAdapter);

        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.about, null);
        dialogBuilder.setView(dialogView);
        TextView rate = dialogView.findViewById(R.id.rate);
        TextView moreapps = dialogView.findViewById(R.id.more);
        rate.setOnClickListener(listener);
        moreapps.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.rate){
                Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goToMarket);
                alertDialog.dismiss();
            }
            if (view.getId() == R.id.more){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=6661394351829764948")));
                alertDialog.dismiss();
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchitem = menu.findItem(R.id.searchview);
        SearchView searchView = (SearchView) searchitem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nightmode:
                Log.d(TAG, "onOptionsItemSelected: ");
                if (isNightModeEnabled()){
                    setIsNightModeEnabled(false);
                    setAppTheme(R.style.AppTheme_Base_Light);
                    recreate();
                }else {
                    setIsNightModeEnabled(true);
                    setAppTheme(R.style.AppTheme_Base_Night);
                    recreate();
                }
                return true;
            case R.id.about:
                alertDialog = dialogBuilder.create();
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.nightmode);
        if (isNightModeEnabled()){
            item.setIcon(R.drawable.ic_light_mode);
        }else {
            item.setIcon(R.drawable.ic_night_mode);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemClick(int position) {
        Intent shareApkIntent = new Intent();
        shareApkIntent.setAction(Intent.ACTION_SEND);
        shareApkIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mAdapter.getItem(position).getApkPath())));
        shareApkIntent.setType("application/vnd.android.package-archive");
        startActivity(Intent.createChooser(shareApkIntent, getString(R.string.share_apk)));
    }

    private void setAppTheme(@StyleRes int style) {
        setTheme(style);
    }

    private boolean isNightModeEnabled() {
        return  mSharedPref.getBoolean(NIGHT_MODE, false);
    }

    private void setIsNightModeEnabled(boolean state) {
        SharedPreferences.Editor mEditor = mSharedPref.edit();
        mEditor.putBoolean(NIGHT_MODE, state);
        mEditor.apply();
    }


}
