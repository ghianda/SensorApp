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
    - FARE I DUE METODI CREATE URL
    - mettere il metodo ParseXmlUrl nell'onclick del bottone LAST READ
    - {eventuale} implementare un check button "ALL" accanto allo spinner sensor che "oscura" lo spinner e
      toglie il sensore specifico dall'url, così fa il last reading di tutto il meter
    */


public abstract class AbstractReadingActivity extends AppCompatActivity {
    //Attribute --------------------------------
    protected final static SensorList allSensors = new SensorList(); //lista di coppie (meter -> elenco sensori)
    protected static ArrayAdapter<String> spinMeterAdapter;
    protected static ArrayAdapter<String> spinSensorAdapter;

    protected Meter chosenMeter; //meter selezionato
    protected Sensor chosenSensor; //sensore scelto

    protected String url;


    //Method -------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(myView());

        //preparazione degli spinner dei Sensori
        setSensorsSpinner();


    }


    //to override in de derived classes
    public abstract int myView();

    public abstract int getIdMeterSpinner();

    public abstract int getIdSensorSpinner();

    public abstract void createUrl();


    //Not Abstract Method - button read method
    public void read(View view) {
        System.out.println("sto eseguendo il metodo read !!--!!--!!--!!--!!--!!--!!--!!--!!");

        createUrl();

        ParseXmlUrl();

    }


    // ====== metodo di rihiesta get XML (simpleXmlRequest with Netsens class parsing ======
    public void ParseXmlUrl() {
        //TODO da togliee
        System.out.println("ParseXML_URL -------->");
        System.err.println(url);

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

                        //TODO da togliere
                        System.out.println("Netsens_onResponse()");
                        //TODO RESTITUIRO' I RISULTATI PER ELA. GRAFICA SUCCESSIVA

                        System.out.println("Netsens_ getMeasures().getClass(): ");

                        response.getMeasuresList().forEach(measure ->
                                System.out.println(measure.getMeter() + " " + measure.getValue()));
                        /*
                        for(Measure m : response.getMeasuresList()){
                            System.out.println(m.getMeter()+"  "+m.getValue());
                        }
                        */

                    },

                    (VolleyError error) -> {
                        //override ErrorListener method
                        System.err.println("onErrorResponse() - errore libreria Volley");
                        System.err.println(error.getMessage());
                        //TODO gestire un messaggio di erore (toast?) senz far crashare il prog
                    }
            );


            // Add the request to the RequestQueue.
            queue.add(simpleRequest_netsens);   //agg richiesta con nodi di tipo netsens per url vera
            // fine get___________________________________________________________________

        } else {
            //no connessione disponibile: avviso
            Toast.makeText(this, R.string.text_toast_net_error, Toast.LENGTH_SHORT).show();
        }
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
                //TODO verificare che funzioni la sostituzione
                //spinSensorAdapter.addAll(allSensors.getSensorsNamesByIdMeter((int) id));
                spinSensorAdapter.addAll(allSensors.getSensorsNamesByMeter(chosenMeter));

                sensorSpinner.setAdapter(spinSensorAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //TODO possibile semplificazione con lambda expression...
        //definizione del setOnItemSelectedListener per SensorSpinner
        sensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //salvataggio Oggetto Sensor scelto
                //TODO CONTROLLRE L'ogetto ricevuto
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
