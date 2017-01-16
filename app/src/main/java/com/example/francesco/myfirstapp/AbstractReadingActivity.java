package com.example.francesco.myfirstapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

/**
 * Created by francesco on 31/12/2016.
 */

/*TODO
        V - fare il salvataggio del sensore scelto nell'onclick listener del secondo spinnner (CHE E' ANCORA DA FARE)
        V - FARE Il METODO CREATE URL
        V - mettere il metodo ParseXmlUrl nell'onclick del bottone LAST READ

    */


public abstract class AbstractReadingActivity extends AppCompatActivity {
    //Attribute --------------------------------
    protected final static SensorList allSensors = new SensorList(); //lista di coppie (meter -> elenco sensori)
    protected static ArrayAdapter<String> spinMeterAdapter;
    protected static ArrayAdapter<String> spinSensorAdapter;

    protected static Meter chosenMeter; //meter selezionato
    protected static Sensor chosenSensor; //sensore scelto

    protected String url;


    //Method -------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(myView());

        //preparazione degli spinner dei Sensori
        setSensorsSpinner();


    }


    //to override in the derived classes
    public abstract int myView();

    public abstract int getIdMeterSpinner();

    public abstract int getIdSensorSpinner();

    public abstract void createUrl();

    public abstract void displayResult(Netsens response, Meter chosenMeter, Sensor chosenSensor);


    //Not Abstract Method - button read method
    public void read(View view) {
        System.out.println("sto eseguendo il metodo read !!--!!--!!--!!--!!--!!--!!--!!--!!");

        createUrl();
        System.err.println("url: " + url);
        ParseXmlUrl();


    }


    public void storeResult(Netsens response) {
        //store the response data in Global Sensor Attribute
        ((SensorProjectApp) this.getApplication()).setGlobalData(
                response, chosenMeter, chosenSensor);
    }


    /* Not Abstract Method -
    *    ====== metodo di rihiesta get XML (simpleXmlRequest with Netsens class parsing ======
    *   produce avviso con mumero di record ottenuti in caso di risposta affermativa del server
    */
    public void ParseXmlUrl() {

        //connecting
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // avviso di connessione effettuata
            Toast.makeText(this, R.string.text_toast_net_ok, Toast.LENGTH_SHORT).show();

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);

            //Request a XML response from the provided URL
            SimpleXmlRequest<Netsens> simpleRequest_netsens = new SimpleXmlRequest<Netsens>(
                    Request.Method.GET, url, Netsens.class,
                    (Netsens response) -> {
                        // override onResponse method
                        storeResult(response);
                        displayResult(response, chosenMeter, chosenSensor);

                        displayCountRecord(response);

                    },

                    (VolleyError error) -> {
                        //override ErrorListener method
                        System.err.println("onErrorResponse() - errore libreria Volley");
                        System.err.println(error.getMessage());
                        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
            );

            // Add the request to the RequestQueue.
            queue.add(simpleRequest_netsens);   //agg richiesta
            // fine get___________________________________________________________________

        } else {
            //no connessione disponibile: avviso
            Toast.makeText(this, R.string.text_toast_net_error, Toast.LENGTH_SHORT).show();
        }
    }


    public void displayCountRecord(Netsens response) {
        Toast.makeText(this,
                new StringBuilder().append("Ricevuti ")
                        .append(response.getMeasuresList().size()).append(" risultati!")
                , Toast.LENGTH_SHORT).show();

    }



    /* Not Abstract Method -
     * Set the value in the meter Spinner and set update method for Sensor Spinner
     */
    public void setSensorsSpinner() {

        //spinner object
        final Spinner meterSpinner = (Spinner) findViewById(getIdMeterSpinner());
        final Spinner sensorSpinner = (Spinner) findViewById(getIdSensorSpinner());

        //dichiarazione Spinner adapters
        spinMeterAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        spinSensorAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);

        // setting MeterAdapter e applicazione allo spinner
        spinMeterAdapter.addAll(allSensors.getMetersName());
        meterSpinner.setAdapter(spinMeterAdapter);

        //definizione del setOnItemSelectedListener per MeterSpinner
        meterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //salvataggio Oggetto Meter scelto
                chosenMeter = allSensors.getMeterById((int) id);

                //aggiornamento sensorSpinner in base al Meter scelto
                spinSensorAdapter.clear();
                spinSensorAdapter.addAll(allSensors.getSensorsNamesByMeter(chosenMeter));

                sensorSpinner.setAdapter(spinSensorAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //definizione del setOnItemSelectedListener per SensorSpinner
        sensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //salvataggio Oggetto Sensor scelto
                //TODO CONTROLLARE L'ogetto ricevuto
                chosenSensor = allSensors.getSensor(chosenMeter, (int) id);
                System.out.println("sensore selezionato ---> ");
                System.out.println(chosenSensor.getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

}
