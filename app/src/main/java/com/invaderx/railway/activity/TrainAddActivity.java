package com.invaderx.railway.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invaderx.railway.R;
import com.invaderx.railway.adapters.TrainsAdapter;
import com.invaderx.railway.auth.LoginActivity;
import com.invaderx.railway.models.Trains;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.ArrayList;


public class TrainAddActivity extends AppCompatActivity {
    CheckBox ma1;
    CheckBox ma2;
    CheckBox ma3;
    CheckBox msl;
    CheckBox mcc;
    private CheckBox sun,mon,tue,wed,thu,fri,sat;

    LinearLayout mLa1;
    LinearLayout mLa2;
    LinearLayout mLa3;
    LinearLayout mLsl;
    LinearLayout mLcc;
    EditText trainNumber;
    EditText trainName;
    EditText sourcePlace;
    EditText sourceTime;
    EditText destPlace;
    EditText destTime;
    EditText oneASeat;
    EditText oneAFair;
    EditText twoASeat;
    EditText twoAFair;
    EditText threeASeat;
    EditText threeAFair;
    EditText slSeat;
    EditText slFair;
    EditText ccSeat;
    EditText ccFair;
    String addTrain = null;
    FloatingActionButton trainSaveButton;

    private Button addStaions;
    private ArrayList<String> staionList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView staionsListView;
    private String TNumber = "";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private int m=0,t=0,w=0,th=0,f=0,sa=0,s=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_add);
        getSupportActionBar().setElevation(0f);
        //database references
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        //for getting intent data and updating title
        Intent intent = getIntent();
        addTrain = intent.getExtras().getString("add");
        TNumber= intent.getStringExtra("TrainNum");
        trainName = findViewById(R.id.train_name);
        trainNumber = findViewById(R.id.train_number);
        sourcePlace = findViewById(R.id.source_place);
        sourceTime = findViewById(R.id.source_time);
        destPlace = findViewById(R.id.dest_place);
        destTime = findViewById(R.id.dest_time);
        oneAFair = findViewById(R.id.one_a_fair);
        oneASeat = findViewById(R.id.one_a_seat);
        twoAFair = findViewById(R.id.two_a_fair);
        twoASeat = findViewById(R.id.two_a_seat);
        threeAFair = findViewById(R.id.three_a_fair);
        threeASeat = findViewById(R.id.three_a_seat);
        slFair = findViewById(R.id.sl_fair);
        slSeat = findViewById(R.id.sl_seat);
        ccFair = findViewById(R.id.cc_fair);
        ccSeat = findViewById(R.id.cc_seat);
        trainSaveButton = findViewById(R.id.train_save_button);
        ma1 =  findViewById(R.id.a1);
        ma2 =  findViewById(R.id.a2);
        ma3 =  findViewById(R.id.a3);
        msl =  findViewById(R.id.sl);
        mcc =  findViewById(R.id.cc);

        mLa1 =  findViewById(R.id.a1linearlayout);
        mLa2 =  findViewById(R.id.a2linearlayout);
        mLa3 =  findViewById(R.id.a3linearlayout);
        mLsl =  findViewById(R.id.sllinearlayout);
        mLcc =  findViewById(R.id.cclinearlayout);

        sun=findViewById(R.id.sun);
        mon=findViewById(R.id.mon);
        tue=findViewById(R.id.tue);
        wed=findViewById(R.id.wed);
        thu=findViewById(R.id.thu);
        fri=findViewById(R.id.fri);
        sat=findViewById(R.id.sat);

        addStaions=findViewById(R.id.addStaions);
        staionsListView=findViewById(R.id.staionsListView);

        if (addTrain.equals("add")) {
            setTitle("Add Train");
            invalidateOptionsMenu();

        }
        else {
            setTitle("Update Train");
            getTrainData(TNumber);

        }


        ma1.setOnClickListener(view -> {
            if (ma1.isChecked()) {
                mLa1.setVisibility(View.VISIBLE);
            } else {
                mLa1.setVisibility(View.GONE);
            }
        });
        ma2.setOnClickListener(view -> {
            if (ma2.isChecked()) {
                mLa2.setVisibility(View.VISIBLE);
            } else {
                mLa2.setVisibility(View.GONE);
            }
        });
        ma3.setOnClickListener(view -> {
            if (ma3.isChecked()) {
                mLa3.setVisibility(View.VISIBLE);
            } else {
                mLa3.setVisibility(View.GONE);
            }
        });
        msl.setOnClickListener(view -> {
            if (msl.isChecked()) {
                mLsl.setVisibility(View.VISIBLE);
            } else {
                mLsl.setVisibility(View.GONE);
            }
        });
        mcc.setOnClickListener(view -> {
            if (mcc.isChecked()) {
                mLcc.setVisibility(View.VISIBLE);
            } else {
                mLcc.setVisibility(View.GONE);
            }
        });

        trainSaveButton.setOnClickListener(view -> saveButton());

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,staionList);

       addStaions.setOnClickListener(v -> {
            addStaionsDetails();
       });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addtrain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_train) {
            //Add delete function here
            deleteTrain(TNumber);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new train, hide the "Delete" menu item.
        if (addTrain.equals("add")) {
            MenuItem menuItem = menu.findItem(R.id.delete_train);
            menuItem.setVisible(false);
        }
        return true;
    }

    public void saveButton() {
        String strainName = trainName.getText().toString().trim();
        String strainNumber = trainNumber.getText().toString().trim();
        String ssourcePlace = sourcePlace.getText().toString().trim();
        String ssourceTime = sourceTime.getText().toString().trim();
        String sdestPLace = destPlace.getText().toString().trim();
        String sdestTime = destTime.getText().toString().trim();

        if (strainName.isEmpty()) {
            trainName.setError("Train Name is required");
            trainName.requestFocus();
            return;
        }
        if (strainNumber.isEmpty()) {
            trainNumber.setError("Train Number is required");
            trainNumber.requestFocus();
            return;
        }
        if (ssourcePlace.isEmpty()) {
            sourcePlace.setError("Source Place is required");
            sourcePlace.requestFocus();
            return;
        }
        if (ssourceTime.isEmpty()) {
            sourceTime.setError("Source Time is required");
            sourceTime.requestFocus();
            return;
        }
        if (sdestPLace.isEmpty()) {
            destPlace.setError("Destination PLace is required");
            destPlace.requestFocus();
            return;
        }
        if (sdestTime.isEmpty()) {
            destTime.setError("Destination Time is required");
            destTime.requestFocus();
            return;
        }

        //1A
        if(ma1.isChecked()){
            if(oneAFair.getText().toString().isEmpty())
                oneAFair.setError("Enter Fair");
            if(oneASeat.getText().toString().isEmpty())
                oneASeat.setError("Enter Number of seats");
        }else{
            oneASeat.setText("0");
            oneAFair.setText("0");
        }

        //2A
        if(ma2.isChecked()){
            if(twoAFair.getText().toString().isEmpty())
                twoAFair.setError("Enter Fair");
            if(twoASeat.getText().toString().isEmpty())
                twoASeat.setError("Enter Number of seats");
        }else{
            twoASeat.setText("0");
            twoAFair.setText("0");
        }

        //3A
        if(ma3.isChecked()){
            if(threeAFair.getText().toString().isEmpty())
                twoAFair.setError("Enter Fair");
            if(threeASeat.getText().toString().isEmpty())
                threeASeat.setError("Enter Number of seats");
        }else{
            threeASeat.setText("0");
            threeAFair.setText("0");
        }

        //CC
        if(mcc.isChecked()){
            if(ccFair.getText().toString().isEmpty())
                ccFair.setError("Enter Fair");
            if(ccSeat.getText().toString().isEmpty())
                ccSeat.setError("Enter Number of seats");
        }else{
            ccFair.setText("0");
            ccSeat.setText("0");
        }

        //SL
        if(msl.isChecked()){
            if(slFair.getText().toString().isEmpty())
                slFair.setError("Enter Fair");
            if(slSeat.getText().toString().isEmpty())
                slSeat.setError("Enter Number of seats");
        }else{
            slSeat.setText("0");
            slFair.setText("0");
        }

        //monday
        if(mon.isChecked())
            m=1;
        else
            m=0;

        //tuesday
        if(tue.isChecked())
            t=1;
        else
            t=0;

        //wednesday
        if(wed.isChecked())
            w=1;
        else
            w=0;

        //thursday
        if(thu.isChecked())
            th=1;
        else
            th=0;

        //friday
        if(fri.isChecked())
            f=1;
        else
            f=0;

        //saturday
        if(sat.isChecked())
            sa=1;
        else
            sa=0;

        //sunday
        if(sun.isChecked())
            s=1;
        else
            s=0;

        updateAddTrain();





    }


    public void getTrainData(String tNo){
        ProgressDialog dialog = ProgressDialog.show(this, "Sit back and relax",
                "Loading. Please wait...", true);

        databaseReference.child("Trains").orderByChild("tNumber").equalTo(tNo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dialog.dismiss();
                        Trains trains1=null;
                        Log.v("DataTrain", String.valueOf(dataSnapshot.getValue(Trains.class)));
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot t : dataSnapshot.getChildren()) {
                                trains1 = t.getValue(Trains.class);
                            }
                            trainNumber.setText(trains1.gettNumber());
                            trainName.setText(trains1.gettName());
                            //1A check box
                            if(!(trains1.getClass1A().equals("0") || trains1.getSeat1A()==0)) {
                                ma1.setChecked(true);
                                oneAFair.setText(trains1.getClass1A());
                                oneASeat.setText(""+trains1.getSeat1A());
                                mLa1.setVisibility(View.VISIBLE);
                            }

                            //2A check box
                            if(!(trains1.getClass2A().equals("0") || trains1.getSeat2A()==0)) {
                                ma2.setChecked(true);
                                twoAFair.setText(trains1.getClass2A());
                                twoASeat.setText(""+trains1.getSeat2A());
                                mLa2.setVisibility(View.VISIBLE);
                            }

                            //3A check box
                            if(!(trains1.getClass3A().equals("0") || trains1.getSeat3A()==0)) {
                                ma3.setChecked(true);
                                threeAFair.setText(trains1.getClass3A());
                                threeASeat.setText(""+trains1.getSeat3A());
                                mLa3.setVisibility(View.VISIBLE);
                            }

                            //SL check box
                            if(!(trains1.getClassSL().equals("0") || trains1.getSeatSL()==0)) {
                                msl.setChecked(true);
                                slFair.setText(trains1.getClassSL());
                                slSeat.setText(""+trains1.getSeatSL());
                                mLsl.setVisibility(View.VISIBLE);
                            }

                            //CC check box
                            if(!(trains1.getClassCC().equals("0") || trains1.getSeatCC()==0)) {
                                mcc.setChecked(true);
                                ccFair.setText(trains1.getClassCC());
                                ccSeat.setText(""+trains1.getSeatCC());
                                mLcc.setVisibility(View.VISIBLE);
                            }

                            //sunday
                            if(trains1.getdSun()==1)
                                sun.setChecked(true);
                            //monday
                            if(trains1.getdMon()==1)
                                mon.setChecked(true);
                            //tuesday
                            if(trains1.getdTue()==1)
                                tue.setChecked(true);
                            //wednesday
                            if(trains1.getdWed()==1)
                                wed.setChecked(true);
                            //thursday
                            if(trains1.getdThur()==1)
                                thu.setChecked(true);
                            //friday
                            if(trains1.getdFri()==1)
                                fri.setChecked(true);
                            //saturday
                            if(trains1.getdSat()==1)
                                sat.setChecked(true);

                            sourcePlace.setText(trains1.getStations().get(0));
                            destPlace.setText(trains1.getStations().get(trains1.getStations().size()-1));
                            String [] times = trains1.getTime().split("-");
                            sourceTime.setText(times[0]);
                            destTime.setText(times[1]);
                            for(int i = 0 ; i<trains1.getStations().size();i++)
                            staionList.add(trains1.getStations().get(i));
                            staionList.remove(0);
                            staionList.remove(staionList.size()-1);

                            staionsListView.setAdapter(adapter);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public void updateAddTrain(){
        staionList.add(0,sourcePlace.getText().toString());
        staionList.add(destPlace.getText().toString());

        Trains trainUpdate = new Trains(sourceTime.getText().toString()+"-"+destTime.getText().toString(),
                oneAFair.getText().toString(),twoAFair.getText().toString(),threeAFair.getText().toString(),
                ccFair.getText().toString(),slFair.getText().toString(),f,m,sa,s,th,t,w,
                Integer.parseInt(oneASeat.getText().toString()),
                Integer.parseInt(twoASeat.getText().toString()),
                Integer.parseInt(threeASeat.getText().toString()),
                Integer.parseInt(ccSeat.getText().toString()),
                Integer.parseInt(slSeat.getText().toString()),
                staionList,trainName.getText().toString(),trainNumber.getText().toString());
        databaseReference.child("Trains").child(trainNumber.getText().toString()).setValue(trainUpdate)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
                    finish();
                });

    }

    public void addStaionsDetails(){
        new LovelyTextInputDialog(this)
                .setTopColorRes(R.color.colorPrimaryDark)
                .setTitle("Add Station")
                .setMessage("Enter station name")
                .setIcon(R.drawable.trainwatermark)
                .setConfirmButton(android.R.string.ok, text -> {
                    staionList.add(text.toLowerCase());
                    staionsListView.setAdapter(adapter);
                })
                .show();
    }

    public void deleteTrain(String tno){
        new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(android.R.color.holo_red_dark)
                .setButtonsColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.cancel_button)
                .setTitle("Delete Train")
                .setMessage("Are you sure you want to delete this train?")
                .setPositiveButton(android.R.string.ok, v -> databaseReference.child("Trains").child(tno).setValue(null)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(TrainAddActivity.this, "Train Deleted Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }))
                .setNegativeButton(android.R.string.no, null)
                .show();

    }

}
