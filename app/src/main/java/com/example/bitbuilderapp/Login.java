package com.example.bitbuilderapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Login extends Activity {

    EditText input_login_user, input_login_pass;
    String login_user, login_pass;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        input_login_user = (EditText) findViewById(R.id.input_login_user);
        input_login_pass = (EditText) findViewById(R.id.input_login_pass);
    }
    public void userReg(View view){
        startActivity(new Intent(this, Register.class));

    }

    public void userLogin(View view){
        login_user = input_login_user.getText().toString().trim();
        login_pass = input_login_pass.getText().toString().trim();

        String method = "login";
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method, login_user, login_pass);
    }
}
