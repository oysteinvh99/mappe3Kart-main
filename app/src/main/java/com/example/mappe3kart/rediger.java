package com.example.mappe3kart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;

public class rediger extends AppCompatActivity {
    TextView Breddegraden;
    TextView Lengdegraden;
    TextView Adresse;
    TextView Beskrivelse;
    int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rediger);
        Breddegraden = (TextView) findViewById(R.id.Breddegrad);
        Lengdegraden = (TextView) findViewById(R.id.Lengdegrad);
        Adresse = (TextView) findViewById(R.id.Adresse);
        Beskrivelse = (TextView) findViewById(R.id.beskrivelse);
        id=getIntent().getIntExtra("Id",0);
        new getJSON().execute(id);

    }
    public void Slett(View view){

            new slettLoc().execute(id);



    }
    public void EndreLokasjon(View view){
        Intent intent=new Intent(rediger.this,redigerLoc.class);
        intent.putExtra("Gateadresse",Adresse.getText());
        intent.putExtra("Breddegrad", Breddegraden.getText());
        intent.putExtra("Lengdegrad",Lengdegraden.getText());
        intent.putExtra("Beskrivelse",Beskrivelse.getText());
        intent.putExtra("Id",id);
        startActivity(intent);
        finish();

    }
    private class getJSON extends AsyncTask<Integer, Void, informasjon> {
        JSONObject jsonObject;

        @Override
        protected informasjon doInBackground(Integer... id) {
            String retur = "";
            String s = "";
            String output = "";
            int iden=id[0];
            informasjon nyInfo = new informasjon();

                try {
                    URL urlen = new URL("http://data1500.cs.oslomet.no/~s354354/jsoncha.php/?id="+iden);
                    HttpURLConnection conn = (HttpURLConnection)
                            urlen.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept",
                            "application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code :+ conn.getResponseCode()");
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));
                    System.out.println("Output from Server .... \n");
                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    try {
                        JSONArray mat = new JSONArray(output);
                            JSONObject jsonobject = mat.getJSONObject(0);
                            nyInfo.setId(jsonobject.getInt("id"));
                            nyInfo.setBeskrivelse(jsonobject.getString("Beskrivelse"));
                            nyInfo.setGateadresse(jsonobject.getString("Gateadresse"));
                            nyInfo.setBreddegrad(jsonobject.getDouble("Breddegrad"));
                            nyInfo.setLengdegrad(jsonobject.getDouble("Lengdegrad"));

                            return nyInfo;
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    return null;
                }

            return nyInfo;
        }

        @Override
        protected void onPostExecute(informasjon info) {
            Beskrivelse.setText(info.getBeskrivelse());
            Adresse.setText(info.getGateadresse());
            Breddegraden.setText(String.valueOf(info.getBreddegrad()));
            Lengdegraden.setText(String.valueOf(info.getLengdegrad()));

        }
    }
    private class slettLoc extends AsyncTask<Integer, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(Integer...id) {
            int iden=id[0];
            String retur = "";
            String s = "";
            String output = "";
            try {
                URL urlen = new URL("http://data1500.cs.oslomet.no/~s354354/jsondel.php/?id="+iden);
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
            Intent intent=new Intent(rediger.this,MapsActivity.class);
            startActivity(intent);
            finish();

        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(rediger.this,MapsActivity.class);
        startActivity(intent);
        finish();

    }
}

