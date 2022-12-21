package com.ppb13937.aplikasikalkulator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

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
        try {
            JSONObject jsonObj = new JSONObject(jsonHistory);
            JSONArray jsonArray = jsonObj.getJSONArray("history");
            if(jsonArray.length() == 0) return false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if(obj.getInt("num1") == num1 && obj.getInt("num2") == num2 && obj.getString("operator").equals(operator) && obj.getInt("result") == result) return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void saveHistory(int num1, int num2, String operator, int result) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            JSONObject jo = new JSONObject();
            jo.put("num1", num1);
            jo.put("num2", num2);
            jo.put("operator", operator);
            jo.put("result", result);
            JSONArray ja = new JSONArray();
            listHistory.add(new History(num1, num2, operator, result));
            ja.put(jo);
            JSONObject mainObj = new JSONObject();
            mainObj.put("history", ja);
            editor.putBoolean("initialized", true);
            editor.putString("pref_data", mainObj.toString()).commit();
        } catch (JSONException json) {
        }
        editor.apply();
    }

    public void updateHistory(int num1, int num2, String operator, int result) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        History history = new History(num1, num2, operator, result);
        listHistory.add(history);

        try {
            JSONArray historyArray = new JSONArray();
            for (History h : listHistory) {
                JSONObject historyObject = new JSONObject();
                historyObject.put("num1", h.getNum1());
                historyObject.put("num2", h.getNum2());
                historyObject.put("operator", h.getOperator());
                historyObject.put("result", h.getResult());
                historyArray.put(historyObject);
            }

            JSONObject mainObject = new JSONObject();
            mainObject.put("history", historyArray);

            editor.putBoolean("initialized", true);
            editor.putString("pref_data", mainObject.toString()).commit();
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonHistory = sharedPreferences.getString("pref_data", "");
        listHistory.clear();

        try {
            JSONObject jsonObj = new JSONObject(jsonHistory);
            JSONArray mJsonArrayProperty = jsonObj.getJSONArray("history");
            if (mJsonArrayProperty.length() == 0) return;

            for (int i = 0; i < mJsonArrayProperty.length(); i++) {
                JSONObject history = mJsonArrayProperty.getJSONObject(i);
                listHistory.add(new History(history.getInt("num1"), history.getInt("num2"), history.getString("operator"), history.getInt("result")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recHistory.setAdapter(new HistoryAdapter(listHistory, this));
        recHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    public static void reloadData(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String jsonHistory = sharedPrefs.getString("pref_data", "");
        try {
            JSONObject jsonObj = new JSONObject(jsonHistory);
            JSONArray mJsonArrayProperty = jsonObj.getJSONArray("history");
            try {
                if (listHistory.size() == 0) {
                    sharedPrefs.edit().clear().commit();
                    recHistory.setAdapter(new HistoryAdapter(listHistory, context));
                    recHistory.setLayoutManager(new LinearLayoutManager(recHistory.getContext()));
                    return;
                }
                JSONArray ja = new JSONArray();
                for (int i = 0; i < listHistory.size(); i++) {
                    History history = listHistory.get(i);
                    JSONObject historyObj = new JSONObject();
                    historyObj.put("num1", history.getNum1());
                    historyObj.put("num2", history.getNum2());
                    historyObj.put("operator", history.getOperator());
                    historyObj.put("result", history.getResult());
                    ja.put(historyObj);
                }
                JSONObject mainObj = new JSONObject();
                mainObj.put("history", ja);
                editor.putBoolean("initialized", true);
                editor.putString("pref_data", mainObj.toString()).commit();
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recHistory.setAdapter(new HistoryAdapter(listHistory, context));
        recHistory.setLayoutManager(new LinearLayoutManager(recHistory.getContext()));
    }



}
