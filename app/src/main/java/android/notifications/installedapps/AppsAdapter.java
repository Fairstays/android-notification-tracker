package android.notifications.installedapps;

/**
 * Created by Juned on 4/14/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.notifications.R;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.View;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder>{

    Context context1;
    List<String> stringList;
    public static final String prefSelectedPackageList = "PREF_SELECTED_PACKAGE";
    ArrayList<String> selectedPackageList = new ArrayList<>();

    public AppsAdapter(Context context, List<String> list){

        context1 = context;

        stringList = list;
        ArrayList<String> selectedPackage = getArrayList(context1);
        if(selectedPackage != null){
            selectedPackageList.addAll(selectedPackage);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CardView cardView;
        public ImageView imageView;
        public TextView textView_App_Name;
        public TextView textView_App_Package_Name;
        public CheckBox cbselect;

        public ViewHolder (View view){

            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view);
            imageView = (ImageView) view.findViewById(R.id.imageview);
            textView_App_Name = (TextView) view.findViewById(R.id.Apk_Name);
            textView_App_Package_Name = (TextView) view.findViewById(R.id.Apk_Package_Name);
            cbselect = (CheckBox)view.findViewById(R.id.cbselect);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view2 = LayoutInflater.from(context1).inflate(R.layout.cardview_layout,parent,false);

        ViewHolder viewHolder = new ViewHolder(view2);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position){
        viewHolder.setIsRecyclable(false);
        ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(context1);

        final String ApplicationPackageName = (String) stringList.get(position);
        String ApplicationLabelName = apkInfoExtractor.GetAppName(ApplicationPackageName);
        Drawable drawable = apkInfoExtractor.getAppIconByPackageName(ApplicationPackageName);

        viewHolder.textView_App_Name.setText(ApplicationLabelName);

        viewHolder.textView_App_Package_Name.setText(ApplicationPackageName);
//        viewHolder.cbselect.setChecked(false);
//        ArrayList<String> selectedPackage = getArrayList();
        if(selectedPackageList.contains(ApplicationPackageName)){
            viewHolder.cbselect.setChecked(true);
        }else{
            viewHolder.cbselect.setChecked(false);
        }

        viewHolder.cbselect.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final String ApplicationPackageName = (String) stringList.get(position);
                if(isChecked){
                        if(! selectedPackageList.contains(ApplicationPackageName)){
                            selectedPackageList.add(ApplicationPackageName);
                            ArrayList<String> selectedPackage = getArrayList(context1);
                            if(selectedPackage != null) {
                                selectedPackage.add(ApplicationPackageName);
                                saveArrayList(selectedPackage, context1);
                            }else{
                                ArrayList<String> selectedPackagenew = new ArrayList<>();
                                selectedPackagenew.add(ApplicationPackageName);
                                saveArrayList(selectedPackagenew, context1);
                            }
                        }

                }else{
                    if(selectedPackageList.contains(ApplicationPackageName)){
                        selectedPackageList.remove(ApplicationPackageName);
                        saveArrayList(selectedPackageList, context1);
                    }
                }
            }
        });


        viewHolder.imageView.setImageDrawable(drawable);

        //Adding click listener on CardView to open clicked application directly from here .
//        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = context1.getPackageManager().getLaunchIntentForPackage(ApplicationPackageName);
////                if(intent != null){
////                    context1.startActivity(intent);
////                }
////                else {
////                    Toast.makeText(context1,ApplicationPackageName + " Error, Please Try Again.", Toast.LENGTH_LONG).show();
////                }
//            }
//        });
    }

    @Override
    public int getItemCount(){

        return stringList.size();
    }


    public static void saveArrayList(ArrayList<String> list, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(prefSelectedPackageList, json);
        editor.apply();

    }

    public static ArrayList<String> getArrayList(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString(prefSelectedPackageList, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}