package com.example.myapplication2;

import android.content.Context;
import android.database.Cursor;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExpListAdapter extends BaseExpandableListAdapter {
    private final ArrayList<ArrayList<Note>> mGroups = new ArrayList<ArrayList<Note>>();
    private ArrayList<String> mDays;
    private final Context mContext;
    private final SQLiteDatabase mydb;
    private final String LOG_TAG = "List";
    private  final ArrayList<Note> notes = new ArrayList<Note>();

    public ExpListAdapter (Context context, DatabaseHelper db){
        List<Note> notes = db.getAllNotes();
        mydb = db.getReadableDatabase();
        mContext = context;
        String str = "";
        int index = 0;

        mDays = db.distinct(Note.COLUMN_DATE);
        if (mDays != null){
            for(String data:mDays){
               mGroups.add(db.where(Note.COLUMN_DATE, data));
            }

//        String selectQuery = "SELECT  DISTINCT " +  Note.COLUMN_DATE + " FROM " + Note.TABLE_NAME;
//
//        Cursor cursor = mydb.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do { // дергаем каждый день
////                Map<String,Double> map = new HashMap<>();
//                str = cursor.getString(cursor.getColumnIndex(Note.COLUMN_DATE));
//                mDays.add(str);
//                Log.d(LOG_TAG, "-------" + str);
////               а теперь определим сколько заказов было за этот день
//                selectQuery = "SELECT " +  Note.COLUMN_ZAK + "," + Note.COLUMN_CHAS +
//                        " FROM " + Note.TABLE_NAME + " WHERE " + Note.COLUMN_DATE +
//                        " = '" + str + "'";
//                Cursor curNotes = mydb.rawQuery(selectQuery, null);
//                if (curNotes.moveToFirst()) {
//                    do {
//
//                        map.put(curNotes.getString(curNotes.getColumnIndex(Note.COLUMN_ZAK)),
//                                curNotes.getDouble(curNotes.getColumnIndex(Note.COLUMN_CHAS)));
//                        Log.d(LOG_TAG, curNotes.getString(curNotes.getColumnIndex(Note.COLUMN_ZAK)) + "-------" + curNotes.getDouble(curNotes.getColumnIndex(Note.COLUMN_CHAS)));
//
//                    } while (curNotes.moveToNext());
//                    Log.d(LOG_TAG, map + "-----------map----------");
//                }
//                mGroups.add(map);
//                index++;
//                curNotes.close();
//            } while (cursor.moveToNext());
        }
        else {
//            Map<String,Double> map = new HashMap<>();
            mDays.add("Нет даты");
//            map.put("Нет заказов", (double) 0);
            mGroups.add(new ArrayList<>());}
//        cursor.close();
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
                zak_nar = chasy.toString();
                textChasy.setText(zak_nar);
        }

       return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}