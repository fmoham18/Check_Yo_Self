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
    private View _root;
    HashMap<String, HashSet<String>> trackList2Strings = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        _root = root;

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
        List<UsageStats> lists = activeList(new ArrayList<String>());

//        renderGraph();
        return root;
    }
//
//public void renderGraph(){
//    BarChart chart = _root.findViewById(R.id.barchart);
//
//    ArrayList NoOfEmp = new ArrayList();
//
//    NoOfEmp.add(new BarEntry(945f, 0));
//    NoOfEmp.add(new BarEntry(1040f, 1));
//    NoOfEmp.add(new BarEntry(1133f, 2));
//    NoOfEmp.add(new BarEntry(1240f, 3));
//    NoOfEmp.add(new BarEntry(1369f, 4));
//    NoOfEmp.add(new BarEntry(1487f, 5));
//    NoOfEmp.add(new BarEntry(1501f, 6));
//    NoOfEmp.add(new BarEntry(1645f, 7));
//    NoOfEmp.add(new BarEntry(1578f, 8));
//    NoOfEmp.add(new BarEntry(1695f, 9));
//
//    ArrayList year = new ArrayList();
//
//    year.add("2008");
//    year.add("2009");
//    year.add("2010");
//    year.add("2011");
//    year.add("2012");
//    year.add("2013");
//    year.add("2014");
//    year.add("2015");
//    year.add("2016");
//    year.add("2017");
//
//    BarDataSet bardataset = new BarDataSet(NoOfEmp, "No Of Employee");
//
//    chart.animateY(5000);
//    BarData data = new BarData( bardataset);
////    data.set
//    bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
//    chart.setData(data);
//}
    public List<UsageStats> activeList(ArrayList<String> appList) {
        long endTime = System.currentTimeMillis();
        long beginTime = endTime - 1000;
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

        UsageStatsManager stats = (UsageStatsManager) getContext().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatMap = stats.queryUsageStats(stats.INTERVAL_BEST,
                beginTime,
                endTime);
        String line = "";
//        for (UsageStats entry : usageStatMap) {
//            line += "\n" + "\n pack: " + entry.getPackageName() + "time(s): " + (entry.getTotalTimeInForeground() / 1000);
//        }
//        ;
        return usageStatMap;
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
