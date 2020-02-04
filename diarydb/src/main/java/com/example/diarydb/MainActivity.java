package com.example.diarydb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    DatePicker dPicker;
    EditText edtDiary;
    Button btnSave;
    String fileName;
    int cYear, cMonth, cDay;
    MyDBHelper myDBHelper;
    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dPicker=(DatePicker)findViewById(R.id.dPicker);
        edtDiary=(EditText)findViewById(R.id.edtDiary);
        btnSave=(Button)findViewById(R.id.btnSave);
        myDBHelper=new MyDBHelper(this);
        Calendar cal=Calendar.getInstance();
        cYear=cal.get(Calendar.YEAR);
        cMonth=cal.get(Calendar.MONTH);
        cDay=cal.get(Calendar.DAY_OF_MONTH);
        fileName=cYear + "_" + (cMonth+1) + "_" + cDay;
        edtDiary.setText(readyDiary(fileName));
        dPicker.init(cYear, cMonth, cDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fileName=year + "_" + (monthOfYear+1) + "_" + dayOfMonth;
                edtDiary.setText(readyDiary(fileName));
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlDB=myDBHelper.getWritableDatabase();
                if(btnSave.getText().toString().equals("새로 저장")) {
                    sqlDB.execSQL("INSERT INTO myDiary VALUES('" + fileName + "','" +
                            edtDiary.getText().toString() + "');");
                    showToast("일기가 저장되었습니다.");
                }else {
                    sqlDB.execSQL("UPDATE myDiary SET content ='" + edtDiary.getText().toString() +
                            "' WHERE diaryDate ='" + fileName + "';");
                    showToast("일기가 수정되었습니다.");
                }
                sqlDB.close();
            }
        });
    }
    //토스트 메서드
    void showToast(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
    //일기를 읽어서 처리하는 메서드
    String readyDiary(String fileName) {
        String diaryStr=null;
        sqlDB=myDBHelper.getReadableDatabase();
        String sql="SELECT * FROM myDiary WHERE diaryDate = '" + fileName + "';";
        Cursor cursor;
        cursor=sqlDB.rawQuery(sql,null);
        Log.d("SQL_statement", sql);
        if(cursor==null) {
            edtDiary.setHint("일기없음");
            btnSave.setText("새로 저장");
        }else if(cursor.moveToFirst()) {
            diaryStr=cursor.getString(1);
            btnSave.setText("수정하기");
        }else {
            edtDiary.setHint("일기없음");
            btnSave.setText("새로 저장");
        }
        cursor.close();
        sqlDB.close();
        return diaryStr;
    }

    public class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE myDiary (diaryDate TEXT PRIMARY KEY, content TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
