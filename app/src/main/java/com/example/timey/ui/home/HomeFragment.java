package com.example.timey.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.timey.Compoenets.AppListViewAdapter;
import com.example.timey.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    private int STATS_PERMISSION = 101;
    private int KILL_PERMISSION = 102;
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
                        (ApplicationInfo.FLAG_SYSTEM
                                | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0)
                .collect(Collectors.toList());
        String[] appNames = nonSystemApps.stream()
                .map(app -> pm.getApplicationLabel(app).toString())
                .toArray(String[]::new);

        String[] packageNames = nonSystemApps.stream()
                .map(app -> app.packageName).toArray(String[]::new);

        Drawable[] appIcons = nonSystemApps.stream()
                .map(app -> app.loadIcon(pm)).toArray(Drawable[]::new);
        HashSet<String> selectedApps = new HashSet<String>();
        AppListViewAdapter appLVAdapter = new AppListViewAdapter(
                getContext(),
                appNames,
                packageNames,
                appIcons,
                selectedApps);

        ((Button) root.findViewById(R.id.debugButton)).setOnClickListener(view ->
        {
//            String line = "";
//            for (String entry : selectedApps) {
//                line += "|" + entry;
//            }
//            ;
//            Toast.makeText(getContext(), line, Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(HomeFragment.this.getActivity(),
//                    new String[]
//                            {Manifest.permission.KILL_BACKGROUND_PROCESSES},
//                    KILL_PERMISSION);
//            ActivityManager mActivityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
//            mActivityManager.killBackgroundProcesses("com.android.chrome");

        });

        lv.setAdapter(appLVAdapter);
        List<UsageStats> stats = activeList(null);
        renderGraph(root, stats);
        return root;
    }

    //
    private void renderGraph(View root, List<UsageStats> stats) {
        BarChart chart = root.findViewById(R.id.barchart);

        ArrayList<BarEntry> NoOfEmp = new ArrayList<>();

        for (int i = 0; i < stats.size(); i++) {
            UsageStats stat = stats.get(i);
            NoOfEmp.add(new BarEntry(i, stat.getTotalTimeInForeground() / 1000.0f));
        }

//        ArrayList<String>

        BarDataSet bardataset = new BarDataSet(NoOfEmp, "App time use");

        chart.animateY(5000);
        chart.fitScreen();
        chart.setFitBars(true);
        chart.getLegend().setEnabled(true);
        BarData data = new BarData(bardataset);

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(data);
    }

    private List<UsageStats> activeList(ArrayList<String> appList) {

        _getStats_Permission();
        long endTime = System.currentTimeMillis();
        long beginTime = endTime - 100000;

        UsageStatsManager stats = (UsageStatsManager) getContext().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatMap = stats.queryUsageStats(stats.INTERVAL_BEST,
                beginTime,
                endTime);
        String line = "";
        for (UsageStats entry : usageStatMap) {
            line += "\n" + "\n pack: " + entry.getPackageName() + "time(s): " + (entry.getTotalTimeInForeground() / 1000);
        }
        System.out.println(line);
        return usageStatMap == null ? new ArrayList<>() : usageStatMap;


    }

    private boolean _getStats_Permission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.PACKAGE_USAGE_STATS)) {

            new AlertDialog.Builder(getContext())
                    .setTitle("Permission Needed")
                    .setMessage("The app needs to access usage stats")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(HomeFragment.this.getActivity(),
                                    new String[]
                                            {Manifest.permission.PACKAGE_USAGE_STATS},
                                    STATS_PERMISSION);

                        }
                    })
                    .setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {

            ActivityCompat
                    .requestPermissions(getActivity(), new String[]{Manifest.permission.PACKAGE_USAGE_STATS}, STATS_PERMISSION);
        }
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STATS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == getContext().getPackageManager().PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();

            }
        }
    }
}
