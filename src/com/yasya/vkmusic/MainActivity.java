package com.yasya.vkmusic;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yasya.vkmusic.FragWebView.onFinishWork;

import org.json.JSONObject;

public class MainActivity extends FragmentActivity implements onFinishWork {

    FragWebView frag1;
    FragListView frag2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        frag1 = FragWebView.getInstance(this);
        Button button = (Button) findViewById(R.id.button1);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fMan = getSupportFragmentManager();
                FragmentTransaction ft = fMan.beginTransaction();
                ft.replace(R.id.layoutMain, frag1);
                ft.commit();
            }
        });
    }

    @Override
    public void createdJSON(JSONObject obj) {
        frag2 = new FragListView();
        FragmentManager fMan = getSupportFragmentManager();
        FragmentTransaction ft = fMan.beginTransaction();
        ft.replace(R.id.layoutMain, frag2);
        ft.commit();
    }
}
