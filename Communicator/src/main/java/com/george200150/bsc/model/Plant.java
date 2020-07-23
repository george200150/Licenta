package com.george200150.bsc.model;

public class Plant {
    private int id;

    private String englishName;
    private String latinName;

    private String kingdom;
    private Clades clades; // branch - can be multiple
    private String division; // can be null
    private String plantClass; // can be null
    private String order;
    private String family;
    private String subFamily; // can be null
    private String genus;
    private String species; // can be null

    private Location location;
    private Interval greeningSeason;
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

// https://en.wikipedia.org/wiki/Tagetes_erecta
// https://en.wikipedia.org/wiki/Acer_pseudoplatanus
// to do: https://www.google.com/search?q=Lilium+martagon+var+albiflorum&sxsrf=ALeKk03w9Nz7b7aOEltf27WARgeTEFVrhA:1595542224935&source=lnms&tbm=isch&sa=X&ved=2ahUKEwiv7Kn8seTqAhUK06YKHfJJBKAQ_AUoAXoECAsQAw&biw=1920&bih=937
// https://photos.google.com/photo/AF1QipNuiOdDwAJWRVk8RVcK8ENwRLKE4HPGcrHLq4Nt (link to the above plant's label)
