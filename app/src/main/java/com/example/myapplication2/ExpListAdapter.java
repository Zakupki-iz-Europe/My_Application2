package com.example.myapplication2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.RequiresApi;

import com.example.myapplication2.sqlite.DatabaseHelper;
import com.example.myapplication2.sqlite.Note;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExpListAdapter extends BaseExpandableListAdapter {
    private final ArrayList<ArrayList<Note>> mGroups = new ArrayList<ArrayList<Note>>();
    private ArrayList<String> mDays;
    private final Context mContext;
    private final String LOG_TAG = "List";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ExpListAdapter (Context context, DatabaseHelper db){
        mContext = context;
        mDays = db.distinct(Note.COLUMN_DATE);

        if (mDays != null){
// я не знаю как, но это работает!!! надо срочно разбираться с лямбда-функциями
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            Collections.sort(mDays, (s1, s2) -> LocalDate.parse(s1, formatter).
                    compareTo(LocalDate.parse(s2, formatter)));

            for(String data:mDays){
               mGroups.add(db.where(Note.COLUMN_DATE, data));
            }
        }
        else {
            mDays.add("Нет даты");
            mGroups.add(new ArrayList<>());}
        Log.d(LOG_TAG, mGroups + "-------" );
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

    @SuppressLint("DefaultLocale")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);
        }
        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
        TextView textChasy = (TextView) convertView.findViewById(R.id.textChasy);

        if (textChild != null && textChasy != null ) {
            String zak_nar = "";
                zak_nar = mGroups.get(groupPosition).get(childPosition).getZak();
                Double chasy = mGroups.get(groupPosition).get(childPosition).getChas();

                textChild.setText(zak_nar);

                zak_nar = String.format("%.2f",chasy);
                textChasy.setText(zak_nar);
        }

       return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}