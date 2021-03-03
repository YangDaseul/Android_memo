package com.example.memo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {
    ArrayList<MemoItem> items = new ArrayList<MemoItem>();
    EditText titleText;
    EditText contentsText;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    Intent intent1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();
        database = dbHelper.getWritableDatabase();

        titleText = (EditText) findViewById(R.id.titleText);
        contentsText = (EditText) findViewById(R.id.contentsText);

        //뒤로가기
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        TextView saveBtn = (TextView) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insert();

            }
        });

        //아이템 클릭 시 전달된 데이터
        intent1 = getIntent();
        int list_id = intent1.getIntExtra("list_id", 0);
        String list_title = intent1.getStringExtra("list_title");
        String list_contents = intent1.getStringExtra("list_contents");


        // 전달 받은 데이터 textView에 출력
        titleText.setText(list_title);
        contentsText.setText(list_contents);


        //삭제 버튼
        TextView deleteBtn = (TextView)findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(list_id);
            }
        });

        
        // 수정 버튼
        TextView modifyBtn = (TextView) findViewById(R.id.modifyBtn);
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modify(list_id);
            }
        });

    }


    private void insert(){
        String title = titleText.getText().toString();
        String contents = contentsText.getText().toString();

        println("insert 호출됨");

        if (database == null) {
            println("데이터베이스를 먼저 생성");
            return;
        }

        database.execSQL("insert into " + DatabaseHelper.TABLE_NAME +
                "(title, contents) values(" +
                "'" + title + "', " +
                "'" + contents + "')");

        Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_LONG).show();


        Intent intent = getIntent();
        intent.putExtra("title", title);
        intent.putExtra("contents",contents);
        setResult(RESULT_OK, intent);

        finish();


    }

    private void modify(int list_id){
        String modify_title = titleText.getText().toString();
        String modify_contents = contentsText.getText().toString();

        String sql = "UPDATE " + DatabaseHelper.TABLE_NAME
                + " SET "
                + " title = '" + modify_title + "'"
                + " ,contents = '" + modify_contents + "'"
                + " WHERE "
                + " _id = " + list_id;

        database.execSQL(sql);

        Toast.makeText(getApplicationContext(), "수정 완료. ", Toast.LENGTH_LONG).show();

        setResult(RESULT_OK, intent1);
        finish();

    }



    private void delete(int list_id){
        String sql = "DELETE FROM " + DatabaseHelper.TABLE_NAME
                + " WHERE "
                + " _id = " + list_id;

        database.execSQL(sql);

        Log.d("list_id", String.valueOf(list_id));

        Toast.makeText(getApplicationContext(), "삭제 완료. ", Toast.LENGTH_LONG).show();

        setResult(RESULT_OK, intent1);
        finish();



    }
    public void println(String data) {
        Log.d("data", data);
    }
}