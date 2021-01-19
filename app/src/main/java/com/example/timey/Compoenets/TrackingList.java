package com.example.timey.Compoenets;

import android.app.usage.UsageStats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class TrackingList {
    private HashSet<String> trackedApps;
    private final long totalTimeAllowed;
    private String listName;

    public static boolean checkIfListQuotaIsMet(TrackingList list, HashMap<String, UsageStats> stats) {
        long timeSpent = 0;

        for (String packageName : list.trackedApps) {
            UsageStats packageStat = stats.get(packageName);
            timeSpent += packageStat == null ? 0 : packageStat.getTotalTimeInForeground();
        }

        return timeSpent < list.totalTimeAllowed;
    }


    public TrackingList(HashSet<String> trackedApps, long totalTimeAllowed, String listName) {
        this.trackedApps = trackedApps;
        this.totalTimeAllowed = totalTimeAllowed;
        this.listName = listName;

    }

    public HashSet<String> getTrackedApps() {
        return trackedApps;
    }

    public void setTrackedApps(HashSet<String> trackedApps) {
        this.trackedApps = trackedApps;
    }

    public long getTotalTimeAllowed() {
        return totalTimeAllowed;
    }


    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public TrackingList setTotalTimeAllowed(long totalTimeAllowed) {
        return new TrackingList(trackedApps, totalTimeAllowed, listName);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackingList that = (TrackingList) o;
        return Objects.equals(listName, that.listName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listName);
    }

    @Override
    public String toString() {
        return "TrackingList{" +
                "trackedApps=" + Arrays.toString(trackedApps.toArray(new String[]{})) +
                ", totalTimeAllowed=" + totalTimeAllowed +
                ", listName='" + listName + '\'' +
                '}';
    }
}
