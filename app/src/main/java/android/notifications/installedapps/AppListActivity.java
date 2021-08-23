package android.notifications.installedapps;
import android.notifications.R;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AppsAdapter adapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_applist);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Passing the column number 1 to show online one column in each row.
        recyclerViewLayoutManager = new LinearLayoutManager(AppListActivity.this);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        adapter = new AppsAdapter(AppListActivity.this, new ApkInfoExtractor(AppListActivity.this).GetAllInstalledApkInfo());

        recyclerView.setAdapter(adapter);
    }
}