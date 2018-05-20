package com.project.rmss.parkhere;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Statement;

import static com.project.rmss.parkhere.DBContract.*;
import static com.project.rmss.parkhere.ManageSlots.*;

public class ManageSlots extends AppCompatActivity {
    NumberPicker numberPicker;
    static ProgressBar progressBar;
    static Button button;
    static boolean increment;
    static int slots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_slots);
        numberPicker = findViewById(R.id.numberPicker);
        progressBar = findViewById(R.id.manageProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        button = findViewById(R.id.commitButton);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(50);
        numberPicker.setValue(slotcount);
        increment = false;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slots = numberPicker.getValue();
                if(slots > slotcount)
                    increment = true;
                else if(slots < slotcount)
                    increment = false;
                else
                    Toast.makeText(getApplicationContext(), "Already in place!", Toast.LENGTH_SHORT).show();
                if(increment == true || increment==false) {
                    SlotManipulation slotManipulation = new SlotManipulation(getApplicationContext());
                    slotManipulation.execute("");
                }
            }
        });
    }
}

class SlotManipulation extends AsyncTask<String ,String ,String >{
    String toastMessage;
    int i = 1;
    int temp = Math.abs(slots-slotcount);
    Context context;

    SlotManipulation(Context context){
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
        try {
            String s1;
            Statement mstatement = connection.createStatement();
            Statement mstatement1 = connection.createStatement();
             if (increment == true) {
                String managequery = "INSERT INTO "+dbParkingTable+" ("+dbVehicleNumber+") VALUES (NULL);";
                while (slotcount != slots){
                    mstatement.executeUpdate(managequery);
                    slotcount = slotcount + 1;
                }
                mstatement.close();
            }
            else if (increment == false){

                String managequery1;
                String managequery2;
                while (slotcount != slots){
                    s1 = String.valueOf(slotcount);
                    managequery1 = "DELETE FROM `parkingdatabase`.`parking` WHERE `db_vehicle_slot`="+slotcount+";";
                    managequery2 = "ALTER TABLE "+dbParkingTable+"\nAUTO_INCREMENT = "+slotcount+" ;";
                    mstatement.executeUpdate(managequery1);
                    mstatement1.executeUpdate(managequery2);
                    slotcount = slotcount -1;
                }
                mstatement.close();
                mstatement1.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        progressBar.setVisibility(View.INVISIBLE);
        button.setActivated(true);
        Toast.makeText(context, "Number of slots changed", Toast.LENGTH_SHORT).show();
        super.onPostExecute(s);
    }
}
