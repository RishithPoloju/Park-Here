package com.project.rmss.parkhere;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.project.rmss.parkhere.SplashUI.*;
import static com.project.rmss.parkhere.DBContract.*;

public class SplashUI extends AppCompatActivity {
    static Activity activity;
    static ProgressBar progressBar;
    static boolean creation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ui);
        progressBar = findViewById(R.id.splash_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        activity = this;
        GetConnection getConnection = new GetConnection(getApplicationContext());
        getConnection.execute();
        creation = true;
    }

    @Override
    protected void onResume() {
        if(!creation)
            finish();
        super.onResume();
    }
}
class GetConnection extends AsyncTask<Void,Void,Void>{
    boolean success;
    private Context context;

    GetConnection(Context context){
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        success = false;
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ResultSet resultSet;
        count = 0;
        slotcount = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(host  + "?user=" + dbUsername + "&password=" + dbPassword);
            String query = "select * from " + dbStaffTable;
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                count++;
            }
            resultSet.close();
            statement.close();
            query = "select * from " + dbParkingTable;
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                slotcount++;
            }
            resultSet.close();
            statement.close();
            success = true;
        } catch (Exception ex) {
            success = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressBar.setVisibility(View.GONE);
        if (success)
            context.startActivity(new Intent(context, LoginActivity.class));
        else{
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
            builder.setTitle("No Internet Connection")
                    .setMessage("Please connect to the Internet!")
                    .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
        context = null;
        creation = false;
        super.onPostExecute(aVoid);
    }
}
