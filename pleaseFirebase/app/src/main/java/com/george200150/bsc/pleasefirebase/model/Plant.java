package com.george200150.bsc.pleasefirebase.model;

import com.george200150.bsc.pleasefirebase.model.Clades;
import com.george200150.bsc.pleasefirebase.model.Interval;
import com.george200150.bsc.pleasefirebase.model.Location;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Plant {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("englishName")
    @Expose
    private String englishName;

    @SerializedName("latinName")
    @Expose
    private String latinName;

    @SerializedName("kingdom")
    @Expose
    private String kingdom;

    @SerializedName("clades")
    @Expose
    private Clades clades; // branch - can be multiple

    @SerializedName("division")
    @Expose
    private String division; // can be null

    @SerializedName("plantClass")
    @Expose
    private String plantClass; // can be null

    @SerializedName("order")
    @Expose
    private String order;

    @SerializedName("family")
    @Expose
    private String family;

    @SerializedName("subFamily")
    @Expose
    private String subFamily; // can be null

    @SerializedName("genus")
    @Expose
    private String genus;

    @SerializedName("species")
    @Expose
    private String species; // can be null


    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("greeningSeason")
    @Expose
    private Interval greeningSeason;

    @SerializedName("bloomingSeason")
    @Expose
    private Interval bloomingSeason;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getLatinName() {
        return latinName;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public Clades getClades() {
        return clades;
    }

    public void setClades(Clades clades) {
        this.clades = clades;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getPlantClass() {
        return plantClass;
    }

    public void setPlantClass(String plantClass) {
        this.plantClass = plantClass;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getSubFamily() {
        return subFamily;
    }

    public void setSubFamily(String subFamily) {
        this.subFamily = subFamily;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Interval getGreeningSeason() {
        return greeningSeason;
    }

    public void setGreeningSeason(Interval greeningSeason) {
        this.greeningSeason = greeningSeason;
    }

    public Interval getBloomingSeason() {
        return bloomingSeason;
    }

    public void setBloomingSeason(Interval bloomingSeason) {
        this.bloomingSeason = bloomingSeason;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "id=" + id +
                ", englishName='" + englishName + '\'' +
                ", latinName='" + latinName + '\'' +
                ", kingdom='" + kingdom + '\'' +
                ", clades=" + clades +
                ", division='" + division + '\'' +
                ", plantClass='" + plantClass + '\'' +
                ", order='" + order + '\'' +
                ", family='" + family + '\'' +
                ", subFamily='" + subFamily + '\'' +
                ", genus='" + genus + '\'' +
                ", species='" + species + '\'' +
                ", location=" + location +
                ", greeningSeason=" + greeningSeason +
                ", bloomingSeason=" + bloomingSeason +
                '}';
    }
}