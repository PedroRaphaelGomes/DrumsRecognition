package com.example.drumsrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    TextView dado, dado1, dado2;
    Button btntreino,btncompara;

    SensorManager sensorManager;
    Sensor sensor;

    List<String> dados = new ArrayList<>();
    List<List<String>> lista1 = new ArrayList<List<String>>();
    List<List<String>> lista2 = new ArrayList<List<String>>();

    Boolean Treino = false, Compara = false;
    long timeStart = 0;
    //Boolean dtw = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        dado = findViewById(R.id.txtDist);

        btncompara = findViewById(R.id.btnCompara);
        btntreino = findViewById(R.id.btnTreino);

        btntreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OLA", "Clicou Treino");
                //Treino = true;
                timeStart = System.currentTimeMillis();

                if(!Treino){

                    start();
                    Log.d("OLA", "Treino Start");
                    Treino = true;

                }else{
                    stop();
                    //lista1.add(dados);
                    //dados.clear();

                    Treino = false;
                }

            }
        });

        btncompara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OLA", "Clicou Compara");
                //Compara = true;
                timeStart = System.currentTimeMillis();

                if(!Compara){

                    start();
                    Log.d("OLA", "Compara Start");
                    Compara = true;

                }else{
                    stop();
                    MDDTW mddtw;

                    SensorData sensorData1 = new SensorData(lista1);
                    SensorData sensorData2 = new SensorData(lista2);

                    Log.d("OLA", "X, Y e Z : " + "X1: " + sensorData1.getXData() + "X2: " + sensorData2.getXData());
                    mddtw = new MDDTW(sensorData1,sensorData2);
                    Log.d("OLA", "Distancia: " + mddtw.getDistancia());
                    dado.setText("" + mddtw.getDistancia());

                    lista1.clear();
                    lista2.clear();
                }
            }
        });

        if(sensor == null){
            //dado.setText("Aqui");
            finish();

        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        String gravity0 = Float.toString(sensorEvent.values[0]);
        String gravity1 = Float.toString(sensorEvent.values[1]);
        String gravity2 = Float.toString(sensorEvent.values[2]);

        dados.add(gravity0);
        dados.add(gravity1);
        dados.add(gravity2);

        if(Treino){
            Log.d("OLA", "Treino Adicinando");
            lista1.add(new ArrayList<String>(dados));
            dados.clear();
        }

        if(Compara){
            Log.d("OLA", "Compara Adicinando");
            lista2.add(new ArrayList<String>(dados));
            dados.clear();
        }

        //Log.d("OLA", "onSensorChanged: Treino Start" + " "  + gravity0 + " "  + gravity2 + " "  + gravity1);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void start(){
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stop(){
        sensorManager.unregisterListener(this);
    }



}