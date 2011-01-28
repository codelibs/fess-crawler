package org.seasar.robot.dbflute.cbean.chelper;

/**
 * @author jflute
 */
public interface HpCalculator {

    /**
     * Plus the specified column with the value. (+)
     * @param plusValue The number value for plus. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator plus(Number plusValue);

    /**
     * Minus the specified column with the value. (-)
     * @param minusValue The number value for minus. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator minus(Number minusValue);

    /**
     * Multiply the value to the specified column. (*)
     * @param multiplyValue The number value for multiply. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator multiply(Number multiplyValue);

    /**
     * Divide the specified column by the value. (/)
     * @param divideValue The number value for divide. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator divide(Number divideValue);

    // ===================================================================================
    //                                                                       Related Class
    //                                                                       =============
    public static class CalculationElement {
        protected CalculationType _calculationType;
        protected Number _calculationValue;

        public CalculationType getCalculationType() {
            return _calculationType;
        }

        public void setCalculationType(CalculationType calculationType) {
            this._calculationType = calculationType;
        }

        public Number getCalculationValue() {
            return _calculationValue;
        }

        public void setCalculationValue(Number calculationValue) {
            this._calculationValue = calculationValue;
        }
    }

    public static enum CalculationType {
        PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/");
        private String _operand;

        private CalculationType(String operand) {
            _operand = operand;
        }

        public String operand() {
            return _operand;
        }
    }
}
