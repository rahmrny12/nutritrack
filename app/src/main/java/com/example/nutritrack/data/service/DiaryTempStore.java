package com.example.nutritrack.data.service;

import com.example.nutritrack.data.model.DiaryDetail;
import java.util.ArrayList;
import java.util.List;

public class DiaryTempStore {

    private static DiaryTempStore instance;
    private final List<DiaryDetail> diaryList = new ArrayList<>();

    private DiaryTempStore() {}

    public static DiaryTempStore getInstance() {
        if (instance == null) instance = new DiaryTempStore();
        return instance;
    }

    public void addItem(DiaryDetail item) {
        diaryList.add(item);
    }

    public List<DiaryDetail> getAll() {
        return diaryList;
    }

    public void clear() {
        diaryList.clear();
    }

    public void removeItem(DiaryDetail item) {
        diaryList.remove(item);
    }
}
