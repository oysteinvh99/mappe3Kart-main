package com.example.mappe3kart;

public class informasjon {
    public int id;
    public String Beskrivelse;
    public String Gateadresse;
    public double Breddegrad;
    public double Lengdegrad;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBeskrivelse() {
        return Beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        Beskrivelse = beskrivelse;
    }

    public String getGateadresse() {
        return Gateadresse;
    }

    public void setGateadresse(String gateadresse) {
        Gateadresse = gateadresse;
    }

    public double getBreddegrad() {
        return Breddegrad;
    }

    public void setBreddegrad(double breddegrad) {
        Breddegrad = breddegrad;
    }

    public double getLengdegrad() {
        return Lengdegrad;
    }

    public void setLengdegrad(double lengdegrad) {
        Lengdegrad = lengdegrad;
    }

    public informasjon(String beskrivelse, String gateadresse, double breddegrad, double lengdegrad) {
        Beskrivelse = beskrivelse;
        Gateadresse = gateadresse;
        Breddegrad = breddegrad;
        Lengdegrad = lengdegrad;
    }

    public informasjon() {

    }
}