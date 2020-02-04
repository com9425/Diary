package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button btnSave;
    DatePicker dPicker;
    EditText edtDiary;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dPicker = (DatePicker) findViewById(R.id.dPicker);
        edtDiary = (EditText) findViewById(R.id.edtDiary);
        btnSave = (Button) findViewById(R.id.btnSave);
        Calendar cal = Calendar.getInstance();// 폰에 저장돼있는 오늘 날짜

        int cYear = cal.get(Calendar.YEAR);
        int cMonth = cal.get(Calendar.MONTH);
        int cday = cal.get(Calendar.DAY_OF_MONTH);
        fileName=cYear+"_"+(cMonth+1)+"_"+cday+".txt";
        edtDiary.setText(readDiary(fileName));
        dPicker.init(cYear, cMonth, cday, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fileName = year + "_" + (monthOfYear + 1) + "_" + dayOfMonth + ".txt";
                edtDiary.setText(readDiary(fileName));


            }
        });// 이앱ㅇ을 실행시킬때마다 초기화
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    FileOutputStream fileos = openFileOutput(fileName, Context.MODE_PRIVATE);// 파일이름 쓰면 계속 덮어씌어져서 하나만남음 그래서 날짜마다 이름저잦ㅇ하는 fileName
                    String str = edtDiary.getText().toString();
                    fileos.write(str.getBytes());
                    fileos.close();
                    showToast(fileName + "파일이 저장되었습니다");
                } catch (IOException e) {
                    showToast("파일을 저장할 수 없습니다.");
                }

            }
        });

    }

    void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    //일기를 읽어서 처리하는 메서드
    String readDiary(String fileName) {
        String diaryStr = null;

        try {
            FileInputStream fileis = openFileInput(fileName);
            byte txt[] = new byte[fileis.available()];
            fileis.read(txt);
            diaryStr = (new String(txt).trim());
            fileis.close();
            btnSave.setText("수정하기");
        } catch (IOException e) {
            edtDiary.setHint("일기 없음");
            btnSave.setText("새로 저장");
        }
            return diaryStr;


    }
}

