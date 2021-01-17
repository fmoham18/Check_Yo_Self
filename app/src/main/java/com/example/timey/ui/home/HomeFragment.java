package com.example.timey.ui.home;

import android.app.ActivityManager;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timey.Compoenets.AppListViewAdapter;
import com.example.timey.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static android.content.Context.ACTIVITY_SERVICE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ListView lv;
    HashMap<String, HashSet<String>> trackList2Strings = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);


        final PackageManager pm = getContext().getPackageManager();

        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        lv = root.findViewById(R.id.listView);

        List<ApplicationInfo> nonSystemApps = apps
                .stream()
                .filter((appInfo) -> (appInfo.flags &
                        (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0)
                .collect(Collectors.toList());
        String[] appNames = nonSystemApps.stream()
                .map(app -> pm.getApplicationLabel(app).toString())
                .toArray(String[]::new);

        String[] packageNames = nonSystemApps.stream().map(app -> app.packageName).toArray(String[]::new);

        Drawable[] appIcons = nonSystemApps.stream().map(app -> app.loadIcon(pm)).toArray(Drawable[]::new);
        HashSet<String> selectedApps = new HashSet<String>();

        AppListViewAdapter appLVAdapter = new AppListViewAdapter(getContext(), appNames, packageNames, appIcons, selectedApps);

        ((Button) root.findViewById(R.id.debugButton)).setOnClickListener(view ->
        {
            String line = "";
            for (String entry : selectedApps) {
                line += "|" + entry;
            }
            ;
            Toast.makeText(getContext(), line, Toast.LENGTH_SHORT).show();

        });

        lv.setAdapter(appLVAdapter);
        System.out.println(activeList(new ArrayList<String>()));

        return root;
    }


    public String activeList(ArrayList<String> appList) {
        Long endTime = System.currentTimeMillis();
        Long beginTime = endTime - 10000;
        UsageStatsManager stats = (UsageStatsManager) getContext().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatMap = stats.queryUsageStats(stats.INTERVAL_YEARLY,
                beginTime,
                endTime);
        String line = "";
        for (UsageStats entry : usageStatMap) {
            line += "\n" + "\n pack: " + entry.getPackageName() + "time(s): " + (entry.getTotalTimeInForeground() / 1000);
        }
        ;
        return line;
    }
}
