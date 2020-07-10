package com.example.nanoatt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.nanoatt.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AttendanceActivity extends AppCompatActivity {

    CustomAdapter adapter;
    ArrayList<Item> listArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        listArray=new ArrayList<>();
        adapter=new CustomAdapter(this, listArray);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        getData();

    }
    String TAG="ATTENDANCE";
    private void getData() {
        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        int id = prefs.getInt("id",0);
        AndroidNetworking.post(Urls.ATTENDANCE_URL)
                .addBodyParameter("user_id", id+"")
                .addHeaders("Accept","application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(AttendanceActivity.this, "Logging In", Toast.LENGTH_SHORT).show();
                        try {
                            if (response.getBoolean("success")){
                                 String name = response.getJSONObject("data").getString("name");
                                 JSONArray array = response.getJSONObject("data").getJSONArray("records");
                                 for (int i=0; i<array.length(); i++){
                                    JSONObject record = array.getJSONObject(i);
                                    String time_in= record.getString("timeAppIn");
                                    String time_out= record.getString("timeAppOut");
                                    String date= record.getString("dateChekin");
                                    Item item=new Item(name, date, time_in, time_out);
                                    listArray.add(item);
                                 }
                                 adapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(AttendanceActivity.this, "Failed To Load Data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Log.d(TAG, "onError: "+anError.getErrorBody());
                        Log.d(TAG, "onError: "+anError.getMessage());
                        Log.d(TAG, "onError: "+anError.getResponse());
                        Toast.makeText(AttendanceActivity.this, "Error while logging in.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
