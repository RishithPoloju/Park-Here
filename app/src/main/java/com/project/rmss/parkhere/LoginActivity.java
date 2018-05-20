package com.project.rmss.parkhere;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.project.rmss.parkhere.DBContract.*;
import static com.project.rmss.parkhere.LoginActivity.*;

public class LoginActivity extends AppCompatActivity {

    public static Statement statement;
    public static boolean loginFlag;
    public static boolean adminFlag;

    public static String toastMessage;

    static boolean buttonClickable;

    static EditText login_username;
    static EditText login_password;
    static ProgressBar progressBar;
    Button login_button;

    private long lastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lastClickTime = 0;

        login_username = findViewById(R.id.login_et_uname);
        login_password = findViewById(R.id.login_et_passwd);
        login_button = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.login_progressBar);

        loginFlag = false;
        adminFlag = false;

        buttonClickable = true;

        login_username.setText("Rishith");
        login_password.setText("qwerty");

        progressBar.setVisibility(View.GONE);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonClickable == false){
                    return;
                }
                buttonClickable = false;
                if(TextUtils.isEmpty(login_username.getText().toString())) {
                    login_username.setError("Please Enter Username");
                }
                if(TextUtils.isEmpty(login_password.getText().toString())) {
                    login_password.setError("Please Enter your Password");
                }
                else {
                    LoginTask login = new LoginTask(getApplicationContext());
                    login.execute("");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finish();
    }
}
class LoginTask extends AsyncTask<String, String, String> {
    Context context;
    private ResultSet resultSet;

    LoginTask(Context context){
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String username = login_username.getText().toString();
        String password = login_password.getText().toString();
        adminFlag = false;
        if (username.trim().equals("") || password.trim().equals("")) {
            toastMessage = "Enter Username and password";
        }
        else{
            try{
                if (connection == null) {
                }
                else {
                    String query = "select * from " + dbAdminTable + " where "+ dbAdminUname +" ='" + username + "' and " + dbAdminPasswd + "='" + password + "'";
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(query);
                    if (resultSet.next()) {
                        adminFlag = true;
                        loginFlag = true;
                    }
                    else {
                        query = "select * from " + dbStaffTable + " where "+ dbStaffUname +" ='" + username + "' and " + dbStaffPasswd + "='" + password + "'";
                        resultSet = statement.executeQuery(query);
                        if (resultSet.next()) {
                            loginFlag = true;
                            adminFlag = false;
                        }
                        else{
                        toastMessage = "Invalid Credentials";
                        buttonClickable = true;
                        }
                    }
                }
                resultSet.close();
            }
            catch (Exception ex) {
                toastMessage = ex.getMessage();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        progressBar.setVisibility(View.GONE);
        if (loginFlag == true && adminFlag == true) {
            context.startActivity(new Intent(context, AdminConsole.class));
            buttonClickable = true;
        }
        else if(loginFlag == true){
            context.startActivity(new Intent(context, StaffConsole.class));
            buttonClickable = true;
        }
        else
        Toast.makeText(context, toastMessage,Toast.LENGTH_SHORT).show();
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onPostExecute(s);
    }


}
