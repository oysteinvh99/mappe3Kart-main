package com.example.mappe3kart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class leggTil extends AppCompatActivity {
    double breddegrad;
    double lengdegrad;
    TextView Breddegraden;
    TextView Lengdegraden;
    TextView Adresse;
    EditText Beskrivelse;
    String holder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_legg_til);
        Breddegraden = (TextView) findViewById(R.id.Breddegrad);
        Lengdegraden = (TextView) findViewById(R.id.Lengdegrad);
        Adresse = (TextView) findViewById(R.id.Adresse);
        Beskrivelse = (EditText) findViewById(R.id.beskrivelse);
        breddegrad=getIntent().getDoubleExtra("Breddegrad",0);
        lengdegrad=getIntent().getDoubleExtra("Lengdegrad",0);
        Breddegraden.setText(String.valueOf(breddegrad));
        Lengdegraden.setText(String.valueOf(lengdegrad));
        LatLng latLng=new LatLng(breddegrad,lengdegrad);
        new GetLocationTask(breddegrad,lengdegrad).execute();


    }
    public void leggTil(View view){
        String sjekker=Beskrivelse.getText().toString();
        if (sjekker.equals("")){
            Toast.makeText(leggTil.this,"Må ha en beskrivelse", Toast.LENGTH_SHORT).show();


        }
        else {
            informasjon info = new informasjon(Beskrivelse.getText().toString(), Adresse.getText().toString(), breddegrad, lengdegrad);
            new leggInn().execute(info);
        }

        }





    private class GetLocationTask extends AsyncTask<Void, Void, String> {
        JSONObject jsonObject;
        double bredde;
        double lengde;
        String address;
        public GetLocationTask(double b,double l) {
            this.bredde=b;
            this.lengde=l;
        }

        @Override
        protected String doInBackground(Void...params) {
            String s = "";
            String output = "";
            String query = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+bredde+","+lengde+"&key=AIzaSyCSw8TvEdjW6SMHI3bq1HTpRK0qZBvITNo";
            try {
                URL urlen = new URL(query);
                HttpURLConnection conn = (HttpURLConnection)
                        urlen.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }
                BufferedReader br = new BufferedReader(new
                        InputStreamReader((conn.getInputStream())));
                while ((s = br.readLine()) != null) {
                    output = output + s;
                }
                jsonObject = new JSONObject(output.toString());
                conn.disconnect();
                address= ((JSONArray)
                        jsonObject.get("results")).getJSONObject(0).getString("formatted_address");
                JSONArray liste= ((JSONArray)
                        jsonObject.get("results")).getJSONObject(0).getJSONArray("types");
                holder= (String) liste.get(0);
                return address.toString();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return address.toString();
        }
        @Override
        protected void onPostExecute(String resultat) {
            Adresse.setText(resultat);

        }
    }


    private class leggInn extends AsyncTask<informasjon, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(informasjon... info) {
            double bredde = info[0].getBreddegrad();
            double lengde = info[0].getLengdegrad();
            String besk = info[0].getBeskrivelse();
            String adr = info[0].getGateadresse();
            String retur = "";
            String s = "";
            String output = "";
            try {
                URL urlen = new URL("http://data1500.cs.oslomet.no/~s354354/jsonin.php/?Beskrivelse=" + besk + "&Gateadresse=" + adr + "&Breddegrad=" + bredde + "&Lengdegrad=" + lengde);
                HttpURLConnection conn = (HttpURLConnection)
                        urlen.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept",
                        "application/json");
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code :+ conn.getResponseCode()");
                }

                conn.disconnect();


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "ok";
        }

        @Override
        protected void onPostExecute(String ss) {

            Intent intent = new Intent(leggTil.this, MapsActivity.class);

            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(leggTil.this,MapsActivity.class);
        startActivity(intent);
        finish();

    }
}