package com.example.mappe3kart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class redigerLoc extends AppCompatActivity {
    TextView Breddegraden;
    TextView Lengdegraden;
    TextView Adresse;
    EditText Beskrivelse;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rediger_loc);
        Breddegraden = (TextView) findViewById(R.id.Breddegrad);
        Lengdegraden = (TextView) findViewById(R.id.Lengdegrad);
        Adresse = (TextView) findViewById(R.id.Adresse);
        Beskrivelse = (EditText) findViewById(R.id.beskrivelse);
        hentEkstra();

    }
    public void hentEkstra(){
        String breddegrad=getIntent().getStringExtra("Breddegrad");
        String lengdegrad=getIntent().getStringExtra("Lengdegrad");
        String adresse=getIntent().getStringExtra("Gateadresse");
        String beskrivelse=getIntent().getStringExtra("Beskrivelse");
        id=getIntent().getIntExtra("Id",0);
        Lengdegraden.setText(String.valueOf(lengdegrad));
        Breddegraden.setText(String.valueOf(breddegrad));
        Adresse.setText(adresse);
        Beskrivelse.setText(beskrivelse);


    }
    public void LagreEndring(View view){
        String sjekker=Beskrivelse.getText().toString();
        if (sjekker.equals("")){
            Toast.makeText(redigerLoc.this,"Må ha en beskrivelse", Toast.LENGTH_SHORT).show();
        }
        else {
            informasjon info = new informasjon();
            info.setId(id);
            info.setBeskrivelse(Beskrivelse.getText().toString());
            new EndreLoc().execute(info);
        }


    }

    private class EndreLoc extends AsyncTask<informasjon, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(informasjon...info) {
            int iden=info[0].getId();
            String infoen=info[0].getBeskrivelse();
            try {
                URL urlen = new URL("http://data1500.cs.oslomet.no/~s354354/jsonend.php/?Beskrivelse="+infoen+"&id="+iden);
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
            Intent intent=new Intent(redigerLoc.this,rediger.class);
            intent.putExtra("Id",id);
            startActivity(intent);
            finish();

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(redigerLoc.this,rediger.class);
        intent.putExtra("Id",id);
        startActivity(intent);
        finish();

    }
}