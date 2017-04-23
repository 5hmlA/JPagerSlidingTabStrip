package com.jonas.acase;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class MainActivity extends ListActivity {
    private String[] acticityNames = new String[] { "expand", "notExpand" };
    private Class[] acticitys = new Class[] { ExpandActivity.class, NotExpandActicity.class };


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, acticityNames));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this,acticitys[position]));
            }
        });
    }
}