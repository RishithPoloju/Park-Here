package com.project.rmss.parkhere;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.project.rmss.parkhere.DBContract.*;
import static com.project.rmss.parkhere.StaffConsole.*;

public class StaffConsole extends AppCompatActivity {

    static SlotsAdapter adapter;
    static ResultSet resultSet;
    static RecyclerView slotsRecyclerView;
    static GettingVehicles gd;
    static ProgressBar progressBar;
    static String clickedVnum;
    static String clickedId;
    static boolean creation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_console);
        progressBar = findViewById(R.id.slots_progress);
        slotsRecyclerView = this.findViewById(R.id.slots_recycler_view);
        slotsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gd = new GettingVehicles(getApplicationContext());
        gd.execute("");
        creation = true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Logging Out")
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        finish();
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

    @Override
    protected void onResume() {
        if(!creation) {
            gd = new GettingVehicles(getApplicationContext());
            gd.execute("");
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }
}

class GettingVehicles extends AsyncTask<String, String, String> implements SlotsAdapter.ListItemClickListener {
    private Context context;

    GettingVehicles(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String query = "select * from " + dbParkingTable + " order by "+dbVehicleSlot;
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        adapter = new SlotsAdapter(context, resultSet, slotcount, this);
        slotsRecyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
        gd.cancel(true);
        creation = false;
        gd = null;
        super.onPostExecute(s);
    }

    @Override
    public void onListItemClick(RecyclerView.ViewHolder viewHolder, int clickedItemIndex) {
        clickedId = String.valueOf(viewHolder.itemView.getTag());
        OnClicking oc = new OnClicking(context);
        oc.execute("");
    }
}
class OnClicking extends AsyncTask<String ,String ,String>{
    Context context;
    OnClicking(Context context){
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            Statement statement = connection.createStatement();
            String query = "Select * from " + dbParkingTable + " where " + dbVehicleSlot + " = '"+ clickedId +"';";
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            clickedVnum = rs.getString(dbVehicleNumber);
            statement.close();
            rs.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Intent intent;
        if(TextUtils.isEmpty(clickedVnum)) {
            intent = new Intent(context, EmptySlotsActivity.class);
        }
        else{
            intent = new Intent(context, OccupiedSlotsActivity.class);
        }
        intent.putExtra("clickedID", clickedId);
        progressBar.setVisibility(View.INVISIBLE);
        context.startActivity(intent);
        super.onPostExecute(s);
    }
}