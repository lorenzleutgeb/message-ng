package com.hackjunction.messageng;

import java.util.Map;

public class AlphaScaleModel implements EmotionalStateInterface{
    @Override
    public boolean getCurrentEmotionalState(Map<BrainWave, double[]> measurements) {
        double[] a = measurements.get(BrainWave.ALPHA);
        double sum = 0;
        for (double value : a)  sum += value;
        sum = sum/a.length;
        return sum>0.5?true:false;
    }
}
