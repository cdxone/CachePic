package com.example.cachepic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.lib_image_lib.ImageLoaderManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.iv);
        findViewById(R.id.btn_cache).setOnClickListener(this);
        findViewById(R.id.btn_load).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cache){
            String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1602742630353&di=f58aa72324c92e1d5629d8434494487f&imgtype=0&src=http%3A%2F%2Ft8.baidu.com%2Fit%2Fu%3D1484500186%2C1503043093%26fm%3D79%26app%3D86%26f%3DJPEG%3Fw%3D1280%26h%3D853";
            ImageLoaderManager.getInstance().cachePic(this,url);
        } else if (id == R.id.btn_load){
            String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1602742630353&di=f58aa72324c92e1d5629d8434494487f&imgtype=0&src=http%3A%2F%2Ft8.baidu.com%2Fit%2Fu%3D1484500186%2C1503043093%26fm%3D79%26app%3D86%26f%3DJPEG%3Fw%3D1280%26h%3D853";
            ImageLoaderManager.getInstance().displayImageForView(iv,url);
        }
    }
}