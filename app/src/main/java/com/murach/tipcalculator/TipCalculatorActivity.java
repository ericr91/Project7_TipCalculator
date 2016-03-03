package com.murach.tipcalculator;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class TipCalculatorActivity extends Activity 
implements OnEditorActionListener, OnClickListener {

    // define variables for the widgets
    private EditText billAmountEditText;
    private TextView percentTextView;   
    private Button   percentUpButton;
    private Button   percentDownButton;
    private TextView tipTextView;
    private TextView totalTextView;
    private Button saveButton;
    private Button deleteButton;
    private TipCalculatorDB db = new TipCalculatorDB(this);
    
    // define instance variables that should be saved
    private String billAmountString = "";
    private float tipPercent = .15f;
    
    // set up preferences
    private SharedPreferences prefs;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);
        
        // get references to the widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        saveButton = (Button) findViewById(R.id.saveButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        
        // get default SharedPreferences object
        prefs = PreferenceManager.getDefaultSharedPreferences(this);        
    }
    
    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = prefs.edit();        
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();        

        super.onPause();      
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // get the instance variables
        billAmountString = prefs.getString("billAmountString", "");
        tipPercent = prefs.getFloat("tipPercent", tipPercent);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);
        
        // calculate and display

        float tipPercentAverage = 0;
        float y = 0;
        ArrayList<Tip> tips = new ArrayList<Tip>();
        tips = db.getTips();
        int x = 0;

        for(Tip t : tips){
            y = t.getTipPercent();
            tipPercentAverage = y + tipPercentAverage;
            x++;
            String bill = t.getBillAmountFormatted();
            String tip = t.getTipPercentFormatted();
            String date = t.getDateStringFormatted();
            Log.d(bill,"Bill Amount");
            Log.d(tip,"Tip Percentage");
            Log.d(date,"Date");
        }
        tipPercent = tipPercentAverage / x;
        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));
        calculateAndDisplay();
    }
    
    public void calculateAndDisplay() {        

        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount;
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }
        
        // calculate tip and total 
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;
        
        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));
        
        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));
    }
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
    		actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }        
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.percentDownButton:
                tipPercent = tipPercent - .01f;
                calculateAndDisplay();
                break;

            case R.id.percentUpButton:
                tipPercent = tipPercent + .01f;
                calculateAndDisplay();
                break;

            case R.id.saveButton:
                billAmountString = billAmountEditText.getText().toString();
                float bill;
                if (billAmountString.equals("")) {
                    bill = 0;
                }
                else {
                    bill = Float.parseFloat(billAmountString);
                }
                db.saveTips(System.currentTimeMillis(), bill, tipPercent);
                billAmountEditText.setText("");
                tipTextView.setText("");
                totalTextView.setText("");
                break;
            case R.id.deleteButton:
                db.deleteTipTable();
                tipPercent = .15f;
                break;
        }
    }


}