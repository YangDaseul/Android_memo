package com.example.memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter implements Filterable {
    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList (원본 데이터 리스트)
    ArrayList<MemoItem> items;
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유
    ArrayList<MemoItem> filteredItems;

    Context context;


    Filter listFilter;

    public ListViewAdapter(ArrayList<MemoItem> items, Context context) {
        this.items = items;
        this.context = context;
        filteredItems = items;

    }

    //Adapter에 사용되는 데이터 개수 리턴
    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //position에 위치한 데이터를 화면에 출력하는데 사용된 View 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView contentsTextView = convertView.findViewById(R.id.contentsTextView);

        //filteredItems에서 position에 위치한 데이터 참조 획득
        MemoItem memoItem = filteredItems.get(position);

        titleTextView.setText(memoItem.getTitle());
        contentsTextView.setText(memoItem.getContents());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = items;
                results.count = items.size();
            } else {
                ArrayList<MemoItem> itemList = new ArrayList<MemoItem>();

                for (MemoItem item : items) {
                    if (item.getTitle().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            item.getContents().toUpperCase().contains(constraint.toString().toUpperCase())) {

                        itemList.add(item);

                    }
                }
                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredItems = (ArrayList<MemoItem>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }


        }
    }


}
