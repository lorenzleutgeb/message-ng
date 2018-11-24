package com.hackjunction.messageng;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by melanie on 24.11.18.
 */

public class EmotionalStateToDictionaryMapper {
    private final Map<Boolean,String> emotionalDictionary = new HashMap<>();
    private final EmotionalStateInterface state;

    public EmotionalStateToDictionaryMapper(EmotionalStateInterface state){
        this.state = state;
    }

    public String getEmotionalMapping(){
        return emotionalDictionary.get(state.getCurrentEmotionalState());
    }
}
