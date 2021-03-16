package com.example.myapplication2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.example.myapplication2.sqlite.DatabaseHelper;
import com.example.myapplication2.sqlite.Note;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

public class ExpListAdapter extends BaseExpandableListAdapter {
    private final ArrayList<ArrayList<Note>> mGroups = new ArrayList<ArrayList<Note>>();
    private final ArrayList<String> mDays;
    private final Context mContext;
    private final String LOG_TAG = "List";

    public ExpListAdapter (Context context, DatabaseHelper db){
        mContext = context;
        mDays = db.distinct(Note.COLUMN_DATE);

        if (mDays != null){
/*
  для java 8 . я не знаю как, но это работает!!! надо срочно разбираться с лямбда-функциями
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());
            Collections.sort(mDays, (s1, s2) -> LocalDate.parse(s1, formatter).
                    compareTo(LocalDate.parse(s2, formatter)));
.getDateStr()

*/
//            сортировка
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            mDays.sort(new Comparator<String>() {
                public int compare(String o1, String o2) {
                    try {
                        return df.parse(o1).compareTo(df.parse(o2));
                    } catch (ParseException pe) {
                        // не получилось
                        Log.d(LOG_TAG, mGroups + "-------" + pe );
                    }
                    return 0;
                }
            });
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
       Double daySum = 0.0;

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

        for (Note zaDen : mGroups.get(groupPosition)) {
           daySum += zaDen.getChas();
        }
        @SuppressLint("DefaultLocale") String strSum = String.format("%.2f",daySum);
        TextView textSum = (TextView) convertView.findViewById(R.id.daySum);
        textSum.setText(strSum);


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