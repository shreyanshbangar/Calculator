package com.shreyansh.calculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyansh.calculator.models.Operation;

import java.util.ArrayList;

public class CalculatorActivity extends AppCompatActivity {

    private static final String TAG = "Firebase";
    CustomEditText inputET;
    AppCompatTextView tv_result;
    AppCompatButton btn_ANS;
    Character lastChar='a';
    int previousLength=0;
    int answer =0;
    String userName="";
    private DatabaseReference mDatabase;

    static ArrayList<Operation> listOfOperations=new ArrayList<Operation>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        inputET= findViewById(R.id.inputET);
        tv_result= findViewById(R.id.tv_result);
        btn_ANS = findViewById(R.id.btn_ANS);
        userName= getIntent().getStringExtra("UserName");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fetchDataFromFirebase(userName);

        btn_ANS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inputET.setText(Integer.toString(answer));
                tv_result.setText("");
            }
        });

        inputET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))){

                    String lastIndexChar =  inputET.getText().toString().substring(inputET.getText().toString().length() - 1);
                    //Last char can't be operator
                    if(lastIndexChar.equals("*") || lastIndexChar.equals("/") || lastIndexChar.equals("+")|| lastIndexChar.equals("-"))
                    {
                        Toast.makeText(CalculatorActivity.this, "Last Character can't be a Operator", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Calculator calc = new Calculator();
                        answer = (int)(calc.compute(inputET.getText().toString()));
                        AddOperationToHistoryList(inputET.getText().toString(), Integer.toString(answer));

                        tv_result.setText(Integer.toString(answer));

                    }
                    return true;
                }
                else{
                    return false;
                }
            }
        });

        inputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                if(charSequence.length()<1)return;
                previousLength=charSequence.length();
                lastChar=charSequence.charAt(charSequence.length()-1);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String latestText=inputET.getText().toString();
                //first char can't be operator.
                if(latestText.length()==1)
                {
                    if(latestText.equals("*") || latestText.equals("/") || latestText.equals("+")|| latestText.equals("-")) {
                        inputET.setText("");
                        return;
                    }
                }

                //User Can't Enter Two Operators(same or different) together.
                if(latestText.length()<=previousLength)return;

                if(lastChar=='*' || lastChar=='/' || lastChar=='+'|| lastChar=='-')
                {

                    latestText =  latestText.substring(latestText.length() - 1);
                    if(latestText.equals("*") || latestText.equals("/") || latestText.equals("+")|| latestText.equals("-"))
                    {
                        inputET.setText(ChangeText());

                    }
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to Sign Out?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        listOfOperations.clear();
                        CalculatorActivity.super.onBackPressed();
                    }
                }).create().show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calculator_options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.history:
                Intent historyIntent= new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(historyIntent);
                return true;
            case R.id.sync:
                UpdateDataOnFirebase(userName);
                return true;
            case R.id.SignOut:
                listOfOperations.clear();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void UpdateDataOnFirebase(String UserName) {

        mDatabase.child("users").child(UserName).setValue(listOfOperations);
    }

    private void fetchDataFromFirebase(String userName) {

        mDatabase.child("users").child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot Operations : snapshot.getChildren()) {
                    String input="";
                    String output="";
                    Log.e(TAG, "onDataChange: Operations-> "+Operations );
                    for (DataSnapshot singleOperation : Operations.getChildren()) {
                        Log.e(TAG, "onDataChange: singleOperation-> "+singleOperation );
                         if(singleOperation.getKey().equals("input"))
                         {
                             input= singleOperation.getValue().toString();
                         }
                        else
                        {
                            output= singleOperation.getValue().toString();
                        }
                        //listOfOperations.add(Integer.valueOf( child.getKey()), new Operation());
                    }
                    listOfOperations.add(Integer.valueOf( Operations.getKey()), new Operation(input, output));
                    if(listOfOperations.size()==11)
                        listOfOperations.remove(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void AddOperationToHistoryList(String input, String output) {

        Operation mOperation= new Operation(input, output);

        listOfOperations.add(mOperation);
        if(listOfOperations.size()==11)
            listOfOperations.remove(0);
    }

    private String ChangeText() {

        StringBuffer sb= new StringBuffer();
        String textInputET= inputET.getText().toString();

        String  lastChar=textInputET.substring(textInputET.length()-1);
        sb.append(textInputET.substring(0,textInputET.length() - 2));
        sb.append(lastChar);
        return  sb.toString();
    }
}

