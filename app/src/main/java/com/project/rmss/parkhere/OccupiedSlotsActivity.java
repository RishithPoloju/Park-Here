package com.project.rmss.parkhere;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.project.rmss.parkhere.OccupiedSlotsActivity.*;
import static com.project.rmss.parkhere.DBContract.*;

public class OccupiedSlotsActivity extends AppCompatActivity {

    static Activity activity;
    static String clickedID;
    static TextView vnum;
    static TextView intime;
    static TextView outtime;
    static TextView cost;
    static TextView slot;
    static ImageView vtype;
    static Button button;
    static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occupied_slots);

        activity = this;
        clickedID = getIntent().getStringExtra("clickedID");

        vnum = findViewById(R.id.slots_text_vnum);
        intime = findViewById(R.id.slots_text_intime);
        outtime = findViewById(R.id.slots_text_outime);
        cost = findViewById(R.id.slots_text_cost);
        slot = findViewById(R.id.slots_text_slot);
        vtype = findViewById(R.id.slots_vtype);

        button = findViewById(R.id.slots_button);

        progressBar = findViewById(R.id.slots_progress);

        progressBar = findViewById(R.id.slots_progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        GettingSlotData gettingSlotData = new GettingSlotData();
        gettingSlotData.execute("");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Departing Vehicle")
                        .setMessage("Are you sure you want to depart the vehicle?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DepartVehicle departVehicle = new DepartVehicle(getApplicationContext());
                                departVehicle.execute("");
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });
    }
}

class GettingSlotData extends AsyncTask<String, String, String>{

    private String rs_vnum;
    private String rs_inhours;
    private String rs_inminutes;
    private String rs_vtype;
    private String rs_outhours;
    private String rs_outminutes;
    private String pcost;

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String query = "Select * from " + dbParkingTable + " where " + dbVehicleSlot + " = '"+ clickedID +"';";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            rs_vnum = resultSet.getString(dbVehicleNumber);
            rs_inhours = resultSet.getString(dbVehicleInTimeHours);
            rs_inminutes = resultSet.getString(dbVehicleInTimeMinutes);
            rs_vtype = resultSet.getString(dbVehicleType);
            statement.close();
            if (rs_inhours != null && rs_inminutes != null) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat hours = new SimpleDateFormat("HH");
                rs_outhours = hours.format(c.getTime());
                SimpleDateFormat minutes = new SimpleDateFormat("mm");
                rs_outminutes = minutes.format(c.getTime());
                int inhours = Integer.parseInt(rs_inhours);
                int outhours = Integer.parseInt(rs_outhours);
                int inminutes = Integer.parseInt(rs_inminutes);
                int outminutes = Integer.parseInt(rs_outminutes);
                int pminutes = Math.abs((outhours - inhours) * 60) + Math.abs(outminutes - inminutes);
                int phours = pminutes % 60;
                if (phours <= 1) {
                    pcost = "20";
                } else if (phours > 1 && phours <= 4) {
                    pcost = "40";
                } else if (phours > 4 && phours <= 12) {
                    pcost = "60";
                } else {
                    pcost = "400";
                }
            }else
            {
                rs_outhours = null;
                rs_outminutes = null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.i("Hello",clickedID);
        vnum.setText(rs_vnum+"");
        intime.setText(rs_inhours+" : "+rs_inminutes);
        outtime.setText(rs_outhours+" : "+rs_outminutes);
        cost.setText(""+pcost+" RS");
        slot.setText(clickedID);
        Log.i("Hello","in occupied slots");
        if (rs_vtype.equalsIgnoreCase("b"))
            vtype.setImageResource(R.drawable.slot_bike);
        else if (rs_vtype.equalsIgnoreCase("c"))
            vtype.setImageResource(R.drawable.slot_car);
        progressBar.setVisibility(View.INVISIBLE);
        super.onPostExecute(s);
    }
}

class DepartVehicle extends AsyncTask<String,String ,String >{
    Context context;
    String toast;
    DepartVehicle(Context context){
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String query = "UPDATE "+dbParkingTable+" SET "+dbVehicleNumber+" = NULL, "+dbVehicleType+"=NULL, "+dbVehicleInTimeHours+"=NULL, "+dbVehicleInTimeMinutes+"=NULL, "+dbVehicleOutTimeHours+"=NULL, "+dbVehicleOutTimeMinutes+"=NULL "+" WHERE "+dbVehicleSlot+"='"+clickedID+"';";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            toast = "Vehicle Departed";
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT);
        activity.finish();
        super.onPostExecute(s);
    }
}
