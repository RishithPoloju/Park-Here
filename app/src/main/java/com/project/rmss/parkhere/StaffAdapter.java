package com.project.rmss.parkhere;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import static com.project.rmss.parkhere.DBContract.*;


public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder>{

    private ResultSet mResultSet;
    private Context mContext;
    private int count;
    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener{
        void onListItemClick(RecyclerView.ViewHolder viewHolder, int clickedItemIndex);
    }
    public StaffAdapter(Context context, ResultSet resultSet, int count, ListItemClickListener listener){
        this.mContext = context;
        this.mResultSet = resultSet;
        this.count = count;
        this.mOnClickListener = listener;
    }

    @Override
    public StaffViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.staff_recycler_view, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StaffViewHolder holder, int position) {
        String name;
        String id;
        try {
            if (!mResultSet.next()) {
                return;
            }
            name = mResultSet.getString(dbStaffUname);
            id = String.valueOf(mResultSet.getInt(dbStaffId));
            holder.tv_name.setText(name);
            holder.tv_id.setText(id);
            holder.itemView.setTag(id);
        }
        catch (Exception ex){
        }

    }

    @Override
    public int getItemCount() {
        return count;
    }

    class StaffViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tv_name;
        public TextView tv_id;

        public StaffViewHolder(View itemView){
            super(itemView);
            tv_name = itemView.findViewById(R.id.srv_tv_sname);
            tv_id = itemView.findViewById(R.id.srv_tv_id);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(this,clickedPosition);
        }
    }

}
