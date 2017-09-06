package com.example.gianlu.fooddiary;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SymptomsAndTreatmentsActivity extends AppCompatActivity {

    DatabaseHelper app_db;
    Utils app_utils;

    SeekBar seekBarStomach;
    SeekBar seekBarBelly;
    SeekBar seekBarAntacid;
    SeekBar seekBarPPI;
    ImageView imageViewStomach;
    ImageView imageViewBelly;
    TextView textViewAntacidValue;
    TextView textViewPPIValue;
    Button buttonSaveSympAndTreat;
    Button buttonCancelSympAndTreat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_and_treatments);

        app_db = new DatabaseHelper(this);
        app_utils = new Utils();

        //        activity objects
        seekBarStomach = (SeekBar)findViewById(R.id.seekBarStomach);
        seekBarBelly = (SeekBar)findViewById(R.id.seekBarBelly);
        seekBarAntacid = (SeekBar)findViewById(R.id.seekBarAntacid);
        seekBarPPI = (SeekBar)findViewById(R.id.seekBarPPI);
        imageViewStomach = (ImageView)findViewById(R.id.imageViewStomach);
        imageViewBelly = (ImageView)findViewById(R.id.imageViewBelly);
        textViewAntacidValue = (TextView)findViewById(R.id.textViewAntacidValue);
        textViewPPIValue = (TextView)findViewById(R.id.textViewPPIValue);
        buttonSaveSympAndTreat = (Button)findViewById(R.id.buttonSaveSympAndTreat);
        buttonCancelSympAndTreat = (Button)findViewById(R.id.buttonCancelSympAndTreat);

//        Set default values
        setDefaultStomachLevel();
        setDefaultBellyLevel();
        setDefaultAntacidLevel();
        setDefaultPPILevel();

        setStomachLevel();
        setBellyLevel();
        setAntacidLevel();
        setPPILevel();

        save();
        cancel();
    }


    private void setStomachLevel(){
        seekBarStomach.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

                Drawable smile0 = getDrawable(R.drawable.smile0);
                Drawable smile1 = getDrawable(R.drawable.smile1);
                Drawable smile2 = getDrawable(R.drawable.smile2);
                Drawable smile3 = getDrawable(R.drawable.smile3);

                Integer seekBarStomachLevel = seekBarStomach.getProgress();
                switch (seekBarStomachLevel){
                    case 0:
                        imageViewStomach.setImageDrawable(smile0);
                        break;
                    case 1:
                        imageViewStomach.setImageDrawable(smile1);
                        break;
                    case 2:
                        imageViewStomach.setImageDrawable(smile2);
                        break;
                    case 3:
                        imageViewStomach.setImageDrawable(smile3);
                        break;
                }

            }
        });
    }
    private void setBellyLevel(){
        seekBarBelly.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

                Drawable smile0 = getDrawable(R.drawable.smile0);
                Drawable smile1 = getDrawable(R.drawable.smile1);
                Drawable smile2 = getDrawable(R.drawable.smile2);
                Drawable smile3 = getDrawable(R.drawable.smile3);

                Integer seekBarBellyLevel = seekBarBelly.getProgress();
                switch (seekBarBellyLevel){
                    case 0:
                        imageViewBelly.setImageDrawable(smile0);
                        break;
                    case 1:
                        imageViewBelly.setImageDrawable(smile1);
                        break;
                    case 2:
                        imageViewBelly.setImageDrawable(smile2);
                        break;
                    case 3:
                        imageViewBelly.setImageDrawable(smile3);
                        break;
                }

            }
        });
    }
    private void setAntacidLevel(){

        seekBarAntacid.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

                Integer seekBarAntacidLevel = seekBarAntacid.getProgress() * 10;
                textViewAntacidValue.setText(seekBarAntacidLevel.toString() + " ml");
            }
        });
    }
    private void setPPILevel(){

        seekBarPPI.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

                Integer seekBarPPILevel = seekBarPPI.getProgress() * 20;
                textViewPPIValue.setText(seekBarPPILevel.toString() + " mg");
            }
        });
    }

    public void cancel(){
        buttonCancelSympAndTreat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToMainActivity();
                    }
                }
        );
    }
    public void save() {
        buttonSaveSympAndTreat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Boolean sympStomachIsInserted = checkUserEntry(seekBarStomach.getProgress());
                        Boolean sympBellyhIsInserted = checkUserEntry(seekBarBelly.getProgress());
                        Boolean treatAntaIsInserted = checkUserEntry(seekBarAntacid.getProgress());
                        Boolean treatPpiIsInserted = checkUserEntry(seekBarPPI.getProgress());

                        // do not save treatments if no symptoms are specified
                        if (treatAntaIsInserted || treatPpiIsInserted)
                            if (!(sympStomachIsInserted || sympBellyhIsInserted)) {
                                Toast.makeText(SymptomsAndTreatmentsActivity.this, "For what Symptom are you taking treatments?", Toast.LENGTH_LONG).show();
                                return;
                            }

                        //Save Data
                        // Stomach
                        if (sympStomachIsInserted) {
                            //insert into sqlite DB
                            sympStomachIsInserted = app_db.insertSymptom(
                                    app_utils.getCurrentDateInteger(),
                                    app_utils.getCurrentTimestampForSQLite(),
                                    "stomach",
                                    seekBarStomach.getProgress()
                            );
                        }

                        // Belly
                        if (sympBellyhIsInserted) {
                            //insert into sqlite DB
                            sympBellyhIsInserted = app_db.insertSymptom(
                                    app_utils.getCurrentDateInteger(),
                                    app_utils.getCurrentTimestampForSQLite(),
                                    "belly",
                                    seekBarBelly.getProgress()
                            );
                        }

                        // Antacid
                        if (treatAntaIsInserted) {
                            //insert into sqlite DB
                            treatAntaIsInserted = app_db.insertTreatment(
                                    app_utils.getCurrentDateInteger(),
                                    app_utils.getCurrentTimestampForSQLite(),
                                    "antacid",
                                    seekBarAntacid.getProgress()
                            );
                        }

                        // PPI
                        if (treatPpiIsInserted) {
                            //insert into sqlite DB
                            treatPpiIsInserted = app_db.insertTreatment(
                                    app_utils.getCurrentDateInteger(),
                                    app_utils.getCurrentTimestampForSQLite(),
                                    "ppi",
                                    seekBarPPI.getProgress()
                            );
                        }

                        // Print message
                        if (sympStomachIsInserted || sympBellyhIsInserted || treatAntaIsInserted || treatPpiIsInserted) {
                            Toast.makeText(SymptomsAndTreatmentsActivity.this, "Data Saved", Toast.LENGTH_LONG).show();
                            goToMainActivity();
                        }
                        else
                            Toast.makeText(SymptomsAndTreatmentsActivity.this, "No Data To Save", Toast.LENGTH_LONG).show();

                    }
                }
        );
    }

    private void setDefaultStomachLevel(){
        Drawable myDrawable = getDrawable(R.drawable.smile0);
        imageViewStomach.setImageDrawable(myDrawable);

    }
    private void setDefaultBellyLevel(){
        Drawable myDrawable = getDrawable(R.drawable.smile0);
        imageViewBelly.setImageDrawable(myDrawable);

    }
    private void setDefaultAntacidLevel(){
        textViewAntacidValue.setText("0 ml");
    }
    private void setDefaultPPILevel(){
        textViewPPIValue.setText("0 mg");
    }

    private boolean checkUserEntry(Integer lev){
        if(lev == 0)
            return false;
        return true;
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
