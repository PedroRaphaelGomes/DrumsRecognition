package com.example.drumsrecognition;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BancoDeDados extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MOVIMENTOS";
    public static final String GESTO1 = "GESTO1";
    public static final String GESTO2 = "GESTO2";
    public static final int DATABASE_VERSION = 1;
    public BancoDeDados(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS list_info ( id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT );";
        sqLiteDatabase.execSQL(createTableQuery);
        createTableQuery = "CREATE TABLE IF NOT EXISTS GESTO1 ( id INTEGER, ordem INTEGER, x TEXT, y TEXT, z TEXT );";
        sqLiteDatabase.execSQL(createTableQuery);
        createTableQuery = "CREATE TABLE IF NOT EXISTS GESTO2 ( id INTEGER, ordem INTEGER, x TEXT, y TEXT, z TEXT );";
        sqLiteDatabase.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }



    public long inserirGesto(String TipoGesto, SQLiteDatabase db){

        ContentValues values = new ContentValues();
        values.put("type", TipoGesto);

        return db.insert("list_info", null, values);
    }

    public void inserir_dados_gesto(SQLiteDatabase db, long listId, SensorData data,String database){
        ContentValues values = new ContentValues();

        //Log.d("banco de dados", "Dentro de Inserir dados");

        List<List<String>> dadosSensor = data.getDadosSensor();

        for (int i = 0; i < dadosSensor.size() ;i++){
            Log.d("banco de dados", "Inserindo: " + i);
            values.put("id", listId);
            values.put("ordem", i);
            values.put("x", dadosSensor.get(i).get(0));
            values.put("y", dadosSensor.get(i).get(1));
            values.put("z", dadosSensor.get(i).get(2));

            db.insert(database, null, values);
        }
    }
    public List<Long> getListaIDGestos(SQLiteDatabase db, String type) {
        List<Long> ids = new ArrayList<>();

       // Log.d("banco de dados", "Dentro de getListaID");

        String query = "SELECT id FROM list_info WHERE type = ?;";
        String[] selectionArgs = {type};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        int columnIndex = cursor.getColumnIndex("id");

        while (cursor.moveToNext()) {
            //Log.d("banco de dados", "Dentro de getListaID2");
            long id = cursor.getLong(columnIndex);
            ids.add(id);
        }

        cursor.close();

        return ids;
    }

    public List<List<List<String>>> getListaGesto(String nomeTabela,List<Long> listIds,SQLiteDatabase db) {
        List<List<List<String>>> result = new ArrayList<>();
       // Log.d("banco de dados", "Dentro de getLista");

        for (long id : listIds) {
           // Log.d("banco de dados", "Dentro de getLista2");

            List<List<String>> gestoData = getGesto(nomeTabela,id,db);
           // Log.d("banco de dados", "gestoData:  "+ gestoData);
            result.add(gestoData);
        }

        return result;
    }

    private List<List<String>> getGesto(String nomeTabela, long id,SQLiteDatabase db) {
        List<List<String>> gestoData = new ArrayList<>();

        String[] colunas = {"x", "y", "z"};
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(id)};
        String orderBy = "ordem";

        Cursor cursor = db.query(nomeTabela, colunas, whereClause, whereArgs, null, null, orderBy);

        /*String query = "SELECT x, y, z FROM GESTO1 WHERE id = ? ORDER BY ordem";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.rawQuery(query, selectionArgs);*/


        int columnIndexX = cursor.getColumnIndex("x");
        int columnIndexY = cursor.getColumnIndex("y");
        int columnIndexZ = cursor.getColumnIndex("z");

        while (cursor.moveToNext()) {
            String x = cursor.getString(columnIndexX);
            String y = cursor.getString(columnIndexY);
            String z = cursor.getString(columnIndexZ);

            List<String> dimensions = new ArrayList<>();
            dimensions.add(x);
            dimensions.add(y);
            dimensions.add(z);

            gestoData.add(dimensions);
            //dimensions.clear();
        }

        cursor.close();

        return gestoData;
    }
}
