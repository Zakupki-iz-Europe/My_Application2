package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;




public class MainActivity extends AppCompatActivity {

    private final static String FILE_NAME = "content.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    // сохранение файла
    public void saveText(View view){

        FileOutputStream fos = null;
        try {
            EditText textBox = (EditText) findViewById(R.id.zakaz_naryad);
            String text = textBox.getText().toString() + ' ';

            textBox = (EditText) findViewById(R.id.normochasy);
            text = text + textBox.getText().toString() + ' ';

            textBox = (EditText) findViewById(R.id.data);
            text = text + textBox.getText().toString() + "\r\n";

            clearField();

            fos = openFileOutput(FILE_NAME, MODE_APPEND);
            fos.write(text.getBytes());
            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
        }
        catch(IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    // открытие файла
    public void openText(View view){

        FileInputStream fin = null;
        TextView textView = (TextView) findViewById(R.id.open_text);
        try {
            fin = openFileInput(FILE_NAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
            textView.setText(text);
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
        FileInputStream fin = null;
        TextView textView = (TextView) findViewById(R.id.open_text);
        try {
            deleteFile(FILE_NAME);
            textView.setText(null);
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
        ((EditText) findViewById(R.id.zakaz_naryad)).setText("");
        ((EditText) findViewById(R.id.normochasy)).setText("");
        ((EditText) findViewById(R.id.data)).setText("");
    }
}