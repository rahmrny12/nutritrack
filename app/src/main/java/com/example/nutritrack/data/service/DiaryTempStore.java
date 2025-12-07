package com.example.nutritrack.data.service;

import com.example.nutritrack.data.model.DiaryItemModel;
import java.util.ArrayList;
import java.util.List;

public class DiaryTempStore {

    private static DiaryTempStore instance;
    private final List<DiaryItemModel> diaryList = new ArrayList<>();

    private DiaryTempStore() {}

    public static DiaryTempStore getInstance() {
        if (instance == null) instance = new DiaryTempStore();
        return instance;
    }

    public void addItem(DiaryItemModel item) {
        diaryList.add(item);
    }

    public List<DiaryItemModel> getAll() {
        return diaryList;
    }

    public void clear() {
        diaryList.clear();
    }

    public void removeItem(DiaryItemModel item) {
        diaryList.remove(item);
    }
}
