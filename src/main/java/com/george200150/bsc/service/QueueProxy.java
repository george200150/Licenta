package com.george200150.bsc.service;

import com.george200150.bsc.model.Bitmap;
import com.george200150.bsc.model.Message;
import com.george200150.bsc.model.Prediction;

import java.util.ArrayList;
import java.util.List;

public class QueueProxy {

    // todo: there should not be anything dealing with networking top level

//    // create unique identifier for message
//    String token = bitmap.hashCode() + "_TOKEN_" + System.nanoTime();
//    Message message = new Message();
//        message.setToken(token);
//        message.setBitmap(bitmap);

    public List<Prediction> process(Bitmap bitmap) {
        System.out.println("IN QUEUE PROXY:    " + bitmap.toString());
        List<Prediction> rez = new ArrayList<>();

        Prediction pred = new Prediction();
        pred.setCharacter("a");
        pred.setPercentage(100);

        Prediction pred2 = new Prediction();
        pred2.setCharacter("a");
        pred2.setPercentage(20);

        rez.add(pred);
        rez.add(pred2);
        rez.add(pred);
        rez.add(pred2);
        return rez;
    }
}
