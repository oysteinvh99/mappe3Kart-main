package com.example.mappe3kart;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mappe3kart.databinding.ActivityMapsBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private GoogleApiClient mGoogleApiClient;
    List<informasjon>markerList=new ArrayList<>();
    String holder="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View view = mapFragment.getView();
        view.setClickable(true);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Setter opp kart
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getJSON infList=new getJSON();
        infList.execute(new
                String[]{"http://data1500.cs.oslomet.no/~s354354/jsout.php"});


        //Registrerer hvis kart blir trykket på
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker arg0) {
                    Intent intent = new Intent(MapsActivity.this, rediger.class);
                    int id = (int) arg0.getTag();
                    intent.putExtra("Id", id);
                    // Starting the  Activity
                    startActivity(intent);
                    onBackPressed();
                    Log.d("mGoogleMap1", "Activity_Calling");

            }
        });
        //Lager infoboks for marker
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(MapsActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);
                TextView title = new TextView(MapsActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                TextView snippet = new TextView(MapsActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                info.addView(title);
                info.addView(snippet);
                return info;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                new GetLocationTask(point.latitude,point.longitude).execute();
            }
        });
    }
    //Henter ut attraksjoner og legger de i en markør.
    private class getJSON extends AsyncTask<String, Void, List<informasjon>> {
        JSONObject jsonObject;

        @Override
        protected List<informasjon> doInBackground(String... urls) {
            String retur = "";
            String s = "";
            String output = "";
            List<informasjon> ListeInf = new ArrayList<>();
            for (String url : urls) {
                try {
                    URL urlen = new URL(urls[0]);
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

                        for (int i = 0; i < mat.length(); i++) {
                            informasjon nyInfo = new informasjon();
                            JSONObject jsonobject = mat.getJSONObject(i);
                            nyInfo.setId(jsonobject.getInt("id"));
                            nyInfo.setBeskrivelse(jsonobject.getString("Beskrivelse"));
                            nyInfo.setGateadresse(jsonobject.getString("Gateadresse"));
                            nyInfo.setBreddegrad(jsonobject.getDouble("Breddegrad"));
                            nyInfo.setLengdegrad(jsonobject.getDouble("Lengdegrad"));
                            ListeInf.add(nyInfo);
                        }
                        markerList=ListeInf;

                        return ListeInf;
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                } catch (Exception e) {

                    return null;

                }
            }

            return ListeInf;
        }

        @Override
        protected void onPostExecute(List<informasjon> ListInf) {
            for (informasjon inf : markerList) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(inf.getBreddegrad(),inf.getLengdegrad())).title("Beskrivlese").snippet(inf.getBeskrivelse()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).setTag(inf.getId());
            }
            //Sette kartposisjon til sist brukte.
            if (getIntent().hasExtra("Bredde")) {
                CameraUpdate point = CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(getIntent().getStringExtra(("Bredde"))), Double.parseDouble(getIntent().getStringExtra(("Lengde")))));
                mMap.moveCamera(point);
            }
            else {

                CameraUpdate point = CameraUpdateFactory.newLatLng(new LatLng(markerList.get(markerList.size()-1).Breddegrad,markerList.get(markerList.size()-1).Lengdegrad));
                mMap.moveCamera(point);

            }




        }
    }
    //Finner adresse med google maps geocoder
    private class GetLocationTask extends AsyncTask<Void, Void, LatLng> {
        JSONObject jsonObject;
        double bredde;
        double lengde;
        String address;
        public GetLocationTask(double b,double l) {
            this.bredde=b;
            this.lengde=l;
        }

        @Override
        protected LatLng doInBackground(Void...params) {
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
                JSONArray liste= ((JSONArray)
                        jsonObject.get("results")).getJSONObject(0).getJSONArray("types");
                holder= (String) liste.get(0);
                LatLng latLng=new LatLng(bredde,lengde);
                return latLng;
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return null;
            } catch (JSONException ex) {
                ex.printStackTrace();
                return null;

            }

        }
        @Override
        protected void onPostExecute(LatLng resultat) {
            if (!holder.equals("plus_code")) {
                holder="";
                Intent intent = new Intent(MapsActivity.this, leggTil.class);
                intent.putExtra("Breddegrad", resultat.latitude);
                intent.putExtra("Lengdegrad", resultat.longitude);
                startActivity(intent);
                finish();
            }
            else {
                holder="";
                Toast.makeText(MapsActivity.this,"Attraksjonen må være registrert på en adresse", Toast.LENGTH_SHORT).show();

            }


        }
    }





}









