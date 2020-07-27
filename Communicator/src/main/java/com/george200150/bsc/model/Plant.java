package com.george200150.bsc.model;

import lombok.Data;

@Data
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
}

// https://en.wikipedia.org/wiki/Tagetes_erecta
// https://en.wikipedia.org/wiki/Acer_pseudoplatanus
// to do: https://www.google.com/search?q=Lilium+martagon+var+albiflorum&sxsrf=ALeKk03w9Nz7b7aOEltf27WARgeTEFVrhA:1595542224935&source=lnms&tbm=isch&sa=X&ved=2ahUKEwiv7Kn8seTqAhUK06YKHfJJBKAQ_AUoAXoECAsQAw&biw=1920&bih=937
// https://photos.google.com/photo/AF1QipNuiOdDwAJWRVk8RVcK8ENwRLKE4HPGcrHLq4Nt (link to the above plant's label)
