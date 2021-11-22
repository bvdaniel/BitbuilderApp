package com.example.bitbuilderapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Register extends Activity {

    EditText input_register_name, input_register_pass, input_register_user;
    String name, user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        input_register_name = (EditText) findViewById(R.id.input_register_name);
        input_register_user = (EditText) findViewById(R.id.input_register_user);
        input_register_pass = (EditText) findViewById(R.id.input_register_pass);

    }
    public void userReg(View view){
        name = input_register_name.getText().toString();
        user = input_register_user.getText().toString();
        pass = input_register_pass.getText().toString();
        String method = "register";

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method, name, user, pass);
        finish();
    }
}
