package com.example.memo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.File;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity implements AutoPermissionsListener {
    EditText titleText, contentsText;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    Intent intent1;
    int list_id;
    String list_picture, list_contents, picturePath, modify_picture;

    TextView saveBtn, deleteBtn, dateTextView;

    //현재 날짜 출력하기
    long now;
    Date date;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm"); // date 출력 형식

    ImageView imageView;
    ImageButton image_add, image_delete, camera_icon;

    File file;


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


        camera_icon = (ImageButton) findViewById(R.id.camera_icon);
        camera_icon.setOnClickListener(this::onClickBtn);

        imageView = (ImageView) findViewById(R.id.imageView);


        //아이템 클릭 시 전달된 데이터
        intent1 = getIntent();
        String type = intent1.getStringExtra("type");
        viewType(type);

        list_id = intent1.getIntExtra("list_id", 0);
        list_picture = intent1.getStringExtra("list_picture");
        list_contents = intent1.getStringExtra("list_contents");


        // 전달 받은 데이터 textView에 출력
        contentsText.setText(list_contents);
        // 전달 받은 사진 imageView에 출력
        imageView.setImageBitmap(BitmapFactory.decodeFile(list_picture));


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

        AutoPermissions.Companion.loadAllPermissions(this, 102); // 카메라 연동 권한


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
                break;
            case R.id.image_delete:
                imageDelete();
                break;
            case R.id.camera_icon:
                takePicture();

        }
    }

    public void viewType(String type) {
        if (type.equals("insert")) {
            image_delete.setVisibility(View.GONE);
        } else if (type.equals("modify")) {
            image_delete.setVisibility(View.VISIBLE);


        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // 카메라 연동 권한 요청
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }


    public void openGallery() { // 갤러리 연동
        Intent intent = new Intent();
        intent.setType("image/*"); // 모든 종류의 이미지 타입
        intent.setAction(Intent.ACTION_GET_CONTENT); // 이미지 가져오기

        startActivityForResult(intent, 101);

    }


    public void imageDelete() { // 추가한 이미지 삭제 시 photo 아이콘 표시
        imageView.setImageBitmap(null);

        picturePath = null;

        image_delete.setVisibility(View.GONE);
        image_add.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 갤러리 연동
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {

                ContentResolver resolver = getContentResolver();
                Cursor cursor = resolver.query(data.getData(), null, null, null, null);
                cursor.moveToNext();

                String[] filePathColumn = {MediaStore.MediaColumns.DATA};
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                //String picturePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));  //위 세줄의 한줄 코드

                cursor.close();

                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                imageView.setImageBitmap(bitmap);

            }
            image_add.setVisibility(View.VISIBLE);
            image_delete.setVisibility(View.VISIBLE);

        } else if (requestCode == 102 && resultCode == RESULT_OK) { // 카메라 연동
            // 이미지 파일을 Bitmap 객체로 만들기
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            picturePath = file.getAbsolutePath();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);
            imageView.setImageBitmap(bitmap);
        }
    }


    //현재 날짜 출력
    private String getTime() {
        now = System.currentTimeMillis();
        date = new Date(now);

        return dateFormat.format(date);
    }

    public void takePicture() { // 카메라 어플 연동
        if (file == null) {
            file = createFile();
        }

        Uri uri = FileProvider.getUriForFile(this, "com.example.memo.fileprovider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 102); // 사진 찍기 화면 띄우기
        }

    }

    private File createFile() {
        String fileName = "capture.jpg"; // capture.jsp 라는 이름으로 파일 저장

        File storageDir = Environment.getExternalStorageDirectory();
        File outFile = new File(storageDir, fileName);

        return outFile;
    }


    private void insert() {
        String picture = picturePath; // 파일 경로
        String contents = contentsText.getText().toString(); // 메모 내용
        String date = dateTextView.getText().toString(); // 날짜

        println("insert 호출됨");

        if (database == null) {
            println("데이터베이스를 먼저 생성");
            return;
        }

        if (picture != null || contents.length() > 0) { //입력 값이 있을 때
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
        String modify_picture = picturePath;
        String modify_contents = contentsText.getText().toString(); // 메모 내용
        String modify_date = dateTextView.getText().toString(); // 날짜


        if (modify_contents != null || modify_picture != null) { //입력 값이 있을 때
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

        } else { // 입력 값이 없을 때
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

    @Override
    public void onDenied(int requestCode, String[] permissions) {
        //Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onGranted(int requestCode, String[] permissions) {
        //Toast.makeText(this, "permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();

    }
}