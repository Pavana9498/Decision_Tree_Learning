package ai.assignment.decisiontree.math;


public class Calculate {
    
    private static final double LOG_TO_THE_BASE_TWO = Math.log(2.0);

    public static double logBaseTwo(double value) {
        return value == 0 ? 0 : value * Math.log(value) / LOG_TO_THE_BASE_TWO;
    } 


}
