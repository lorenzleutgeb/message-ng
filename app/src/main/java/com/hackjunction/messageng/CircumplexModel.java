package com.hackjunction.messageng;

import com.choosemuse.libmuse.MuseDataPacketType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class CircumplexModel {
    ArousalValenceDirectMapping mapping;
    EmotionCategories category;
    public CircumplexModel() {
        this.mapping = new ArousalValenceDirectMapping();
    }

    public EmotionCategories getEmotion(double[] inputSet){
        double x = mapping.getValence(inputSet);
        double y = mapping.getArousal(inputSet);
        double r = Math.sqrt(x*x + y*y);
        double angle = Math.atan2(y, x);
        double flippedAngle = Math.PI / 2 - angle;
        if ((y>0 && x<0)||(y<0 && x<0)) angle = angle +  Math.PI;
        if (x>0 && y<0) angle = angle +  Math.PI*2;
        int theta = (int) angle;
        return category.valueOf(theta);
    }

}