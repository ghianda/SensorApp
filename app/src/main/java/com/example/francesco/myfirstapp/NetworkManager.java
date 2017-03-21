package com.example.francesco.myfirstapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;

import static com.example.francesco.myfirstapp.SensorProjectApp.windowYesterdayConsumeRequest;

/**
 * Created by francesco on 26/02/2017.
 */

public class NetworkManager
{
    private static final String TAG = "NetworkManager";
    private static NetworkManager instance = null;
    private Context context;

    //for Volley API
    public RequestQueue requestQueue;




    public NetworkManager(Context context)
    {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }




    public static synchronized NetworkManager getInstance(Context context)
    {
        if (null == instance)
            instance = new NetworkManager(context);
        return instance;
    }




    public static synchronized NetworkManager getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }







    public void getNetsensRequest(String url, final CustomListener<Netsens> listener) {
        //connecting
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            SimpleXmlRequest<Netsens> simpleRequest_netsens = new SimpleXmlRequest<Netsens>(Request.Method.GET, url, Netsens.class,
                    new Response.Listener<Netsens>() {
                        @Override
                        public void onResponse(Netsens response) {

                            // override onResponse method
                            if (response.getMeasuresList() != null) {

                                listener.getResult(response);
                            }

                            else {
                                //Toast.makeText(context.getApplicationContext(),
                                //        R.string.text_toast_no_data, Toast.LENGTH_SHORT).show();
                                //clearValueTextView();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {//override ErrorListener method
                            //Toast toast = Toast.makeText(context.getApplicationContext(),
                            //        R.string.text_toast_net_error, Toast.LENGTH_SHORT);
                            //toast.show();
                        }
                    });

            requestQueue.add(simpleRequest_netsens);
        }
        else {
            //no connessione disponibile: avviso
            //Toast.makeText(context, R.string.text_toast_net_error, Toast.LENGTH_SHORT).show();
        }
    }





    public String createWindowUrl(Meter chosenMeter, Sensor chosenSensor, Calendar cal, long window){

        //create the date of cal date and window millis before
        final long toMillis = cal.getTimeInMillis();
        final long fromMillis = toMillis - window; // now - 15 minutes

        return createUrl(chosenMeter, chosenSensor, fromMillis, toMillis);


    }

    public String createWindowUrl(String meterUrl, String parUrl, long toMillis){

        final long fromMillis = toMillis - windowYesterdayConsumeRequest; // toMillis - window millis

        return  context.getString(R.string.urlDomain)
                + context.getString(R.string.m) + meterUrl + parUrl
                + context.getString(R.string.f) + fromMillis
                + context.getString(R.string.t) + toMillis;

    }

    public String createLastReadUrl(Meter chosenMeter, Sensor chosenSensor) {

        return context.getString(R.string.urlDomain)
                + context.getString(R.string.m)
                + chosenMeter.getUrlString()
                + chosenSensor.getUrlString()
                + context.getString(R.string.lr);
    }




    public String createTimeReadUrl(Meter chosenMeter, Sensor chosenSensor, Calendar fromDate, Calendar toDate) {

        //read date and hour from Date and Hour Picker
        long fromMillis = fromDate.getTimeInMillis();
        long toMillis = toDate.getTimeInMillis();

        return createUrl(chosenMeter, chosenSensor, fromMillis, toMillis);
    }




    private String createUrl(Meter chosenMeter, Sensor chosenSensor, long fromMillis, long toMillis){

        return  context.getString(R.string.urlDomain)
                + context.getString(R.string.m) + chosenMeter.getUrlString() + chosenSensor.getUrlString()
                + context.getString(R.string.f) + fromMillis
                + context.getString(R.string.t) + toMillis;
    }





    public String createLastReadUrl(String meterUrl, String parUrl) {

        return context.getString(R.string.urlDomain)
                + context.getString(R.string.m)
                + meterUrl
                + parUrl
                + context.getString(R.string.lr);
    }


}
