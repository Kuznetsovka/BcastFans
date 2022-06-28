package com.systemair.bcastfans.macroses;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.*;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

import static java.lang.Math.pow;

public class Cp implements FreeRefFunction {
        @Override
        public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
            if (args.length != 1) {
                return ErrorEval.VALUE_INVALID;
            }
            double t;
            double result;
            try {
                ValueEval v1 = OperandResolver.getSingleValue(args[0],
                        ec.getRowIndex(),
                        ec.getColumnIndex());
                t = OperandResolver.coerceValueToInt(v1);
                result = pow(t,2) * -2.58262778300374E-08 + t * 8.31060520539351E-05 + 1.00696564440263;
                checkValue(result);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return new NumberEval(result);
        }

        /**
         * Excel does not support infinities and NaNs, rather, it gives a #NUM! error in these cases
         *
         * @throws EvaluationException (#NUM!) if <tt>result</tt> is <tt>NaN</> or <tt>Infinity</tt>
         */
        static void checkValue(double result) throws EvaluationException {
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new EvaluationException(ErrorEval.NUM_ERROR);
            }
        }
}
