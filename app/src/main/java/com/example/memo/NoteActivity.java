package com.example.memo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {
    EditText titleText, contentsText;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    Intent intent1;
    int list_id;
    String list_title, list_contents, picturePath;

    TextView saveBtn, deleteBtn, dateTextView;

    //현재 날짜 출력하기
    long now;
    Date date;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm"); // date 출력 형식

    ImageView imageView;
    ImageButton image_add, image_delete;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();


        contentsText = (EditText) findViewById(R.id.contentsText);

        deleteBtn = (TextView) findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(this::onClickBtn);

        image_add = (ImageButton) findViewById(R.id.image_add);
        image_add.setOnClickListener(this::onClickBtn);

        image_delete = (ImageButton) findViewById(R.id.image_delete);
        image_delete.setOnClickListener(this::onClickBtn);
        image_delete.setVisibility(View.INVISIBLE);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);


        //아이템 클릭 시 전달된 데이터
        intent1 = getIntent();
        String type = intent1.getStringExtra("type");

        list_id = intent1.getIntExtra("list_id", 0);
        list_title = intent1.getStringExtra("list_title");
        list_contents = intent1.getStringExtra("list_contents");

        // 전달 받은 데이터 textView에 출력
        contentsText.setText(list_contents);

        saveBtn = (TextView) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() { // 완료 버튼 눌렀을 때
            @Override
            public void onClick(View v) {
                if (type.equals("insert")) { // 글 작성 버튼 눌렀을 때
                    insert();
                } else if (type.equals("update")) { // 아이템 클릭하여 수정할 때
                    modify(list_id);
                }
            }
        });

        //날짜 출력하기
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        dateTextView.setText(getTime());


    }


    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.deleteBtn:
                delete(list_id);
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.image_add:
                openGallery();
                imageView.setVisibility(View.VISIBLE);
                break;
            case R.id.image_delete:
                imageDelete();
                imageView.setVisibility(View.GONE);
                break;

        }
    }


    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*"); // 모든 종류의 이미지 타입
        intent.setAction(Intent.ACTION_GET_CONTENT); // 이미지 가져오기

        startActivityForResult(intent, 101);

    }


    public void imageDelete() { // 추가한 이미지 삭제 시 photo 아이콘 표시
        imageView.setImageBitmap(null);

        image_delete.setVisibility(View.INVISIBLE);
        image_add.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                ContentResolver resolver = getContentResolver();
                // 실제 파일 경로 구하기
                String[] filePathColumn = {MediaStore.Images.Media.DATA}; // 기기 기본 갤러리 접근
                Cursor cursor = resolver.query(fileUri, filePathColumn, null, null, null);
                cursor.moveToNext();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);

                //Log.d("picturePath1", picturePath);


                cursor.close();

                try {

                    // 선택한 이미지에서 비트맵 생성
                    InputStream inputStream = resolver.openInputStream(fileUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                    // 이미지뷰에 세팅
                    imageView.setImageBitmap(imgBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            image_add.setVisibility(View.INVISIBLE);
            image_delete.setVisibility(View.VISIBLE);
        }
    }


    //현재 날짜 출력
    private String getTime() {
        now = System.currentTimeMillis();
        date = new Date(now);

        return dateFormat.format(date);
    }


    private void insert() {
        String picture = picturePath; // 파일 경로
        String contents = contentsText.getText().toString(); // 메모 내용
        String date = dateTextView.getText().toString(); // 날짜

        //Log.d("picture", picture);
        println("insert 호출됨");

        if (database == null) {
            println("데이터베이스를 먼저 생성");
            return;
        }

        if (picture.length() > 0 || contents.length() > 0) { //입력 값이 있을 때
            database.execSQL("insert into " + DatabaseHelper.TABLE_NAME +
                    "(picture, contents, date) values(" +
                    "'" + picture + "', " +
                    "'" + contents + "', " +
                    "'" + date + "')");

            Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_LONG).show();


            setResult(RESULT_OK);

            finish();

        } else { // 입력 값이 없을 때
            Toast.makeText(this, "내용을 입력하시오.", Toast.LENGTH_LONG).show();

        }


    }

    private void modify(int list_id) {
        String modify_picture = picturePath; // 파일 경로
        String modify_contents = contentsText.getText().toString(); // 메모 내용
        String modify_date = dateTextView.getText().toString(); // 날짜

        if (modify_picture.length() > 0 || modify_contents.length() > 0) {// 입력 값이 있을 때
            String sql = "UPDATE " + DatabaseHelper.TABLE_NAME
                    + " SET "
                    + " picture = '" + modify_picture + "'"
                    + " ,contents = '" + modify_contents + "'"
                    + " ,date = '" + modify_date + "'"
                    + " WHERE "
                    + " _id = " + list_id;

            database.execSQL(sql);

            Toast.makeText(getApplicationContext(), "수정 완료. ", Toast.LENGTH_LONG).show();

            setResult(RESULT_OK);
            finish();
        } else {//입력 값이 없을 때
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