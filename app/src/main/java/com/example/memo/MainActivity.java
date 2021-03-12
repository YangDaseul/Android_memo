package com.example.memo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_MENU1 = 1; // 작성 페이지로 이동하여 작성 데이터 가져오기
    public static final int REQUEST_CODE_MENU2 = 2; // 데이터 삭제, 수정

    ArrayList<MemoItem> items = new ArrayList<MemoItem>();
    ListViewAdapter adapter;
    ListView listView;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DB 연결
        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        adapter = new ListViewAdapter(items, getApplicationContext()); // adapter 생성
        listView = (ListView) findViewById(R.id.listView); // listView 생성
        listView.setAdapter(adapter); // adapter 지정


        // 저장되어 있는 리스트 메인화면에 출력
        listData();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 수정, 삭제

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemoItem memoItem = items.get(position);

                intent1 = new Intent(getApplicationContext(), NoteActivity.class);

                //int list_id = items.get(position).get_id();
                //String list_contents = items.get(position).getContents();

                int list_id = memoItem.get_id();
                String list_picture = memoItem.getPicture();
                String list_contents = memoItem.getContents();
                String type = "update";

                intent1.putExtra("type", type);
                intent1.putExtra("list_id", list_id);
                intent1.putExtra("list_picture", list_picture);
                intent1.putExtra("list_contents", list_contents);



                startActivityForResult(intent1, REQUEST_CODE_MENU2);
            }
        });


        // 조회기능
        EditText editTextFilter = (EditText) findViewById(R.id.editTextFilter);
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable edit) {

                String filterText = edit.toString();
                if (filterText.length() > 0) {
                    listView.setFilterText(filterText);
                } else {
                    listView.clearTextFilter();
                }

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // 메뉴
        getMenuInflater().inflate(R.menu.menu_bottom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 작성 페이지로 이동
        int id = item.getItemId();
        String type = "insert";

        if (id == R.id.menu_note) {
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra("type", type);
            startActivityForResult(intent, REQUEST_CODE_MENU1);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MENU1 || requestCode == REQUEST_CODE_MENU2) { // 입력 버튼 클릭 시 || 수정, 삭제 버튼 클릭 시
            if (resultCode == RESULT_OK) {
                listData();
            }
        }
    }

    public void listData() { // 리스트 출력
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_NAME;
        Cursor cursor = database.rawQuery(sql, null);

        items.clear(); // 리스트 비우기

        int count = cursor.getCount();

        for (int i = 0; i < count; i++) {
            cursor.moveToNext();

            int _id = cursor.getInt(0);
            String picture = cursor.getString(1);
            String contents = cursor.getString(2);
            String date = cursor.getString(3);

            items.add(new MemoItem(_id, picture, contents, date));
        }
        adapter.notifyDataSetChanged();

    }

}