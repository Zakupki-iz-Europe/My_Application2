package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class MainActivity extends AppCompatActivity {

    private final static String FILE_NAME = "content.txt";
    int len_zakaz_naryad = 5;

    RadioButton rbZN, rbZNPP;
    EditText    et_zakaz_naryad,
                et_normo_chasy,
                et_data;
    TextView tv_open_text;
    String Value;
    TextView currentDateTime;
    Calendar dateAndTime=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_zakaz_naryad = findViewById(R.id.zakaz_naryad);
        et_normo_chasy = findViewById(R.id.normochasy);
        et_data = findViewById(R.id.data);
        currentDateTime = findViewById(R.id.currentDateTime);
        clearField();

        et_zakaz_naryad.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

                Value = et_zakaz_naryad.getText().toString();

               if (Value.length() == len_zakaz_naryad) {
                   et_zakaz_naryad.setTextColor(Color.BLUE);
                   et_normo_chasy.requestFocus();}
               else et_zakaz_naryad.setTextColor(Color.RED);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }




    // сохранение файла
    public void saveText(View view) {
//        et_zakaz_naryad = findViewById(R.id.zakaz_naryad);
//        et_normo_chasy = findViewById(R.id.normochasy);
//        et_data = findViewById(R.id.data);
        rbZN = findViewById(R.id.radioButton);
        rbZNPP = findViewById(R.id.radioButton2);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        FileOutputStream fos = null;

        if (check_field()) {
            try {
               String text = "Дата " + et_data.getText().toString() + " ";

               if (rbZN.isChecked())
                    text = text + rbZN.getText().toString();
               else text = text + rbZNPP.getText().toString();

               text = text + et_zakaz_naryad.getText().toString() + " "
                        + et_normo_chasy.getText().toString() + "н/ч\r\n";

               clearField();

               fos = openFileOutput(FILE_NAME, MODE_APPEND);
               fos.write(text.getBytes());
               Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException ex) {

                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    // открытие файла
    public void openText(View view){

        tv_open_text = (TextView) findViewById(R.id.open_text);

        FileInputStream fin = null;
        try {
            fin = openFileInput(FILE_NAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
            tv_open_text.setText(text);
        }
        catch(IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{

            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    // удаление файла
    public void clearText(View view){

        tv_open_text = (TextView) findViewById(R.id.open_text);

        FileInputStream fin = null;
        try {
            deleteFile(FILE_NAME);
            tv_open_text.setText(null);
            clearField();
        } finally{

            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    // очистка полей ввода
    public void clearField() {
        et_zakaz_naryad = findViewById(R.id.zakaz_naryad);
        et_normo_chasy = findViewById(R.id.normochasy);
        et_data = findViewById(R.id.data);

        et_zakaz_naryad.setText("");
        et_normo_chasy.setText("");

        // Текущее время
        Date currentDate = new Date();
        // Форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        et_data.setText(dateText);
    }

    // проверка, что поля не пустые
    public boolean check_field(){
        et_zakaz_naryad = findViewById(R.id.zakaz_naryad);
        et_normo_chasy = findViewById(R.id.normochasy);
        et_data = findViewById(R.id.data);


        String text = et_zakaz_naryad.getText().toString();
        if (text.length() != len_zakaz_naryad) {
            focus_keyboard(et_zakaz_naryad," Введи номер заказ-наряда - 5 цифр");
            return FALSE;
         }

        text = et_normo_chasy.getText().toString();
        if (text.length() < 2) {
            focus_keyboard(et_normo_chasy,"Введи часы за работу, помноженные на 100, т.е. 0.5 н/ч = 50");
            return FALSE;
        }

        text = et_data.getText().toString();
        if (text.length() < 2) {
            focus_keyboard(et_data,"Введи число");
            return FALSE;
        }

    return TRUE;
    }

    public void focus_keyboard(EditText editText, String str){
       editText.requestFocus();
       InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
       imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
       Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(MainActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            currentDateTime.setText(DateUtils.formatDateTime( MainActivity.this,dateAndTime.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                            | DateUtils.FORMAT_SHOW_TIME));
        }
    };
}