package com.example.myapplication2;
// from MainActivity.java
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.MyRecyclerViewAdapter;
import com.example.myapplication2.R;

//import java.util.ArrayList;
//
//public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
//
//
//    MyRecyclerViewAdapter adapter;
//
//    @Override
//    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
//    }
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // data to populate the RecyclerView with
//        ArrayList<String> animalNames = new ArrayList<>();
//        animalNames.add("Horse");
//        animalNames.add("Cow");
//        animalNames.add("Camel");
//        animalNames.add("Sheep");
//        animalNames.add("Goat");
//
//        // set up the RecyclerView
//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
//        recyclerView.setLayoutManager(layoutManager);
//
//        adapter = new MyRecyclerViewAdapter(this, animalNames);
//        adapter.setClickListener(this);
//        recyclerView.setAdapter(adapter);
//    }
//}

// from activity_main.xml
//<androidx.recyclerview.widget.RecyclerView
//        xmlns:app="http://schemas.android.com/apk/res-auto"
//        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
//        android:id="@+id/rvAnimals"
//        android:layout_width="match_parent"
//        android:layout_height="0dp"
//        android:layout_weight="5"/>