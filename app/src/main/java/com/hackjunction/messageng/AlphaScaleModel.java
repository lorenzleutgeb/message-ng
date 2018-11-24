package com.hackjunction.messageng;

import java.util.List;
import java.util.Map;

public class AlphaScaleModel implements EmotionalStateInterface{
    @Override
    public boolean getCurrentEmotionalState(Map<BrainWave, List<Double>> measurements) {
        double sum = 0;
        double cnt = 0;
        for (Double it : measurements.get(BrainWave.ALPHA)) {
            if (it == null || Double.isNaN(it)) {
                continue;
            }
            sum += it;
            cnt += 1;
        }

        return (sum / cnt) > 0.5;
    }
}
