package com.project.rmss.parkhere;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.Statement;

import static com.project.rmss.parkhere.StaffActivity.*;
import static com.project.rmss.parkhere.DBContract.*;


public class StaffActivity extends AppCompatActivity {
    static Activity activity;
    static String clickedID;
    static TextView uname;
    static TextView fname;
    static TextView age;
    static TextView contact;
    static TextView id;
    static Button button;
    static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        activity = this;
        clickedID = getIntent().getStringExtra("clickedID");

        uname = findViewById(R.id.slots_text_vnum);
        fname = findViewById(R.id.slots_text_slot);
        age = findViewById(R.id.slots_text_intime);
        contact = findViewById(R.id.slots_text_outime);
        id = findViewById(R.id.slots_text_cost);
        button = findViewById(R.id.slots_button);

        progressBar = findViewById(R.id.slots_progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        GettingStaffData gettingStaffData = new GettingStaffData();
        gettingStaffData.execute("");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Employee Leaving")
                        .setMessage("Are you sure you want to remove the employee?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DismissStaff dismissStaff = new DismissStaff(getApplicationContext());
                                dismissStaff.execute("");
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });
    }
}
class GettingStaffData extends AsyncTask<String, String, String>{
    private String db_uname;
    private String db_fname;
    private String db_age;
    private String db_contact;

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            Statement statement = connection.createStatement();
            String query = "select * from " + dbStaffTable + " where " + dbStaffId + " = '" + clickedID + "';";
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            db_uname = resultSet.getString(dbStaffUname);
            db_fname = resultSet.getString(dbStaffFname);
            db_age = resultSet.getString(dbStaffAge);
            db_contact = resultSet.getString(dbStaffContact);
            statement.close();
        }
        catch (Exception exception){
            System.out.println(exception.getStackTrace().toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        uname.setText(db_uname);
        fname.setText(db_fname);
        age.setText(db_age);
        contact.setText(db_contact);
        id.setText(clickedID);
        progressBar.setVisibility(View.INVISIBLE);
        super.onPostExecute(s);
    }
}
class DismissStaff extends AsyncTask<String, String, String>{
    private Context context;
    private String toastMessage;
    DismissStaff(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            Statement statement = connection.createStatement();
            String query = "DELETE FROM "+dbStaffTable+" WHERE "+dbStaffId+ "='"+clickedID+"';";
            statement.executeUpdate(query);
            statement.close();
            statement = connection.createStatement();
            query = "ALTER TABLE "+dbStaffTable+" AUTO_INCREMENT="+clickedID+";";
            statement.executeUpdate(query);
            statement.close();
            count = count - 1;
            toastMessage = "Member Removed";
        }
        catch (Exception exception){
            toastMessage = "OOPS! Something went wrong";
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        activity.finish();
    }
}
