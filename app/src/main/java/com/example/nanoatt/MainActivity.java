package com.example.nanoatt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.nanoatt.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText inputUsername, inputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());
        inputPassword=findViewById(R.id.editTextPassword);
        inputUsername=findViewById(R.id.editTextUsername);


    }

    public  void login(View view){
         String username= inputUsername.getText().toString().trim();
         String password= inputPassword.getText().toString().trim();
         if (username.isEmpty() || password.isEmpty()){
             Toast.makeText(this, "Fill in all the values", Toast.LENGTH_SHORT).show();
             return;
         }
         AndroidNetworking.post(Urls.LOGIN_URL)
                 .addBodyParameter("email", username)
                 .addBodyParameter("password", password)
                 .addHeaders("Accept","application/json")
                 .setPriority(Priority.HIGH)
                 .build()
                 .getAsJSONObject(new JSONObjectRequestListener() {
                     @Override
                     public void onResponse(JSONObject response) {
                         Toast.makeText(MainActivity.this, "Logging In", Toast.LENGTH_SHORT).show();
                         try {
                             if (response.getBoolean("success")){
                                int id = response.getJSONObject("user").getInt("id");
                                String email = response.getJSONObject("user").getString("email");
                                String name = response.getJSONObject("user").getString("name");
                                 SharedPreferences.Editor editor=getSharedPreferences("data", MODE_PRIVATE).edit();
                                 editor.putInt("id", id);
                                 editor.putString("email", email);
                                 editor.putString("name", name);
                                 editor.apply();
                                 Intent intent=new Intent(MainActivity.this, MapActivity.class);
                                 startActivity(intent);
                                 finish();
                             }else{
                                 Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
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
                         Toast.makeText(MainActivity.this, "Error while logging in.", Toast.LENGTH_SHORT).show();
                     }
                 });
    }
    String TAG="LOGIN_PAGE";

}
