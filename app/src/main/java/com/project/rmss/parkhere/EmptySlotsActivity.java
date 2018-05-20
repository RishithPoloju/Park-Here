package com.project.rmss.parkhere;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static  com.project.rmss.parkhere.EmptySlotsActivity.*;
import static com.project.rmss.parkhere.DBContract.*;

public class EmptySlotsActivity extends AppCompatActivity {

    static Activity activity;
    static String toast;
    static EditText vnum;
    static ProgressBar progressBar;
    static Button button;
    static RadioButton radio_bike;
    static RadioButton radio_car;
    static String clickedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_slots);
        activity = this;
        toast = null;
        clickedID = getIntent().getStringExtra("clickedID");
        vnum = findViewById(R.id.park_vnum);
        progressBar = findViewById(R.id.park_progressBar);
        button = findViewById(R.id.park_button);
        radio_bike = findViewById(R.id.park_radio_bike);
        radio_car = findViewById(R.id.park_radio_car);
        final Toast tst = Toast.makeText(this, "Please Enter the vehicle number!", Toast.LENGTH_SHORT);
        progressBar.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(vnum.getText())){
                    if(tst != null){
                        tst.cancel();
                    }
                    tst.show();
                }
                else{
                    ParkVehicle parkVehicle = new ParkVehicle(getApplicationContext());
                    parkVehicle.execute("");
                }
            }});
    }
}
class ParkVehicle extends AsyncTask<String ,String ,String >{

    private Context context;
    private String toastMessage;
    private String inhours;
    private String inminutes;
    ParkVehicle(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        button.setActivated(false);
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String vehicle = vnum.getText().toString();
        String vtype = "";
        boolean flag = false;
        if (radio_bike.isChecked()){
            vtype = "b";
            flag =true;
        }
        else if (radio_car.isChecked()){
            vtype = "c";
            flag = true;
        }
        else
        {
            toastMessage = "Please Select Vehicle Type!";
        }
        if(flag){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat hours = new SimpleDateFormat("HH");
            inhours = hours.format(c.getTime());
            SimpleDateFormat minutes = new SimpleDateFormat("mm");
            inminutes = minutes.format(c.getTime());

        }

        String query = "UPDATE "+dbParkingTable+" SET "+dbVehicleNumber+" = '"+vehicle+"', "+dbVehicleType+"='"+vtype+"', "+dbVehicleInTimeHours+"='"+inhours+"', "+dbVehicleInTimeMinutes+"='"+inminutes+"' WHERE "+dbVehicleSlot+"='"+clickedID+"';";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            toastMessage = "Vehicle Parked in Bay " + clickedID;
            statement.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        button.setActivated(true);
        progressBar.setVisibility(View.GONE);
        activity.finish();
        super.onPostExecute(s);
    }
}