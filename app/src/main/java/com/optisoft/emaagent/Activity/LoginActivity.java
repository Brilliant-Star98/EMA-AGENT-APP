package com.optisoft.emaagent.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optisoft.emaagent.HelperClasses.CommonPrefrence;
import com.optisoft.emaagent.Model.ResponseModel;
import com.optisoft.emaagent.Model.UserModel;
import com.optisoft.emaagent.R;
import com.optisoft.emaagent.Webservices.CallApi;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.HashMap;

import static com.optisoft.emaagent.HelperClasses.ApiConstant.EMAIL_PATTERN;
import static com.optisoft.emaagent.HelperClasses.ApiConstant.EXCEPTION_TAG;


public class LoginActivity extends AppCompatActivity {

    private CallApi callApi = new CallApi();
    private Gson gson = new Gson();
    private UserModel userModel;
    private CommonPrefrence commonPrefrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        commonPrefrence = new CommonPrefrence();
        if (commonPrefrence.getUserLoginSharedPref(this) != null){
            gotoMain();
        }

        TextView btn  = (TextView) findViewById(R.id.btn);
        final EditText et_email  = (EditText) findViewById(R.id.et_email);
        final EditText et_password  = (EditText) findViewById(R.id.et_password);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String pass  = et_password.getText().toString();

                if (email.isEmpty()){
                    et_email.setError("Enter your registered email id");
                    et_email.requestFocus();
                    return;
                }
                if (!email.matches(EMAIL_PATTERN)){
                    et_email.setError("Invalid email id");
                    et_email.requestFocus();
                }
                if (pass.isEmpty()){
                    et_password.setError("Enter password");
                    et_password.requestFocus();
                    return;
                }
                if (pass.length()< 6){
                    et_password.setError("Invalid password");
                    et_password.requestFocus();
                    return;
                }

                HashMap<String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("password", pass);
                map.put("fb_token", commonPrefrence.getFbTockenPref(LoginActivity.this));
                callApi.requestAgentLogin(LoginActivity.this, map);
            }
        });
    }

    private void gotoMain() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void customToast(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
    }

    public void responseLogin(ResponseModel body) {
        try {
            customToast(body.getMessage());
            if (body.getStatus() == 1){
                JSONArray jsonArray = new JSONArray(gson.toJson(body.getResponse()));
                Type type=new TypeToken<UserModel>(){}.getType();
                userModel = gson.fromJson(jsonArray.get(0).toString(), type);
                if (userModel != null){
                    CommonPrefrence commonPrefrence = new CommonPrefrence();
                    commonPrefrence.setUserLoginSharedPref(LoginActivity.this, userModel);
                    gotoMain();
                }
            }
        }catch (Exception e){
            customToast(e.getMessage());
            Log.e(EXCEPTION_TAG, e.getMessage());
        }
    }
}
