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
    EditText titleText;
    EditText contentsText;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    Intent intent1;
    int list_id;
    String list_title, list_contents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        titleText = (EditText) findViewById(R.id.titleText);
        contentsText = (EditText) findViewById(R.id.contentsText);


        //아이템 클릭 시 전달된 데이터
        intent1 = getIntent();
        String type = intent1.getStringExtra("type");

        TextView saveBtn = (TextView)findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this::onClickBtn);
        TextView modifyBtn = (TextView)findViewById(R.id.modifyBtn);
        modifyBtn.setOnClickListener(this::onClickBtn);
        TextView deleteBtn = (TextView)findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(this::onClickBtn);

        if(type.equals("insert")){

            modifyBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);

        }else if(type.equals("update")){

            saveBtn.setVisibility(View.INVISIBLE);

            list_id = intent1.getIntExtra("list_id", 0);
            list_title = intent1.getStringExtra("list_title");
            list_contents = intent1.getStringExtra("list_contents");

            // 전달 받은 데이터 textView에 출력
            titleText.setText(list_title);
            contentsText.setText(list_contents);

        }



    }

    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.saveBtn:
                insert();
                break;
            case R.id.deleteBtn:
                delete(list_id);
                break;
            case R.id.modifyBtn:
                modify(list_id);
                break;
            case R.id.backBtn:
                finish();
        }
    }


    private void insert() {
        String title = titleText.getText().toString();
        String contents = contentsText.getText().toString();

        println("insert 호출됨");

        if (database == null) {
            println("데이터베이스를 먼저 생성");
            return;
        }

        if (title.length() > 0 && contents.length() > 0) { //입력 값이 있을 때
            database.execSQL("insert into " + DatabaseHelper.TABLE_NAME +
                    "(title, contents) values(" +
                    "'" + title + "', " +
                    "'" + contents + "')");

            Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_LONG).show();


            setResult(RESULT_OK);

            finish();

        } else { // 입력 값이 없을 때
            Toast.makeText(this, "내용을 입력하시오.", Toast.LENGTH_LONG).show();

        }


    }

    private void modify(int list_id) {
        String modify_title = titleText.getText().toString();
        String modify_contents = contentsText.getText().toString();

        if(modify_title.length() > 0 && modify_contents.length() > 0){// 입력 값이 있을 때
            String sql = "UPDATE " + DatabaseHelper.TABLE_NAME
                    + " SET "
                    + " title = '" + modify_title + "'"
                    + " ,contents = '" + modify_contents + "'"
                    + " WHERE "
                    + " _id = " + list_id;

            database.execSQL(sql);

            Toast.makeText(getApplicationContext(), "수정 완료. ", Toast.LENGTH_LONG).show();

            setResult(RESULT_OK);
            finish();
        }else{//입력 값이 없을 때
            Toast.makeText(this, "내용을 입력하시오.", Toast.LENGTH_LONG).show();
        }



    }


    private void delete(int list_id) {
        String sql = "DELETE FROM " + DatabaseHelper.TABLE_NAME
                + " WHERE "
                + " _id = " + list_id;

        database.execSQL(sql);

        Log.d("list_id", String.valueOf(list_id));

        Toast.makeText(getApplicationContext(), "삭제 완료. ", Toast.LENGTH_LONG).show();

        setResult(RESULT_OK);
        finish();


    }

    public void println(String data) {
        Log.d("data", data);
    }
}