package com.example.securedeliverypackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.app.NotificationChannel;
import android.app.Dialog;

import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    TextView editTextDate, editTextTime, textLockState, textBoxState, textTemperature;
    Dialog alertDialog;
    DatabaseReference setDB = FirebaseDatabase.getInstance().getReference().child("PorchBox").child("1-set");
    DatabaseReference lockDB = setDB.child("Servo State");
    DatabaseReference boxDB = setDB.child("Box State");
    DatabaseReference tempDB = setDB.child("Temperature");
    DatabaseReference lfDB = FirebaseDatabase.getInstance().getReference().child("PorchBox").child("3-LockFlag");
    private static final String TAG = "Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Date and Time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        String currentTime = simpleDateFormat.format(calendar.getTime());

        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        textLockState = findViewById(R.id.textLockState);
        textBoxState = findViewById(R.id.textBoxState);
        textTemperature = findViewById(R.id.textTemperature);

        alertDialog = new Dialog(this);

        editTextDate.setText(currentDate);
        editTextTime.setText(currentTime + " PM");
        // Security ALERT
        if((textLockState.equals("LOCKED")) && (textBoxState.equals("OPEN"))){
            alertDialog.setContentView(R.layout.alert_popup);
            alertDialog.getWindow();
        }

        // Lock State
         lockDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(String.valueOf(snapshot.getValue(Integer.class)).equals("90")){
                    textLockState.setText("LOCKED");
                    textLockState.setTextColor(Color.RED);
                } else {
                    textLockState.setText("UNLOCKED");
                    textLockState.setTextColor(Color.GREEN);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
        /*lfDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check Servo Motor State
                if(textLockState.getText().equals("LOCKED")){
                    // Locking -> Servo State: 90
                    lfDB.setValue(1);
                } else {
                    // Unlocking -> Servo State: 0
                    lfDB.setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        }); */
        // Box State
        boxDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textBoxState.setText(snapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

        // Temperature
        tempDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tempText = String.valueOf(snapshot.getValue(Float.class));
                textTemperature.setText(tempText + " °F");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

        // Lock Actuation
        Button buttonLock = (Button) findViewById(R.id.buttonLock);
        if(textLockState.equals("LOCKED")){
            buttonLock.setBackgroundColor(Color.RED);
        } else  {
            buttonLock.setBackgroundColor(Color.GREEN);
        }
        buttonLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                lfDB.setValue(1);
            }
        });
    }
    // Refresh Time
    /* public void time(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        String currentTime = simpleDateFormat.format(calendar.getTime());
        editTextDate.setText(currentTime);
        refresh(1000);
    }
    private void refresh(int milliseconds){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run(){
                time();
            }
        };
        handler.postDelayed(runnable, milliseconds);
    } */
}
