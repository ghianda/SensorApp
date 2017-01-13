package com.example.francesco.myfirstapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;


public class MainActivity extends AppCompatActivity {

    //key of input form
    public final static String EXTRA_MESSAGE = "com.example.francesco.MESSAGE";
    public final static String EXTRA_SENSOR = "com.example.francesco.SENSOR";
    //public final static String EXTRA_FROM_TIME = "com.example.francesco.FROM";
    //public final static String EXTRA_TO_TIME = "com.example.francesco.TO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //menu a tendina______________________________________________________________
        Spinner spinner = (Spinner) findViewById(R.id.sensor_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meterName_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);



    }



    /*  "PARSE" button onclick method:    */
    public void ParseRequest(View view){
        System.out.println("ParseRequest-------------->>");
        //creating an Intent
        Intent intent = new Intent(this, DisplayResultActivity.class);

        //defining fields
        //EditText editTextFrom = (EditText) findViewById(R.id.fromTime);
        // idem per TO time
        Spinner spinner = (Spinner)findViewById(R.id.sensor_spinner);
        System.out.println(spinner.getSelectedItem().toString());

        //getting values
        //String fromTime = editTextFirstname.getText().toString();
        //idem per to time
        String chosenSensor = spinner.getSelectedItem().toString();
        System.out.println("value getted");
        //put data in the intent
        //intent.putExtra(FIRSTNAME, firstname);
        intent.putExtra(EXTRA_SENSOR, chosenSensor);
        System.out.println("value are put in the intent");

        startActivity(intent);
    }



    /*  invocate quando pigio il bottone "Send"    */
    public void sendMessage(View view){
        //creating a intent
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        //definig fields
        EditText editText = (EditText) findViewById(R.id.edit_message);
        //getting the fields value
        String message = editText.getText().toString();
        //put data in yhe intent
        intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }



    // ====== metodo di rihiesta get XML (NETSENS)======
    public void connectNetworkXml(View view) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // avviso di connessione effettuata
            Toast.makeText(this,R.string.text_toast_net_ok,Toast.LENGTH_SHORT).show();

            // FETCH DATA_________________________________________________________________
            final TextView mTextView = (TextView) findViewById(R.id.text_response);

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);

            String url = "http://live.netsens.it/export/xml_export_2A.php?user=temp&password=5lkz1d&station=723&meter=QG/Lighting&from=1480503600000&to=1480507200000" ;


            final int nChar = 100;

            //Request a XML response from the provided URL (Netsens).
            SimpleXmlRequest<Netsens> simpleRequest_netsens = new SimpleXmlRequest<Netsens>(Request.Method.GET, url, Netsens.class,
                    new Response.Listener<Netsens>()
                    {
                        @Override
                        public void onResponse(Netsens response) {
                            // response Object
                            //mTextView.setText("Response is: "+ response.substring(0,nChar));
                            //System.out.println(response.substring(0,nChar));
                            //mTextView.setText("Netsens_onResponse()");
                            System.out.println("Netsens_onResponse()");


                            System.out.println("Netsens_ getMeasures().getClass(): ");
                            for(Measure m : response.getMeasuresList()){
                                System.out.println(m.getMeter()+" "+m.getValue());
                            }
                            System.out.println(response.getMeasuresList());

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error Object
                            mTextView.setText("onErrorResponse()");
                            System.out.println("onErrorResponse()");
                        }
                    }
            );



            // Add the request to the RequestQueue.
            queue.add(simpleRequest_netsens);   //agg richiesta con nodi di tipo netsens per url vera
            // fine get___________________________________________________________________

        } else {
            //no connessione disponibile: avviso
            Toast.makeText(this,R.string.text_toast_net_error,Toast.LENGTH_SHORT).show();
        }
    }


}
