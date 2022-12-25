package com.ppb13937.aplikasikalkulator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editText1, editText2;
    TextView hasil;
    private static RecyclerView recHistory;
    RadioButton tambah;
    Button hitung;
    RadioButton kali;
    RadioButton bagi;
    RadioGroup group;
    public static ArrayList<History> listHistory;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "TEXT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
        loadHistory();

        hitung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitung();
            }
        });

    }

    private void initComponent() {
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        hasil = findViewById(R.id.textViewHasil);
        tambah = findViewById(R.id.radioTambah);
        kali = findViewById(R.id.radioKali);
        bagi = findViewById(R.id.radioBagi);
        hitung = findViewById(R.id.btnHitung);
        group = findViewById(R.id.radioGroup);
        recHistory = findViewById(R.id.recHistory);
        this.listHistory = new ArrayList<>();
    }



    private void hitung(){
        int num1 = (!editText1.getText().toString().matches("")) ? Integer.parseInt(editText1.getText().toString()) : 0;
        int num2 = (!editText2.getText().toString().matches("")) ? Integer.parseInt(editText2.getText().toString()) : 0;

        int radioButtonID = group.getCheckedRadioButtonId();
        View radioButton = group.findViewById(radioButtonID);

        int position = group.indexOfChild(radioButton);
        int result = 0;
        String operator = "";
        switch (position){
            case 0:
                result = num1+num2;
                operator = "+";
                break;
            case 1:
                result = num1*num2;
                operator = "*";
                break;
            case 2:
                result = num1/num2;
                operator = "/";
                break;
            case 3:
                result = num1-num2;
                operator = "-";
                break;
            default:

                break;
        }
        hasil.setText(String.valueOf(result));
        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor ed;
        if(!sharedPrefs.contains("initialized")){
            saveHistory(num1,num2,operator,result);
        }
        else{
            if(!isHistoryExist(num1,num2,operator,result)) {
                updateHistory(num1, num2, operator, result);
            }
        }
        loadHistory();
    }

    public boolean isHistoryExist(int num1,int num2,String operator, int result){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonHistory = sharedPreferences.getString("pref_data","");
        Gson gson = new Gson();
        History[] historyArray = gson.fromJson(jsonHistory, History[].class);
        if(historyArray != null) {
            for (History history : historyArray) {
                if (Integer.parseInt(history.getNum1()) == num1 && Integer.parseInt(history.getNum2()) == num2 && history.getOperator().equals(operator) && Integer.parseInt(history.getResult()) == result)
                    return true;
            }
        }
        return false;
    }



    public void saveHistory(int num1, int num2, String operator, int result) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new GsonBuilder().create();
        listHistory.add(new History(num1, num2, operator, result));
        String json = gson.toJson(listHistory);

        editor.putBoolean("initialized", true);
        editor.putString("history", json);
        editor.apply();
    }

        public void updateHistory(int num1, int num2, String operator, int result) {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            History history = new History(num1, num2, operator, result);
            listHistory.add(history);

            Gson gson = new Gson();

            String json = gson.toJson(listHistory);

            editor.putBoolean("initialized", true);
            editor.putString("pref_data", json).commit();
            editor.apply();
        }


    public void loadHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonHistory = sharedPreferences.getString("pref_data", "");
        listHistory.clear();

        Gson gson = new Gson();
        Type type = new TypeToken<List<History>>() {}.getType();
        List<History> historyList = gson.fromJson(jsonHistory, type);

        if (historyList == null || historyList.isEmpty()) {
            return;
        }

        listHistory.addAll(historyList);

        recHistory.setAdapter(new HistoryAdapter(listHistory, this));
        recHistory.setLayoutManager(new LinearLayoutManager(this));
    }


    public static void reloadData(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Gson gson = new Gson();
        String jsonHistory = gson.toJson(listHistory);

        editor.putBoolean("initialized", true);
        editor.putString("pref_data", jsonHistory).commit();
        editor.apply();

        recHistory.setAdapter(new HistoryAdapter(listHistory, context));
        recHistory.setLayoutManager(new LinearLayoutManager(recHistory.getContext()));
    }



}
