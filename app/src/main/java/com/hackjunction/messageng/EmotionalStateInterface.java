package com.hackjunction.messageng;

import com.choosemuse.libmuse.MuseDataPacketType;

import java.util.Map;

/**
 * Created by melanie on 24.11.18.
 */

public interface EmotionalStateInterface {
    boolean getCurrentEmotionalState(Map<BrainWave, double[]> measurements);
}
