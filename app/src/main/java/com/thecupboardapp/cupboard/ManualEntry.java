package com.thecupboardapp.cupboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ManualEntry extends AppCompatActivity {

    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);
        setTitle("New Food Item");

        EditText edittext= (EditText) findViewById(R.id.editText5);
        ImageButton theDateButt = (ImageButton) findViewById(R.id.imageButton);
        theDateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ManualEntry.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button addButt = (Button)findViewById(R.id.button2);
        addButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edittext= (EditText) findViewById(R.id.editText3);
                EditText edittext2= (EditText) findViewById(R.id.editText5);
                String theName = edittext.getText().toString();
                String theDate = edittext2.getText().toString();

                Intent resultInt = new Intent();
                resultInt.putExtra("Result", "Done");

                if(!theName.isEmpty()) {
                    //If user enters in no expiration date, it will default a value far in the future
                    if(theDate.isEmpty()){
                        Date expDate = new Date(Long.parseLong("4133987474999"));
                        myCalendar.setTime(expDate);
                    }
                    FoodItem theFoodToBeAdded = new FoodItem(theName, myCalendar);

                    Calendar theDateAdded = Calendar.getInstance();
                    theDateAdded.getTime();
                    theFoodToBeAdded.setDateAdded(theDateAdded);

                    UserData.get(ManualEntry.this).addFoodItem(theFoodToBeAdded);
                    //go to the next screen passing FoodItem in...
                    setResult(RESULT_OK, resultInt);
                    finish();
                }
                else {
                    //signal to user required fields...
                }

            }

        });

        Button cancelButt = (Button)findViewById(R.id.button3);
        cancelButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultInt = new Intent();
                resultInt.putExtra("Result", "Done");

                setResult(RESULT_CANCELED, resultInt);
                finish();
            }
        });
    }

    public static Intent newIntent(Context packageContext, UUID manualEntryId) {
        Intent intent = new Intent(packageContext, ManualEntry.class);
        return intent;
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        EditText edittext= (EditText) findViewById(R.id.editText5);
        edittext.setText(sdf.format(myCalendar.getTime()));
    }
}
