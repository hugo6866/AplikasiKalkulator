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
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText editText1;
    private EditText editText2;
    private TextView resultTextView;
    private static RecyclerView historyRecyclerView;
    private RadioButton addRadioButton;
    private Button calculateButton;
    private RadioButton multiplyRadioButton;
    private RadioButton divideRadioButton;
    private RadioGroup operatorRadioGroup;
    public static ArrayList<History> historyList;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        loadHistory();

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculate();
            }
        });
    }

    private void initComponents() {
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        resultTextView = findViewById(R.id.textViewHasil);
        addRadioButton = findViewById(R.id.radioTambah);
        calculateButton = findViewById(R.id.btnHitung);
        multiplyRadioButton = findViewById(R.id.radioKali);
        divideRadioButton = findViewById(R.id.radioBagi);
        operatorRadioGroup = findViewById(R.id.radioGroup);
        historyRecyclerView = findViewById(R.id.recHistory);
        this.historyList = new ArrayList<>();
    }

    private void calculate() {
        int num1 = (!editText1.getText().toString().matches("")) ? Integer.parseInt(editText1.getText().toString()) : 0;
        int num2 = (!editText2.getText().toString().matches("")) ? Integer.parseInt(editText2.getText().toString()) : 0;

        int checkedRadioButtonId = operatorRadioGroup.getCheckedRadioButtonId();
        View radioButton = operatorRadioGroup.findViewById(checkedRadioButtonId);

        int position = operatorRadioGroup.indexOfChild(radioButton);
        int result = 0;
        String operator = "";
        switch (position) {
            case 0:
                result = num1 + num2;
                operator = "+";
                break;
            case 1:
                result = num1 * num2;
                operator = "*";
                break;
            case 2:
                result = num1 / num2;
                operator = "/";
                break;
            case 3:
                result = num1 - num2;
                operator = "-";
                break;
            default:
                break;
        }
        resultTextView.setText(String.valueOf(result));
        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor;
         if (!isHistoryExist(num1, num2, operator)) {
                saveHistory(num1, num2, operator, result);
         }
        
    }

    private boolean isHistoryExist(int num1, int num2, String operator) {
        for (History history : historyList) {
            if (Integer.parseInt(history.getNum1()) == num1 && Integer.parseInt(history.getNum2()) == num2 && history.getOperator().equals(operator)) {
                return true;
            }
        }
        return false;
    }

    private void saveHistory(int num1, int num2, String operator, int result) {
        History history = new History(num1, num2, operator, result);
        historyList.add(history);
        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        editor.putString(TEXT, json);
        editor.apply();
        editor.putBoolean("initialized", true);
        editor.apply();
        reloadData(this);
    }

    public static void reloadData(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        try {
            JSONArray jsonArray = new JSONArray();
            for (History history : historyList) {
                JSONObject obj = new JSONObject();
                obj.put("num1", history.getNum1());
                obj.put("num2", history.getNum2());
                obj.put("operator", history.getOperator());
                obj.put("result", history.getResult());
                jsonArray.put(obj);
            }

            JSONObject mainObj = new JSONObject();
            mainObj.put("history", jsonArray);
            editor.putString(TEXT, mainObj.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        HistoryAdapter historyAdapter = new HistoryAdapter(historyList, context);
        historyRecyclerView.setAdapter(historyAdapter);
    }


    private void loadHistory() {
        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPrefs.getString(TEXT, "");
        History history;
        if (text != null) {
            try {
                JSONArray jsonArray = new JSONArray(text);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    history = new History();
                    history.setNum1(obj.getInt("num1"));
                    history.setNum2(obj.getInt("num2"));
                    history.setOperator(obj.getString("operator"));
                    history.setResult(obj.getInt("result"));
                    historyList.add(history);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        HistoryAdapter historyAdapter = new HistoryAdapter(historyList, this);
        historyRecyclerView.setAdapter(historyAdapter);
    }
}
