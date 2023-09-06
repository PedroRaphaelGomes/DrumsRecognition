package com.example.drumsrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    TextView dado, dado1, dado2;
    Button btntreino,btncompara,btninserir;

    SensorManager sensorManager;
    Sensor sensor;

    BancoDeDados bancoDeDados;

    private MediaPlayer mTuc, mTis;

    SQLiteDatabase db;

    ImageView instrument;

    List<List<List<String>>> result = new ArrayList<>();

    List<String> dados = new ArrayList<>();
    List<List<String>> lista1 = new ArrayList<List<String>>();
    List<List<String>> lista2 = new ArrayList<List<String>>();

    List<String> lista_de_gestos = Arrays.asList("GESTO1", "GESTO2");

    Boolean Treino = false, Compara = false;
    long timeStart = 0;
    //Boolean dtw = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instrument = findViewById(R.id.imgInstrumento);
        //instrument.setVisibility(View.INVISIBLE);


        mTuc = MediaPlayer.create(this,R.raw.tuc);
        mTis = MediaPlayer.create(this,R.raw.ts);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        dado = findViewById(R.id.txtDist);

        btncompara = findViewById(R.id.btnCompara);
        btntreino = findViewById(R.id.btnTreino);
        btninserir = findViewById(R.id.btnInserir);

        Log.d("Base de dados", "criar base de dados");
        bancoDeDados = new BancoDeDados(this);
        db = bancoDeDados.getReadableDatabase();
        // db.close();
        // Log.d("Base de dados", "fechar base de dados");

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

        btninserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OLA", "Clicou Inserir");
                if(lista1.size() > 0){
                    Log.d("banco de dados", "antes de salvar ");

                    SensorData sensorData1 = new SensorData(lista1);

                    long idGesto = bancoDeDados.inserirGesto("GESTO2",db);

                    bancoDeDados.inserir_dados_gesto(db,idGesto,sensorData1,"GESTO2");

                    for (int i = 0; i < lista_de_gestos.size();i++){
                        List<Long> ids = new ArrayList<>();
                        Log.d("TAG", "gesto : " + lista_de_gestos.get(i));
                        ids = bancoDeDados.getListaIDGestos(db,lista_de_gestos.get(i));

                        result = bancoDeDados.getListaGesto(lista_de_gestos.get(i),ids,db);
                        Log.d("TAG", "i" + i);
                        Log.d("TAG", "size result" + result.size());

                    }
                }

                lista1.clear();
                Log.d("OLA", "Gesto inserido: "+ bancoDeDados.getListaGesto("GESTO 2", bancoDeDados.getListaIDGestos(db, "GESTO 1"),db).size());



            }
        });

        btncompara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OLA", "Clicou Compara");
                //Compara = true;
                timeStart = System.currentTimeMillis();
                String GestoAtual = "";
                int qt1 = 0;
                int qt2 = 0;
                double dist1 = 0;
                double dist2 = 0;

                if(!Compara){

                    start();
                    Log.d("OLA", "Compara Start");
                    Compara = true;

                }else{
                    stop();

                    MDDTW mddtw_banco;
                    String resul = "";

                    //Log.d("resultado do getGest", "get gest" +bancoDeDados.getListaGesto("GESTO1",ids,db));

                    SensorData sensorData1 = new SensorData(lista1);
                    double menorDIstancia = Double.POSITIVE_INFINITY;;
                    String gesto_mais_proximo = "não encontrado";

                    for (int i = 0; i < lista_de_gestos.size();i++){
                        List<Long> ids = new ArrayList<>();
                        Log.d("TAG", "gesto : " + lista_de_gestos.get(i));
                        ids = bancoDeDados.getListaIDGestos(db,lista_de_gestos.get(i));

                        List<List<List<String>>> result = bancoDeDados.getListaGesto(lista_de_gestos.get(i),ids,db);
                        Log.d("TAG", "i" + i);
                        Log.d("TAG", "size result" + result.size());

                        for (int j = 0 ; j < result.size();j++){

                            Log.d("TAG", "j" + j);
                            SensorData sensorData2 = new SensorData(result.get(j));
                            mddtw_banco = new MDDTW(sensorData1, sensorData2);
                            Log.d("TAG", "distancia : " +mddtw_banco.getDistancia());

                            if(i == 0){
                                qt1 = qt1 + 1;
                                dist1 = dist1 + mddtw_banco.getDistancia();
                            }

                            if(i == 1){
                                qt2 = qt2 + 1;
                                dist2 = dist2 + mddtw_banco.getDistancia();
                            }

                            if (mddtw_banco.getDistancia() < menorDIstancia){
                                Log.d("TAG", "gesto menor encontrado" + mddtw_banco.getDistancia());
                                menorDIstancia = mddtw_banco.getDistancia();
                                gesto_mais_proximo = lista_de_gestos.get(i);
                            }
                        }
                        //resul = "" + mddtw_banco.getDistancia();
                    }
                    //dado.setText("" + mddtw_banco.getDistancia());
                    Compara = false;

                    if(dist1/qt1 < dist2/qt2 && dist1/qt1 <= 80){
                        instrument.setImageResource(R.drawable.music_bass_dram_icon2);
                        tuq();
                    }else {
                        if (dist2/qt2 <= 80){
                            instrument.setImageResource(R.drawable.music_cymbals_icon);
                            tis();
                        }

                    }
                    Log.d("OLA","Distancia1: " + dist1/qt1 + " Quantidade: " + qt1);
                    Log.d("OLA","Distancia2: " + dist2/qt2 + " Quantidade: " + qt2);

                    Log.d("OLA", "Lista1: " + lista1.size() + ": " + lista1);
                    lista1.clear();

                    Log.d("OLA", "Lista1-clear: " +lista1.size() + ": "+ lista1);
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

    private void tuq(){
        mTuc.start();
    }

    private void tis(){
        mTis.start();
    }


}