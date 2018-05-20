package com.project.rmss.parkhere;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.project.rmss.parkhere.DBContract.*;
import static com.project.rmss.parkhere.AdminConsole.*;

public class AdminConsole extends AppCompatActivity{
    static ProgressBar progressBar;
    static StaffAdapter adapter;
    static Statement statement = null;
    static ResultSet resultSet;
    static RecyclerView staffRecyclerView;
    static GettingData gd;
    static boolean creation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        staffRecyclerView = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_console);
        staffRecyclerView = this.findViewById(R.id.recycler_view);
        staffRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gd = new GettingData(getApplicationContext());
        progressBar = findViewById(R.id.admin_progress);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        if(!creation) {
            gd = new GettingData(getApplicationContext());
            gd.execute("");
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.recruit_item) {
            startActivity(new Intent(this, RecruitStaff.class));
        }
        if (id == R.id.manage_slots){
            startActivity(new Intent(this, ManageSlots.class));
        }
        return true;
    }
}


class GettingData extends AsyncTask<String, String, String> implements StaffAdapter.ListItemClickListener {
    Context context;

    GettingData(Context context) {
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
            String query = "select * from " + dbStaffTable + " order by "+dbStaffId;
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            Toast.makeText(context, e.getStackTrace().toString(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        adapter = new StaffAdapter(context, resultSet, count, this);
        staffRecyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
        gd.cancel(true);
        creation = false;
        gd = null;
        super.onPostExecute(s);
    }

    @Override
    public void onListItemClick(RecyclerView.ViewHolder viewHolder, int clickedItemIndex) {
        String id = String.valueOf(viewHolder.itemView.getTag());
        Intent intent = new Intent(context, StaffActivity.class);
        intent.putExtra("clickedID", id);
        context.startActivity(intent);
    }
}
