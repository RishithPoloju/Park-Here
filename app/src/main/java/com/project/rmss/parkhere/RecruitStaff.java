package com.project.rmss.parkhere;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.Statement;

import static  com.project.rmss.parkhere.RecruitStaff.*;
import static com.project.rmss.parkhere.DBContract.*;


public class RecruitStaff extends AppCompatActivity {
    static Activity activity;
    static String toastMessage;
    static EditText recruit_uname;
    static EditText recruit_fullname;
    static EditText recruit_passwd;
    static EditText recruit_rpasswd;
    static EditText recruit_age;
    static EditText recruit_contact;
    static Button recruit;
    static ProgressBar recruit_PB;
    static RadioButton recruit_rb_admin;
    static RadioButton recruit_rb_staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_staff);
        ActionBar actionBar=this.getSupportActionBar();

        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        activity = this;
        toastMessage = null;
        recruit_uname = findViewById(R.id.park_vnum);
        recruit_fullname = findViewById(R.id.recruit_et_fname);
        recruit_passwd = findViewById(R.id.recruit_et_passwd);
        recruit_rpasswd = findViewById(R.id.recruit_et_rpasswd);
        recruit_age = findViewById(R.id.recruit_et_age);
        recruit_contact = findViewById(R.id.recruit_et_contact);
        recruit = findViewById(R.id.park_button);
        recruit_PB = findViewById(R.id.park_progressBar);
        recruit_rb_admin = findViewById(R.id.park_radio_bike);
        recruit_rb_staff = findViewById(R.id.park_radio_car);

        recruit_PB.setVisibility(View.GONE);

        recruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(recruit_uname.getText().toString())) {
                    recruit_uname.setError("Please Enter Username");
                }
                if(TextUtils.isEmpty(recruit_fullname.getText().toString())) {
                    recruit_fullname.setError("Please Enter Fullname");
                }
                if(TextUtils.isEmpty(recruit_passwd.getText().toString())) {
                    recruit_passwd.setError("Please Enter your Password");
                }
                if(TextUtils.isEmpty(recruit_age.getText().toString())) {
                    recruit_age.setError("Please Enter your Age");
                }
                if(TextUtils.isEmpty(recruit_contact.getText().toString())) {
                    recruit_contact.setError("Please Enter Contact Number");
                }
                else {
                    if (recruit_passwd.getText().toString().equals(recruit_rpasswd.getText().toString())) {
                        RecruitTask recruitTask = new RecruitTask(getApplicationContext());
                        recruitTask.execute("");
                    } else {
                        Toast.makeText(RecruitStaff.this, "The passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}

class RecruitTask extends AsyncTask<String,String,String >{
    boolean success;
    Context context;
    RecruitTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        success = false;
        recruit_PB.setVisibility(View.VISIBLE);
        recruit.setActivated(false);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String uname = recruit_uname.getText().toString();
        String fullname = recruit_fullname.getText().toString();
        String passwd = recruit_passwd.getText().toString();
        String age = (recruit_age.getText().toString());
        String contact = recruit_contact.getText().toString();
        String selectQuery = null;
        String insertQuery = null;
        boolean flag = false;

        try{
            Statement statement = connection.createStatement();
            if(recruit_rb_admin.isChecked()) {
                selectQuery = "select * from " + dbAdminTable + " where " + dbAdminUname + " ='" + uname + "' and " + dbAdminPasswd + "='" + passwd + "'";
                insertQuery = "INSERT INTO " + dbAdminTable + " (`" + dbAdminUname + "`, `" + dbAdminPasswd + "`, `" + dbAdminContact + "`, `" + dbAdminAge + "`, `" + dbAdminFname + "`) VALUES ('" + uname + "', '" + passwd + "', '" + contact + "', '" + age + "', '" + fullname + "');";
                flag = true;
            }
            else if (recruit_rb_staff.isChecked()) {
                selectQuery = "select * from " + dbStaffTable + " where " + dbStaffUname + " ='" + uname + "' and " + dbStaffPasswd + "='" + passwd + "'";
                insertQuery = "INSERT INTO " + dbStaffTable + " (`"+dbStaffUname+"`, `"+dbStaffPasswd+"`, `"+dbStaffContact+"`, `"+dbStaffAge+"`, `"+dbStaffFname+"`) VALUES ('" + uname + "', '" + passwd + "', '" + contact + "', '" + age + "', '" + fullname + "');";
                flag = true;
            }
            else {
                toastMessage = "Please select the recruit type";
            }
            if(flag) {
                ResultSet resultSet = statement.executeQuery(selectQuery);

                if (resultSet.next()) {
                    toastMessage = "User Already Exist!!";
                } else {
                    statement.executeUpdate(insertQuery);
                    toastMessage = "Member Added";
                    success = true;
                }
            }
        }
        catch (Exception e){
            toastMessage = e.getStackTrace().toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        recruit_PB.setVisibility(View.GONE);
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        if (success) {
            if(!recruit_rb_admin.isChecked())
                count++;
            activity.finish();
        }
        recruit.setActivated(true);
        super.onPostExecute(s);
    }
}
