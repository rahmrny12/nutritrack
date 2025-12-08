package com.example.nutritrack.data.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.DiaryDetail;
import com.example.nutritrack.data.service.DiaryTempStore;

import java.util.List;

public class TempDiaryPopupAdapter extends ArrayAdapter<DiaryDetail> {

    private Activity context;
    private List<DiaryDetail> list;

    public TempDiaryPopupAdapter(Activity context, List<DiaryDetail> list) {
        super(context, R.layout.item_popup_diary, list);
        this.context = context;
        this.list = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item_popup_diary, null, true);

        DiaryDetail item = list.get(position);

        ((TextView) rowView.findViewById(R.id.name)).setText(item.getName());
        ((TextView) rowView.findViewById(R.id.info))
                .setText(item.getCalories() + " cal • " + item.getCarbs() + "c • " + item.getProtein() + "p • " + item.getFat() + "f");

        rowView.findViewById(R.id.btnDelete).setOnClickListener(v -> {
            DiaryTempStore.getInstance().removeItem(item); // remove from store
            list.remove(item); // remove by object, not index
            notifyDataSetChanged();                        // refresh popup
        });

        return rowView;
    }
}
