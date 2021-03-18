package com.example.myapplication2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.NoCopySpan;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication2.sqlite.DatabaseHelper;
import com.example.myapplication2.sqlite.Note;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class MainActivity<global> extends AppCompatActivity  {

//    -------------------------начало списка1------------------
    //https://github.com/ravi8x/AndroidSQLite.git
    //----------------------------------конец списка1-------------------

    private final static String NULLS = "00,00";
    private final static String VSEGO_CHASOV = "Всего часов: ";
    static int LEN_ZAKAZ_NAR = 7;
    int len_zakaz_naryad = 5;
    char razdelitel = ',';
    final String LOG_TAG = "myLogs";
    public DatabaseHelper db;
    boolean isNoteChanged = FALSE;
    Note noteForChange = new Note();
    RadioButton rbZN, rbZAP;
    EditText et_zakaz_naryad,
            et_normo_chasy;
    TextView et_data,
            tv_header,
            tv_raboty;
    ExpandableListView listView;
    String Value;
    Calendar dateAndTime = Calendar.getInstance();
    ColorStateList oldColors;
//    -------------- Удаление по тапу одной работы из строки ВСЕГО ЧАСОВ ---------
    public void delText(View view) {
        String str = tv_raboty.getText().toString();
        String endStr = str;
        if (str.indexOf(razdelitel) > 0)  endStr = str.substring(0,str.lastIndexOf(razdelitel) - 2);
        tv_raboty.setText(endStr);
    }

//  ----------------- Основная программа---------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        et_zakaz_naryad = findViewById(R.id.zakaz_naryad);
        et_normo_chasy = findViewById(R.id.normochasy);
        et_data = findViewById(R.id.currentDateTime);
        rbZN = findViewById(R.id.radioButton);
        rbZAP = findViewById(R.id.radioButton2);
        tv_header = (TextView) findViewById(R.id.header);
        tv_raboty = (TextView) findViewById(R.id.tv_chasy);
        listView = (ExpandableListView)findViewById(R.id.exListView);


//        https://maxfad.ru/programmer/android/252-sozdanie-spiska-listview-i-arrayadapter-v-android-studio.html
        clearField();
        openText(et_data);
        ColorStateList oldColors_background =  rbZN.getLinkTextColors();
        oldColors =  et_zakaz_naryad.getTextColors(); //save original colors
        Log.d(LOG_TAG, oldColors+ "-------" + oldColors_background);
        findViewById(R.id.divider).setBackgroundColor(oldColors_background.getDefaultColor());

         //Создаем адаптер и передаем context и список с данными
        ExpListAdapter adapter2 = new ExpListAdapter(getApplicationContext(), db);
        listView.setAdapter(adapter2);

        // ---------------Отработка нажатий по списку ------------------------------------
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @SuppressLint("SetTextI18n")
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                isNoteChanged = TRUE;
                noteForChange = (Note) adapter2.getChild(groupPosition, childPosition);
                Log.d(LOG_TAG, "onChildClick groupPosition = " + groupPosition +
                        " childPosition = " + childPosition +
                        " noteForChange = " + noteForChange.getId());
//                );
                String zn = "";
                clearField();
                et_data.setText(noteForChange.getData());
                et_normo_chasy.setText( Double.toString(noteForChange.getChas()));
                if (noteForChange.getZak().contains(rbZAP.getText().
                                         subSequence(0,rbZAP.getText().length()))) {
                    rbZAP.setChecked(TRUE);
                    zn = noteForChange.getZak().substring(rbZAP.getText().length());}
                else {
                    rbZN.setChecked(TRUE);
                    zn = noteForChange.getZak().substring(rbZN.getText().length());}

                et_zakaz_naryad.setText(zn);
                return false;
            }
        });

// ------------------- Обработка изменения текста в заказ-наряде ----------------------------
   et_zakaz_naryad.addTextChangedListener(et_zn_watcher);
// ------------------------- Обработка изменения часов ---------------------------------------
   et_normo_chasy.addTextChangedListener(new MyWatcher());
    }

    TextWatcher et_zn_watcher =  new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {
            check_text_zn(s);
        }
    };

    public boolean check_text_zn(Editable s) {
        len_zakaz_naryad = set_len_zn();
        if (s.length() < len_zakaz_naryad) {
            et_zakaz_naryad.setTextColor(Color.RED);
            return FALSE;
        }
        else {
            et_zakaz_naryad.setTextColor(oldColors);
            et_normo_chasy.requestFocus();
            et_normo_chasy.setSelection(et_normo_chasy.getText().length());
            if (s.length() > len_zakaz_naryad)
                s.delete(len_zakaz_naryad, s.length());
        }
        return TRUE;
    };


    public class MyWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {
            int len = s.length();
            if (len > 0) {
                if (len != 5) {
                    StringBuilder strBuff = new StringBuilder();
                    char c;
                    double chas;
                    String strng; // Выбрасываю все знаки из строки - хотя их нельзя ввести, но можно вставить
                    for (int i = 0; i < len; i++) {
                        c = et_normo_chasy.getText().toString().charAt(i);
                        if (Character.isDigit(c)) {
                            strBuff.append(c);
                        }
                    }

                    int strLen = strBuff.length();
                    strng = strBuff.toString();
                        if (strLen > 4) strng = strBuff.substring(strLen - 4);
                    chas = Double.parseDouble(strng);
                    chas = chas/100;
                    DecimalFormat formater = new DecimalFormat("00.00");
                        if (chas==0) strng = NULLS; else strng = formater.format(chas);
                    Log.d(LOG_TAG, strng+ "-------" + chas);
                    s.replace(0,s.length(),strng);
                }
                else if (s.charAt(0) != '0') addJob(et_normo_chasy);

            }
        }
    }

    public void changeTheme(View view) {
        Log.d(LOG_TAG, "-------" + this.getTheme());
        setTheme(R.style.Theme_AppCompat_DayNight);
        recreate();
    }
