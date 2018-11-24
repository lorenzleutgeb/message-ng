package com.hackjunction.fakemessageng;

public class SpatialFilter {
    double[] coefficients;

    public SpatialFilter(double[] coefficients) {
        assert coefficients.length == 5;
        this.coefficients = coefficients;
    }

    public double mergeInput(double[] inputs){
        if (inputs.length == 5) {
            return inputs[0]*coefficients[0] + inputs[1]*coefficients[1] + inputs[2]*coefficients[2] + inputs[3]*coefficients[3] + inputs[4]*coefficients[4];
        }
        else return 0;
    }
}
