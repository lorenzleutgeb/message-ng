package com.hackjunction.messageng;

public class ArousalValenceDirectMapping {
    private double[] arousalRef;
    int numberOfChannels;

    public ArousalValenceDirectMapping() {
        numberOfChannels = 4;
        this.arousalRef = new double[4];
        arousalRef[0] = 0.8;
        arousalRef[1] = 0.1;
        arousalRef[2] = 0.65;
        arousalRef[3] = 0.7;
    }
    public ArousalValenceDirectMapping(boolean t, boolean a, boolean b, boolean g) {
        if (t && a && b && g) numberOfChannels = 4;
        else if ((t && a && b)||(t && a && g)||(t && b && g)||(a && b && g)) numberOfChannels = 3;
        else if ((t && a)||(t && g)||(t && b)||(a && b)||(a && g)||(b && g)) numberOfChannels = 2;
        else if (t || a || b || g) numberOfChannels = 1;
        else numberOfChannels = 0;
        this.arousalRef = new double[numberOfChannels];
        if (numberOfChannels == 4) {
            arousalRef[0] = 0.8;
            arousalRef[1] = 0.1;
            arousalRef[2] = 0.65;
            arousalRef[3] = 0.7;
        } if (numberOfChannels == 3) {
            arousalRef[0] = t?0.8:0.1;
            arousalRef[1] = a?0.1:0.65;
            arousalRef[2] = b?0.65:0.7;
        } if (numberOfChannels == 2) {
            arousalRef[0] = t?0.8:a?0.1:0.65;
            arousalRef[1] = a?0.1:b?0.65:0.7;
        } if (numberOfChannels == 1) {
            arousalRef[0] = t?0.8:a?0.1:b?0.65:0.7;
        }
    }
    public ArousalValenceDirectMapping(int nr) {
        this.arousalRef = new double[nr];
        numberOfChannels = nr;
        switch (nr) {
            case 1:
                arousalRef[0] = 0.8;
                break;
            case 2:
                arousalRef[0] = 0.8;
                arousalRef[1] = 0.1;
                break;
            case 3:
                arousalRef[0] = 0.8;
                arousalRef[1] = 0.1;
                arousalRef[2] = 0.65;
                break;
            case 4:
                arousalRef[0] = 0.8;
                arousalRef[1] = 0.1;
                arousalRef[2] = 0.65;
                arousalRef[3] = 0.7;
                break;
            default:
                arousalRef[0] = 0.8;
                break;
        }
    }
    public double getValence(double[] inputSet){
        double result = 0;
        for (int i = 0; i<numberOfChannels-1; ++i){
            double val = inputSet[i]/inputSet[i+1];
            result += val;
        }
        result = result/numberOfChannels;
        result =  1 - (result/1) - (inputSet[0]/1);
        if (result>1) return 1;
        else if (result<0) return -1;
        else return result*2-1;
    }
    public double getArousal(double[] inputSet){
        double result = 0;
        for (int i = 0; i<numberOfChannels-1; ++i){
            double val = inputSet[i]/arousalRef[i];
            result += val;
        }
        result = result/numberOfChannels;
        if (result>1) return 1;
        else if (result<0) return -1;
        else return result*2-1;
    }
}
