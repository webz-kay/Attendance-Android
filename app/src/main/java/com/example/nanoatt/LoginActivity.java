package com.example.nanoatt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.nanoatt.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText inputUsername, inputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());
        inputPassword=findViewById(R.id.editTextPassword);
        inputUsername=findViewById(R.id.editTextUsername);
        Toast.makeText(this, "IMEI "+getDeviceIMEI(), Toast.LENGTH_SHORT).show();


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
                 .addBodyParameter("device_id", getDeviceIMEI())
                 .addHeaders("Accept","application/json")
                 .setPriority(Priority.HIGH)
                 .build()
                 .getAsJSONObject(new JSONObjectRequestListener() {
                     @Override
                     public void onResponse(JSONObject response) {
                         Toast.makeText(LoginActivity.this, "Logging In", Toast.LENGTH_SHORT).show();
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
                                 Intent intent=new Intent(LoginActivity.this, MapActivity.class);
                                 startActivity(intent);
                                 finish();
                             }else{
                                 if(!response.getBoolean("success")){
                                     Toast.makeText(LoginActivity.this, "Error. "+response.getString("message"), Toast.LENGTH_SHORT).show();

                                 }
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
                         Toast.makeText(LoginActivity.this, "Error while logging in.", Toast.LENGTH_SHORT).show();
                     }
                 });
    }
    String TAG="LOGIN_PAGE";
    /**
     * Returns the unique identifier for the device
     *
     * @return unique identifier for the device
     */
    public String getDeviceIMEI() {
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
         return android_id;
        /*String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;*/
    }

}
