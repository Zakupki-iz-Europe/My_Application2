package com.example.myapplication2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.annotation.RequiresApi;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;


public class MainActivity extends AppCompatActivity
        implements YesNoDialogFragment.YesNoDialogFragmentListener {

//    -------------------------начало списка1------------------
    //https://github.com/ravi8x/AndroidSQLite.git
    //----------------------------------конец списка1-------------------

    private final static String NULLS = "00,00";
    private final static String VSEGO_CHASOV = "Всего часов: ";
    private final static Double MAX_CHASOV = 99.99;
    private final Map<String, String> prefix = new HashMap<>();
    static int LEN_ZAKAZ_NAR = 7;
    int len_zakaz_naryad;
    char razdelitel = ',';
    final String LOG_TAG = "myLogs";
    public DatabaseHelper db;
    boolean isNoteChanged = FALSE;
    Note noteForChange = new Note();
    ArrayList<Note> notesForDel = new ArrayList<Note>();
    Calendar dateAndTime = Calendar.getInstance();
    ColorStateList oldColors;
    ExpListAdapter adapter2;
    RadioButton rbZN, rbZAP, rbZDOP;
    EditText et_zakaz_naryad ,
            et_normo_chasy ;
    TextView et_data,
            tv_header,
            tv_raboty;
    ExpandableListView listView ;
    FragmentManager fragmentManager = this.getSupportFragmentManager();


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
        rbZN = findViewById(R.id.radioButton);
        rbZAP = findViewById(R.id.radioButton2);
       rbZDOP = findViewById(R.id.radioButton3);
        et_zakaz_naryad = findViewById(R.id.zakaz_naryad);
        et_normo_chasy = findViewById(R.id.normochasy);
        et_data = findViewById(R.id.currentDateTime);
        tv_header = findViewById(R.id.header);
        tv_raboty = findViewById(R.id.tv_chasy);
        listView = findViewById(R.id.exListView);
        db = new DatabaseHelper(this);
        prefix.put(getString(R.string._36), NULLS);
        prefix.put(getString(R.string._116), "00,30");
        prefix.put(getString(R.string._dop), NULLS);


//        https://maxfad.ru/programmer/android/252-sozdanie-spiska-listview-i-arrayadapter-v-android-studio.html
        clearField();
        openText(et_data);
        ColorStateList oldColors_background =  rbZN.getLinkTextColors();
        oldColors =  et_zakaz_naryad.getTextColors(); //save original colors
        findViewById(R.id.divider).setBackgroundColor(oldColors_background.getDefaultColor());

        // ---------------Отработка коротких нажатий по списку ------------------------------------
        listView.setOnChildClickListener((ExpandableListView.OnChildClickListener)
                (parent, v, groupPosition, childPosition, id) -> {
                    isNoteChanged = TRUE;
                    noteForChange = (Note) adapter2.getChild(groupPosition, childPosition);
                    Log.d("onChildClick",
                            "id = " + noteForChange.getId() +
                                    ", Дата = " + noteForChange.getData() +
                                    ", Заказ_наряд = " + noteForChange.getZak() +
                                    ", Часы = " + noteForChange.getChas()
                    );
                    String zn;
                    clearField();
                    et_data.setText(noteForChange.getData());
                    if (noteForChange.getZak().contains(rbZAP.getText().
                            subSequence(0, rbZAP.getText().length()))) {
                        rbZAP.setChecked(TRUE);
                    }
//                zn = noteForChange.getZak().substring(rbZAP.getText().length());}
                    else if (noteForChange.getZak().contains(rbZDOP.getText().
                            subSequence(0, rbZDOP.getText().length()))) {
                        rbZDOP.setChecked(TRUE);
                    }
//                zn = noteForChange.getZak().substring(rbZN.getText().length());}
                    else rbZN.setChecked(TRUE);

                    et_zakaz_naryad.setText(noteForChange.getZak());
                    et_normo_chasy.setText(format(Locale.getDefault(), "%05.2f", noteForChange.getChas()));

                    return FALSE;
                });

        // ---------------Отработка долгих нажатий по списку -- удаление дня или записи ----------------------------------
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);
                    // Create YesNoDialogFragment
                    DialogFragment dialogFragment = new YesNoDialogFragment();
                    // Arguments:
                    Bundle args = new Bundle();
                    args.putString(YesNoDialogFragment.ARG_MESSAGE, "Удалить этот заказ?");
                    dialogFragment.setArguments(args);
                    notesForDel.add(adapter2.getChild(groupPosition,childPosition));
                    // Show:
                    dialogFragment.show(fragmentManager, "Dialog");
                    return true;
                }
               if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    // Create YesNoDialogFragment
                    DialogFragment dialogFragment = new YesNoDialogFragment();
                    // Arguments:
                    Bundle args = new Bundle();
                    args.putString(YesNoDialogFragment.ARG_MESSAGE, "Удалить заказы этого дня?");
                    dialogFragment.setArguments(args);
                   int child = 0;
                   Log.d(LOG_TAG, adapter2.getChildrenCount(groupPosition) + "+++++++++++"  );

                   while (child < adapter2.getChildrenCount(groupPosition)) {
                       Log.d(LOG_TAG, child + "-------"  );

                       notesForDel.add(adapter2.getChild(groupPosition,child));
                       child++;
                   }
                   // Show:
                    dialogFragment.show(fragmentManager, "Dialog");
                    return true;
                }
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
        Log.d(LOG_TAG, "Длина зн ------   " + len_zakaz_naryad) ;
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
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof YesNoDialogFragment) {
            YesNoDialogFragment yesNoDialogFragment = (YesNoDialogFragment) fragment;
            yesNoDialogFragment.setOnYesNoDialogFragmentListener(this);
        }
    }

    // Implement method of YesNoDialogFragment.YesNoDialogFragmentListener
    @Override
    public void onYesNoResultDialog(int resultCode) {
        if(resultCode == Activity.RESULT_OK) {
            int counter = 0;
             if (notesForDel!=null) {
                 for (Note note: notesForDel) {
                db.deleteNote(note);
                counter++;}
            }
//             counter--;
            Toast.makeText(this, "Удалено записей: " + counter, Toast.LENGTH_SHORT).show();
            notesForDel.clear();
            openText(et_zakaz_naryad);
            data_set(dateAndTime.getTime());
        } else {
            notesForDel.clear();
            this.tv_header.setText("You don't select");
        }
    }



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
                    s.replace(0,s.length(),strng);
                }
                else if (s.charAt(0) != '0') addJob(et_normo_chasy);

            }
        }
    }

    // ------------------------------ Кнопка добавить работу в строку ВСЕГО РАБОТ-------
    public void addJob(View view) {
        String raboty = tv_raboty.getText().toString();
        String chasyki = et_normo_chasy.getText().toString();
        if (!chasyki.equals(NULLS)) {
            String firstJob = raboty + chasyki;
            et_normo_chasy.setText(NULLS);
            et_normo_chasy.setSelection(NULLS.length());
            firstJob += " + ";
            tv_raboty.setText(firstJob);
        }
    }


    // сохранение файла
    public void saveText(View view) {
        // Убираю клавиатуру
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Note note = new Note();

        if (check_field()) {
            note.setData(et_data.getText().toString());

//            if (rbZN.isChecked())
//                note.setZak(rbZN.getText().toString());
//            else note.setZak(rbZAP.getText().toString());
//
//            note.setZak(note.getZak() + et_zakaz_naryad.getText().toString());

            note.setZak(et_zakaz_naryad.getText().toString());

            note.setChas(Double.parseDouble(tv_raboty.getText().toString().replace(",", ".")));

            if (isNoteChanged) {
               note.setId(noteForChange.getId());
               db.updateNote(note);
               isNoteChanged = FALSE;
               noteForChange = null;
           }
                else db.insertNote(note);

            clearField();
            openText(view);
            listView.expandGroup(adapter2.getGroupNom(note));
        }

    }

    // открытие файла
    public void openText(View view) {
      adapter2 = new ExpListAdapter(getApplicationContext(), db); //.getReadableDatabase());
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
        db.deleteAllNotes();
        clearField();
        openText(view);
    }

    // очистка полей ввода
    public void clearField() {
        rbZN.setChecked(TRUE);

        et_zakaz_naryad.setText(rbZN.getText());
        et_zakaz_naryad.requestFocus();
        et_normo_chasy.setText(NULLS);
        tv_raboty.setText(VSEGO_CHASOV);
        Date currentDate = new Date();
        data_set(currentDate);
    }

    private int check_len_zn(RadioButton rb) {
        if (rb.isChecked()) {
            String str = rb.getText().toString();
            et_normo_chasy.setText(prefix.get(str));
//            et_zakaz_naryad.setText(str);
//            return str.length() - (str.indexOf('-') + 1);
            Log.d(LOG_TAG, " Префикс --- " + str.indexOf('-'));
            return str.indexOf('-');
        }
        return 0;
    }

    public int set_len_zn() {
        return LEN_ZAKAZ_NAR + check_len_zn(rbZAP) + check_len_zn(rbZN) + check_len_zn(rbZDOP);
    }

    public void set_short_len(View view) {
        check_text_zn(et_zakaz_naryad.getText());

    }

    // проверка, что поля не пустые
   public boolean check_field() {
    String chasy;
    String[] summa_chasov;
    double int_chasy = 0.0;

    chasy= format("%s%s", tv_raboty.getText().toString(), et_normo_chasy.getText().toString());
    chasy = chasy.replaceAll(",", ".");
    summa_chasov= chasy.split(" "); // делю строку любыми символами кроме цифр

    for (String s : summa_chasov)
        if (s.contains("."))
            int_chasy += Double.parseDouble(s);


    if (int_chasy == 0.0) {
        focus_keyboard(et_normo_chasy, "Введи часы за работу");
        return FALSE;
    }

    if (int_chasy > MAX_CHASOV) {
        int_chasy -= MAX_CHASOV;
        focus_keyboard(et_normo_chasy, "Братан, это круто, но в одном заказе не больше " + MAX_CHASOV + " часов");
        String s = VSEGO_CHASOV + String.format(Locale.getDefault(),"%05.2f",int_chasy) + " + ";
        tv_raboty.setText(s);
        return FALSE;
    }

    if (!check_text_zn(et_zakaz_naryad.getText())) {
         focus_keyboard(et_zakaz_naryad, " Введи номер заказ-наряда");
         return FALSE;
    }

    Toast.makeText(this, "Запись сохранена", Toast.LENGTH_SHORT).show();
    tv_raboty.setText(Double.toString(int_chasy));
    return TRUE;
   }

    public void focus_keyboard(EditText editText, String str) {
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
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
    DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
        dateAndTime.set(Calendar.YEAR, year);
        dateAndTime.set(Calendar.MONTH, monthOfYear);
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        data_set(dateAndTime.getTime());
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



    // построчное считывание файла
    @SuppressLint("DefaultLocale")
    public String file_read(Date date) {
    double sumChas = 0.0;
        for (Note chas : db.getAllNotes() ) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
         try {
             Date dateZak = df.parse(chas.getData());
             if (date != null) {
                 if (dateZak == null) throw new AssertionError();
                 if (dateZak.getYear() == date.getYear())
                     if (dateZak.getMonth() == date.getMonth()) {
                         sumChas += chas.getChas();
                     }
             } else {
                 throw new AssertionError();
             }
         } catch (ParseException pe) {
                    // не получилось
                    Log.d(LOG_TAG, chas.getData() + "-------" + pe );
             }
        }

        return format("%.2f",sumChas);

    }
}