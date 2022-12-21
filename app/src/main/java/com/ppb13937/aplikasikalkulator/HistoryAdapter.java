package com.ppb13937.aplikasikalkulator;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    public ArrayList<History> listHistory;
    private Context context;

    public HistoryAdapter(ArrayList<History> listHistory, Context context) {
        this.listHistory = listHistory;
        this.context = context;
    }


    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder = new ViewHolder(inflater.inflate(R.layout.history_item, parent,false));

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        History history = listHistory.get(position);
        holder.txtNum1.setText(history.getNum1());
        holder.txtNum2.setText(history.getNum2());
        holder.txtOperator.setText(history.getOperator());
        holder.txtHasil.setText(history.getResult());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Toast.makeText(holder.itemView.getContext(),"longClick!",Toast.LENGTH_LONG).show();
                /*
                Jika dilakukan tap panjang, maka aplikasi akan menampilkan pilihan "Hapus riwayat" yang
                 ketika dipilih akan menghapus suatu riwayat perhitungan tertentu.
                 */
                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Hapus riwayat")
                        .setNegativeButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listHistory.remove(position);
                                MainActivity.reloadData(view.getContext());
                            }
                        })
                        .setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNum1,txtNum2,txtOperator,txtHasil;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNum1 = (TextView) itemView.findViewById(R.id.txtNum1);
            txtNum2 = (TextView) itemView.findViewById(R.id.txtNum2);
            txtOperator = (TextView) itemView.findViewById(R.id.txtOperator);
            txtHasil = (TextView) itemView.findViewById(R.id.txtHasil);
        }
    }
}
