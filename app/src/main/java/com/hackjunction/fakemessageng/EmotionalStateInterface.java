package com.hackjunction.messageng;

import java.util.List;
import java.util.Map;

/**
 * Created by melanie on 24.11.18.
 */
public interface EmotionalStateInterface {
    boolean getCurrentEmotionalState(Map<BrainWave, List<Double>> measurements);
}
