package com.george200150.bsc.model;


public class Plant {
    private int id;

    private String latinName;

    private String englishName;

    private String kingdom;

    private String order;

    private String family;

    private String genus;

    private String species;

    private Location location;

    private Interval greeningSeason;

    private Interval bloomingSeason;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLatinName() {
        return latinName;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
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
                ", latinName='" + latinName + '\'' +
                ", englishName='" + englishName + '\'' +
                ", kingdom='" + kingdom + '\'' +
                ", order='" + order + '\'' +
                ", family='" + family + '\'' +
                ", genus='" + genus + '\'' +
                ", species='" + species + '\'' +
                ", location=" + location +
                ", greeningSeason=" + greeningSeason +
                ", bloomingSeason=" + bloomingSeason +
                '}';
    }
}
