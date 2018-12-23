package com.example.karimm7mad.vehicletracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TransmiterForm extends AppCompatActivity {

    public DatabaseReference firebaseDBman = null;

    public EditText carkeyEditTxt, userkeyEditTxt;
    public String carKeyEntered, userKeyEntered;
    public AlertDialog.Builder builder = null;
    public Button submitBtn, checkCarBtn, checkUserBtn;
    public static boolean isUserExist;
    public static boolean isCarExist;
    public DBAdapter localDBman;
    public Intent goToTransmitterMapIntent;
    public CountDownTimer subDownTimerUser;
    public CountDownTimer subDownTimerCar;
    public ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmiter_form);
        this.loading = findViewById(R.id.determinateBar);
        this.loading.setProgress(100);
        this.goToTransmitterMapIntent = new Intent(TransmiterForm.this, TransmiterMapsActivity.class);
        TransmiterForm.isCarExist = false;
        TransmiterForm.isUserExist = false;
        //firebase and timer to handle the searchings for values
        this.firebaseDBman = FirebaseDatabase.getInstance().getReference();
        this.subDownTimerCar = new CountDownTimer(4000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                loading.setProgress((int) ((4000 - millisUntilFinished) / 40));
                carkeyEditTxt.setFocusable(false);
                carkeyEditTxt.setClickable(false);
                checkCarBtn.setClickable(false);
                if (!TransmiterForm.isCarExist) {
                    userKeyEntered = userkeyEditTxt.getText().toString();
                    carKeyEntered = carkeyEditTxt.getText().toString();
                    firebaseDBman = FirebaseDatabase.getInstance().getReference("Cars").child(userKeyEntered);
                    firebaseDBman.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dn : dataSnapshot.getChildren()) {
                                if (carKeyEntered.equals(dn.getKey())) {
                                    TransmiterForm.isCarExist = true;
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onFinish() {
                loading.setProgress(100);
                if (TransmiterForm.isCarExist) {
                    submitBtn.setClickable(true);
                    submitBtn.setFocusable(true);
                    submitBtn.setVisibility(View.VISIBLE);
                } else {
                    carkeyEditTxt.setFocusable(true);
                    carkeyEditTxt.setClickable(true);
                    checkCarBtn.setClickable(true);
                    showPopUp("Car Doesn't Exist");
                }
            }
        };
        this.subDownTimerUser = new CountDownTimer(4000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                loading.setProgress((int) ((4000 - millisUntilFinished) / 40));
                userkeyEditTxt.setClickable(false);
                userkeyEditTxt.setFocusable(false);
                checkUserBtn.setClickable(false);
                if (!TransmiterForm.isUserExist) {
                    userKeyEntered = userkeyEditTxt.getText().toString();
                    firebaseDBman = FirebaseDatabase.getInstance().getReference("Users");
                    firebaseDBman.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dn : dataSnapshot.getChildren()) {
                                if (userKeyEntered.equals(dn.getKey())) {
                                    TransmiterForm.isUserExist = true;
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                }
            }

            @Override
            public void onFinish() {
                loading.setProgress(100);
                if (TransmiterForm.isUserExist) {
                    carkeyEditTxt.setVisibility(View.VISIBLE);
                    checkCarBtn.setVisibility(View.VISIBLE);
                    checkCarBtn.setClickable(true);
                    checkCarBtn.setFocusable(true);
                } else {
                    userkeyEditTxt.setClickable(true);
                    userkeyEditTxt.setFocusable(true);
                    checkUserBtn.setClickable(true);
                    showPopUp("User Doesn't Exist");

                }

            }
        };
        //get the Views References
        this.userkeyEditTxt = findViewById(R.id.userEditTxtTr);
        this.checkUserBtn = findViewById(R.id.checkUserBtn);
        this.checkUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setProgress(0);
                subDownTimerUser.start();
            }
        });
        //car data
        this.carkeyEditTxt = findViewById(R.id.keyEditTxtTr);
        this.carkeyEditTxt.setVisibility(View.GONE);

        this.checkCarBtn = findViewById(R.id.checkCarBtn);
        this.checkCarBtn.setClickable(false);
        this.checkCarBtn.setFocusable(false);
        this.checkCarBtn.setVisibility(View.GONE);
        this.checkCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setProgress(0);
                subDownTimerCar.start();
            }
        });

        //submit all Data
        this.submitBtn = findViewById(R.id.submitBtnTr);
        this.submitBtn.setClickable(false);
        this.submitBtn.setFocusable(false);
        this.submitBtn.setVisibility(View.GONE);
        this.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localDBman = new DBAdapter(getBaseContext());
                localDBman.open();
                localDBman.deleteAll();
                localDBman.insertRow("CAR_" + carKeyEntered);
                localDBman.close();
                goToTransmitterMapIntent.putExtra("carkey", carKeyEntered);
                TransmiterForm.isCarExist = false;
                TransmiterForm.isUserExist = false;
                startActivity(goToTransmitterMapIntent);
            }
        });
    }

    public void showPopUp(String str) {
        this.builder = new AlertDialog.Builder(this);
        this.builder.setTitle("Pop UP");
        this.builder.setMessage(str);
        this.builder.setNeutralButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        this.builder.create().show();
    }


}
