package android.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.notifications.installedapps.AppsAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NotificationService extends NotificationListenerService {

    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();

        ArrayList<String> selectedPackage = getArrayList();

        if(selectedPackage != null && selectedPackage.contains(pack)){

        String ticker ="";
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        Bundle extras = sbn.getNotification().extras;
        String title = "";
        if(extras.containsKey("android.title") &&
                extras.getString("android.title") != null){
            title = extras.getString("android.title").toString();
        }

        String text = "";
        if(extras.containsKey("android.text") &&
                extras.getString("android.text") != null){
            text = extras.getString("android.text").toString();
        }

        int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
        Bitmap id = sbn.getNotification().largeIcon;

        Log.i("Package",pack);
        Log.i("Ticker",ticker);
        Log.i("Title",title);
        Log.i("Text",text);

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        if(id != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            id.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            msgrcv.putExtra("icon",byteArray);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        addItemToSheet(pack, title, text, ticker);
        }
    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }

    private void addItemToSheet(final String packeageName,
                                final String title, final String content,
                                final String ticker) {

//        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
//        final String name = editTextItemName.getText().toString().trim();
//        final String brand = editTextBrand.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://script.google.com/macros/s/AKfycbyrW0Sj2Ln0WMDlMEDhJ8lEYsWabtYqfIfHjXYCAWvRNZ6h5XXRosqW/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("app sheet response ", response);
//                        loading.dismiss();
//                        Toast.makeText(AddItem.this,response,Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("app sheet", error.networkResponse.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action","addItem");
                parmas.put("package",packeageName);
                parmas.put("title", title);
                parmas.put("content", content);
                parmas.put("ticker", ticker);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public ArrayList<String> getArrayList(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(AppsAdapter.prefSelectedPackageList, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
