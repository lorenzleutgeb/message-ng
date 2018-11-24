package com.hackjunction.messageng;

public class ArousalValenceDirectMapping {
    private double[] arousalRef;

    public ArousalValenceDirectMapping() {
        this.arousalRef = new double[5];
        arousalRef[0] = 0.8;
        arousalRef[1] = 0.55;
        arousalRef[2] = 0.1;
        arousalRef[3] = 0.65;
        arousalRef[4] = 0.7;
    }
    public double getValence(double[] inputSet){
        double result = 0;
        for (int i = 0; i<4; ++i){
            double val = inputSet[i]/inputSet[i+1];
            result += val;
        }
        result = result/5;
        result =  1 - (result/1) - (inputSet[0]/1);
        if (result>1) return 1;
        else if (result<0) return -1;
        else return result*2-1;
    }
    public double getArousal(double[] inputSet){
        double result = 0;
        for (int i = 0; i<4; ++i){
            double val = inputSet[i]/arousalRef[i];
            result += val;
        }
        result = result/5;
        if (result>1) return 1;
        else if (result<0) return -1;
        else return result*2-1;
    }
}
