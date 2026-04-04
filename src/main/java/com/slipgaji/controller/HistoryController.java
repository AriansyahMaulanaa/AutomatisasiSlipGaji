package com.slipgaji.controller;

import com.slipgaji.model.SendHistory;
import com.slipgaji.service.DatabaseService;

import java.util.List;

public class HistoryController {
    private final DatabaseService db;

    public HistoryController() {
        this.db = DatabaseService.getInstance();
    }

    public List<SendHistory> getHistory(String period) {
        return db.getSendHistory(period);
    }

    public int getSentCount() {
        return db.getSentCount();
    }

    public int getFailedCount() {
        return db.getFailedCount();
    }
}
