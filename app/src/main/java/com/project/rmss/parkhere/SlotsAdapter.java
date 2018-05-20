package com.project.rmss.parkhere;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.project.rmss.parkhere.R;

import java.sql.ResultSet;

import static com.project.rmss.parkhere.DBContract.*;


public class SlotsAdapter extends RecyclerView.Adapter<SlotsAdapter.SlotsViewHolder> {
    private ResultSet mResultSet;
    private Context mContext;
    private int count;
    final private SlotsAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener{
        void onListItemClick(RecyclerView.ViewHolder viewHolder, int clickedItemIndex);
    }
    public SlotsAdapter(Context context, ResultSet resultSet, int count, SlotsAdapter.ListItemClickListener listener){
        this.mContext = context;
        this.mResultSet = resultSet;
        this.count = count;
        this.mOnClickListener = listener;
    }

    @Override
    public SlotsAdapter.SlotsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.slots_recycler_view, parent, false);
        return new SlotsAdapter.SlotsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SlotsAdapter.SlotsViewHolder holder, int position) {
        String vnum;
        String id;
        String vtype;
        try {
            if (!mResultSet.next()) {
                return;
            }
            vnum = mResultSet.getString(dbVehicleNumber);
            id = String.valueOf(mResultSet.getInt(dbVehicleSlot));
            vtype = mResultSet.getString(dbVehicleType);
            if(TextUtils.isEmpty(vnum)){
                vnum = "Vacant";
                holder.img_type.setImageResource(R.drawable.slot_empty);
            }
            else if(vtype.equalsIgnoreCase("c")){
                holder.img_type.setImageResource(R.drawable.slot_car);
            }
            else if(vtype.equalsIgnoreCase("b")){
                Log.i("H",vtype);
                holder.img_type.setImageResource(R.drawable.slot_bike);
            }
            holder.tv_vnum.setText(vnum);
            holder.tv_id.setText(id);
            holder.itemView.setTag(id);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return count;
    }

    class SlotsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tv_vnum;
        public TextView tv_id;
        public ImageView img_type;

        public SlotsViewHolder(View itemView){
            super(itemView);
            tv_vnum = itemView.findViewById(R.id.slot_tv_vnum);
            tv_id = itemView.findViewById(R.id.slots_tv_id);
            img_type = itemView.findViewById(R.id.slots_vtype);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(this,clickedPosition);
        }
    }
}