// ------------------------------ Кнопка добавить работу в строку ВСЕГО РАБОТ-------
    public void addJob(View view) {
        String raboty = tv_raboty.getText().toString();
        String chasyki = et_normo_chasy.getText().toString();
        Log.d(LOG_TAG, chasyki + "-------" + NULLS);
        if (!chasyki.equals(NULLS)) {
            String firstJob = raboty + chasyki;
            et_normo_chasy.setText(NULLS);
            et_normo_chasy.setSelection(NULLS.length());
            firstJob = firstJob + " + ";
            tv_raboty.setText(firstJob);
        }
    }


    // сохранение файла
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveText(View view) {
        // Убираю клавиатуру
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Note note = new Note();

        if (check_field()) {
            note.setData(et_data.getText().toString());

            if (rbZN.isChecked())
                note.setZak(rbZN.getText().toString());
            else note.setZak(rbZAP.getText().toString());
            note.setZak(note.getZak() + et_zakaz_naryad.getText().toString());

            double int_chasy = 0;
            String chasy = tv_raboty.getText().toString() + et_normo_chasy.getText().toString();
            Log.d(LOG_TAG, chasy);
            chasy = chasy.replaceAll(",", ".");

            String[] summa_chasov = chasy.split(" "); // делю строку любыми символами кроме цифр
            for (String s : summa_chasov) {
                Log.d(LOG_TAG, s);
                if (s.contains("."))
                    int_chasy += Double.parseDouble(s);
            }
            note.setChas(int_chasy);
           if (isNoteChanged) {
               db.updateNote(note);
               isNoteChanged = FALSE;
           }
                else db.insertNote(note);

            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
            clearField();
            openText(view);

        }

    }

    // открытие файла
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openText(View view) {
        ExpListAdapter adapter2 = new ExpListAdapter(getApplicationContext(), db); //.getReadableDatabase());
        listView.setAdapter(adapter2);
        List<Note> notes = db.getAllNotes();
        for (int i = 0; i < db.getNotesCount(); i++) {
            Log.d(LOG_TAG,
                    "id = " + notes.get(i).getId() +
                            ", Дата = " + notes.get(i).getData() +
                            ", Заказ_наряд = " + notes.get(i).getZak() +
                            ", Часы = " + notes.get(i).getChas()
            );
        }
    }

//------------------ Удаление файла ---------------------
    public void clearText(View view) {
        Log.d(LOG_TAG, "--- Clear mytable: ---");
        db.deleteAllNotes();
        clearField();
    }

    // очистка полей ввода
    public void clearField() {
        et_zakaz_naryad.setText("");
        et_zakaz_naryad.requestFocus();
        et_normo_chasy.setText(NULLS);
        tv_raboty.setText(VSEGO_CHASOV);
        rbZN.setChecked(TRUE);
        len_zakaz_naryad = 5;
        Date currentDate = new Date();
        data_set(currentDate);
    }

    private int check_len_zn(RadioButton rb) {
        if (rb.isChecked()) {
            String str = rb.getText().toString();
            return str.length() - str.indexOf('-');
        }
        return 0;
    }

    public int set_len_zn() {
        return LEN_ZAKAZ_NAR - check_len_zn(rbZAP) - check_len_zn(rbZN);
    }

    public void set_short_len(View view) {
        check_text_zn(et_zakaz_naryad.getText());
    };

    // проверка, что поля не пустые
    public boolean check_field() {

        if (!check_text_zn(et_zakaz_naryad.getText())) {
            focus_keyboard(et_zakaz_naryad, " Введи номер заказ-наряда");
            return FALSE;
        }

        Value = et_normo_chasy.getText().toString() + tv_raboty.getText().toString();
        if (Value.length() < 2) {
            focus_keyboard(et_normo_chasy, "Введи часы за работу");
            return FALSE;
        }

        return TRUE;
    }

    public void focus_keyboard(EditText editText, String str) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    // отображаем диалоговое окно для выбора даты
    public void setDate(View view) {
        new DatePickerDialog(MainActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            data_set(dateAndTime.getTime());
        }
    };

    @SuppressLint("SimpleDateFormat")
    public void data_set(Date date) {
        //         Форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(date);
        et_data.setText(dateText);
         dateFormat = new SimpleDateFormat("LLLL yyyy ");
        String month = dateFormat.format(date);
        Objects.requireNonNull(getSupportActionBar()).hide();
        month = month.substring(0,1).toUpperCase() + month.substring(1).toLowerCase();
        month +=  " - ";
        String summa_chasov =  file_read(date);
        Spannable text = new SpannableString( month + summa_chasov + "н/ч ");
        //text.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple_200)), 14, 14 + summa_chasov.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new StyleSpan(Typeface.BOLD), month.length(), month.length() + summa_chasov.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_header.setText(text);

    }

    ;

    // построчное считывание файла
    @SuppressLint("DefaultLocale")
    public String file_read(Date date) {
    double sumChas = 0.0;
        for (Note chas : db.getAllNotes() ) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
         try {
             Date dateZak = df.parse(chas.getData());
             assert date != null;
             assert dateZak != null;
             if (dateZak.getMonth() == date.getMonth() &&
                         dateZak.getYear() == date.getYear()) {
                         sumChas += chas.getChas();
                 }
             } catch (ParseException pe) {
                    // не получилось
                    Log.d(LOG_TAG, chas.getData() + "-------" + pe );
             }
        }

        return String.format("%.2f",sumChas);

    }
}