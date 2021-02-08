package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class MainActivity extends AppCompatActivity {

    private final static String FILE_NAME = "content.txt";
    int len_zakaz_naryad = 5;
    char razdelitel = ',';

    RadioButton rbZN, rbZNPP;
    EditText    et_zakaz_naryad,
                et_normo_chasy;
    TextView tv_open_text,
            et_data;
    String Value;

    Calendar dateAndTime=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_zakaz_naryad = findViewById(R.id.zakaz_naryad);
        et_normo_chasy = findViewById(R.id.normochasy);
        et_data = findViewById(R.id.currentDateTime);
        rbZN = findViewById(R.id.radioButton);
        rbZNPP = findViewById(R.id.radioButton2);

        clearField();

        et_zakaz_naryad.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

                Value = et_zakaz_naryad.getText().toString();

               if (Value.length() < len_zakaz_naryad + 1) et_zakaz_naryad.setTextColor(Color.RED);
               else {
                    et_zakaz_naryad.setText(Value.substring(0, len_zakaz_naryad));
                    et_zakaz_naryad.setTextColor(Color.WHITE);
                    et_normo_chasy.requestFocus();}
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
        et_normo_chasy.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

                Value = et_normo_chasy.getText().toString();
//                System.out.println(s + " " + Value + " " + Value.substring(0, Value.length() - 2) );

                if (!Character.isDigit(Value.charAt(Value.length() - 1))) {
                    et_normo_chasy.setText(Value.substring(0, Value.length() - 2));
                    System.out.println(s + " " + Value );
//https://ru.stackoverflow.com/questions/554110/Как-в-edittext-отделить-разряды/554280#554280
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });

    }




    // сохранение файла
    public void saveText(View view) {
        // Убираю клавиатуру
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        FileOutputStream fos = null;

        if (check_field()) {
            try {
               String text = et_data.getText().toString() + razdelitel;

               if (rbZN.isChecked())
                    text = text + rbZN.getText().toString();
               else text = text + rbZNPP.getText().toString();

               text = text + et_zakaz_naryad.getText().toString() + razdelitel;

               double int_chasy = 0;
               String chasy = et_normo_chasy.getText().toString();
               String[] summa_chasov = chasy.split("\\D"); // делю строку любыми символами кроме цифр
               for (int i=0; i<summa_chasov.length; i++){int_chasy += Double.parseDouble(summa_chasov[i]);};

               text = text + int_chasy / 100 + "н/ч\r\n";

               fos = openFileOutput(FILE_NAME, MODE_APPEND);
               fos.write(text.getBytes());
               Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();

                clearField();

            } catch (IOException ex) {

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                try {if (fos!=null) fos.close();}
                 catch (IOException ex) {

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

            try {
                if (fin != null) {
                    fin.close();
                }
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    // очистка полей ввода
    public void clearField() {
        et_zakaz_naryad.setText("");
        et_zakaz_naryad.requestFocus();
        et_normo_chasy.setText("");
        rbZN.setChecked(TRUE);
        len_zakaz_naryad = 5;
        Date currentDate = new Date();
        data_set(currentDate);
    }

    public void set_short_len(View view){
        len_zakaz_naryad = 4;
        if (et_zakaz_naryad.getText().toString().length() > len_zakaz_naryad + 1)
            et_zakaz_naryad.setText(Value.substring(0, len_zakaz_naryad));
// если уже набрали больше символов, то оставляем по длине
    }

    public void set_long_len(View view){
        len_zakaz_naryad = 5;
    }

    // проверка, что поля не пустые
    public boolean check_field(){
        String text = et_zakaz_naryad.getText().toString();
        if (text.length() != len_zakaz_naryad) {
            focus_keyboard(et_zakaz_naryad," Введи номер заказ-наряда");
            return FALSE;
         }

        text = et_normo_chasy.getText().toString();
        if (text.length() < 2) {
            focus_keyboard(et_normo_chasy,"Введи часы за работу, помноженные на 100, т.е. 0.5 н/ч = 50");
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
    public void setDate(View view) {
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
            data_set(dateAndTime.getTime());
        }
    };

    public void data_set(Date date){
        //         Форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(date);
        et_data.setText(dateText);
        getSupportActionBar().setTitle("На " + dateText + " " + file_read() + "н/ч ");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff00FFED));
    };

            // построчное считывание файла
        public String file_read() {
            double chasy = 0;
            int begin_of_nch, end_of_nch;
            String line, hours;
            try {
                FileInputStream is = openFileInput(FILE_NAME);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                // read form the stream to build the media list
                ArrayList<String> lstMedia = new ArrayList<String>();

                while ((line = br.readLine()) != null) {
                    if (!line.isEmpty()) {
                        lstMedia.add(new String(line));

                        begin_of_nch = line.lastIndexOf(razdelitel) + 1;
                        end_of_nch = line.length() - 3;
                        hours = line.substring(begin_of_nch,end_of_nch);
                        System.out.println(line + " " + hours);
                       chasy += Double.parseDouble(hours);
                     }
                }

                // clean up
                br.close();
                isr.close();
                is.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return Double.toString(chasy);
        };


}