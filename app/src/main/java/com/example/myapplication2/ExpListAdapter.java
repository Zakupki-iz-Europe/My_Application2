package com.example.myapplication2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.myapplication2.sqlite.DatabaseHelper;
import com.example.myapplication2.sqlite.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExpListAdapter extends BaseExpandableListAdapter {
    private ArrayList<Map<String,Double>> mGroups = new ArrayList<Map<String,Double>>();
    private ArrayList<String> mDays = new ArrayList<>();
    private Context mContext;
    private DatabaseHelper db;

    public ExpListAdapter (Context context, DatabaseHelper db){
        mContext = context;
        Map<String,Double> map = new HashMap<>();
        List<Note> notes = db.getAllNotes();
        if (db.getNotesCount() > 0) {
            for (int i = 0; i < db.getNotesCount(); i++) {
                mDays.add(notes.get(i).getData());
                map.put(notes.get(i).getZak(), notes.get(i).getChas());
            }
        }
        else {
            mDays.add("Нет даты");
            map.put("Нет заказов", (double) 0);
        }
        mGroups.add(map);
    }


    @Override
    public int getGroupCount() {
        return mDays.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_view, null);
        }

        if (isExpanded){
            //Изменяем что-нибудь, если текущая Group раскрыта
        }
        else{
            //Изменяем что-нибудь, если текущая Group скрыта
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
        textGroup.setText(mDays.get(groupPosition));

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);
        }
        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);


        Iterator<Map.Entry<String, Double>> itr = mGroups.get(groupPosition).entrySet().iterator();
        if (itr.hasNext()) {
            Map.Entry<String, Double> entry =  itr.next();
            // get key
            String zak_nar = entry.getKey();
            // get value
            Double chasy  = entry.getValue();
            zak_nar = zak_nar + " " + chasy.toString();
            textChild.setText(zak_nar);
        }

        Button button = (Button)convertView.findViewById(R.id.buttonChild);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"button is pressed",Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}