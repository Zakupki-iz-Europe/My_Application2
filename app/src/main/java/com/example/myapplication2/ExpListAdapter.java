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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExpListAdapter extends BaseExpandableListAdapter {
    private ArrayList<Map<String,Long>> mGroups = new ArrayList<Map<String, Long>>();
    private ArrayList<String> mDays = new ArrayList<>();
    private final Context mContext;
    private final SQLiteDatabase mydb;
    private final String LOG_TAG = "List";

    public ExpListAdapter (Context context, DatabaseHelper db){
        mydb = db.getReadableDatabase();
        mContext = context;
        String str = "";
        int indexGroup = 0;
        Map<String,Long> map = new HashMap<>();

//        List<Note> notes = db.getAllNotes();
        String selectQuery = "SELECT  DISTINCT " +  Note.COLUMN_DATE + " FROM " + Note.TABLE_NAME;

//        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = mydb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do { // дергаем каждый день
                str = cursor.getString(cursor.getColumnIndex(Note.COLUMN_DATE));
                mDays.add(str);
                indexGroup++;
//                mGroups.add(indexGroup,new ArrayList<>());
//               а теперь определим сколько заказов было за этот день
                selectQuery = "SELECT " +  Note.COLUMN_ZAK + "," + Note.COLUMN_CHAS +
                        " FROM " + Note.TABLE_NAME + " WHERE " + Note.COLUMN_DATE +
                        " = '" + mDays.get(cursor.getColumnIndex(Note.COLUMN_DATE)) + "'";
                Cursor curNotes = mydb.rawQuery(selectQuery, null);
                if (curNotes.moveToFirst()) {
                    do {
                        map.put(curNotes.getString(curNotes.getColumnIndex(Note.COLUMN_ZAK)),
                                curNotes.getLong(curNotes.getColumnIndex(Note.COLUMN_CHAS)));
                        } while (curNotes.moveToNext());
                }
                mGroups.add(map);

            } while (cursor.moveToNext());
        }
        else {
            mDays.add("Нет даты");
            map.put("Нет заказов", (long) 0);
            mGroups.add(map);}
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

        if (textChild != null) {
            Iterator<Map.Entry<String, Long>> itr = mGroups.get(groupPosition).entrySet().iterator();
            if (itr.hasNext()) {
                Map.Entry<String, Long> entry = itr.next();
                // get key
                String zak_nar = entry.getKey();
                Long chasy = entry.getValue();
                zak_nar = zak_nar + " " + chasy.toString();
                Log.d(LOG_TAG, itr.toString() + "-------" + zak_nar);

                textChild.setText(zak_nar);
            }
        }
        //
//        Button button = (Button)convertView.findViewById(R.id.buttonChild);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(mContext,"button is pressed",Toast.LENGTH_LONG).show();
//            }
//        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}