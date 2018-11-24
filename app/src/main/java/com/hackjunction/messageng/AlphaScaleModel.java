package com.hackjunction.messageng;

import java.util.List;
import java.util.Map;

public class AlphaScaleModel implements EmotionalStateInterface{

    @Override
    public boolean getCurrentEmotionalState(Map<BrainWave, List<Double>> measurements) {
        List<Double> a = measurements.get(BrainWave.ALPHA);
        double avg = a.stream()
                .mapToDouble(x -> x)
                .average().getAsDouble();
        return avg >0.5?true:false;
    }
}
