package com.systemair.bcastfans.macroses;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.*;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

import java.util.ArrayList;
import java.util.List;

public class FindByTwoCriteria implements FreeRefFunction {
    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 6) {
            return ErrorEval.VALUE_INVALID;
        }
        List<Double> table1, table2;
        String criteria;
        int result;
        double searchValue1, searchValue2;
        int count;
        try {
            List<ValueEval> v1 = getValues(args[0]);
            List<ValueEval> v2 = getValues(args[1]);
            ValueEval v3 = OperandResolver.getSingleValue(args[2],
                    ec.getRowIndex(),
                    ec.getColumnIndex());
            ValueEval v4 = OperandResolver.getSingleValue(args[3],
                    ec.getRowIndex(),
                    ec.getColumnIndex());
            ValueEval v5 = OperandResolver.getSingleValue(args[4],
                    ec.getRowIndex(),
                    ec.getColumnIndex());
            ValueEval v6 = OperandResolver.getSingleValue(args[5],
                    ec.getRowIndex(),
                    ec.getColumnIndex());
            table1 = getTableDoubles(v1);
            table2 = getTableDoubles(v2);
            criteria = OperandResolver.coerceValueToString(v3);
            searchValue1 = OperandResolver.coerceValueToDouble(v4);
            searchValue2 = OperandResolver.coerceValueToDouble(v5);
            count = OperandResolver.coerceValueToInt(v6);
            result = getIndex(table1, table2, criteria, searchValue1, searchValue2, count);
            checkValue(result);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }

    private List<Double> getTableDoubles(List<ValueEval> value) throws EvaluationException {
        List<Double> list = new ArrayList<>();
        for (ValueEval cell : value) {
            list.add(OperandResolver.coerceValueToDouble(cell));
        }
        return list;
    }

    private List<ValueEval> getValues(ValueEval eval) throws EvaluationException {
        if (eval instanceof TwoDEval) {
            int column = ((AreaEval) eval).getFirstColumn();
            TwoDEval ae = ((TwoDEval) eval).getColumn(column);
            List<ValueEval> list = new ArrayList<>();
            int i = 0;
            while (ae.getRow(++i).isRow()) {
                list.add(OperandResolver.getSingleValue(ae.getValue(i, column), i, column));
            }
            return list;
        }
        return null;
    }

    public Integer getIndex(List<Double> table1, List<Double> table2, String criteria1, Double searchValue1, Double searchValue2, int count) {
        int entrance = 1;
        for (int i = 0; i < table1.size(); i++) {
            switch (criteria1) {
                case ">":
                    if (table1.get(i) > searchValue1)
                        if (isSuit(table2, searchValue2, count, entrance, i)) return i;
                case "<":
                    if (table1.get(i) < searchValue1)
                        if (isSuit(table2, searchValue2, count, entrance, i)) return i;
                case "=":
                    if (table1.get(i).equals(searchValue1))
                        if (isSuit(table2, searchValue2, count, entrance, i)) return i;
                case ">=":
                    if (table1.get(i) >= searchValue1)
                        if (isSuit(table2, searchValue2, count, entrance, i)) return i;
                case "<=":
                    if (table1.get(i) <= searchValue1)
                        if (isSuit(table2, searchValue2, count, entrance, i)) return i;
                default:
                    entrance++;
            }
        }
        return table1.size() + 1;
    }

    private boolean isSuit(List<Double> table2, Double searchValue2, int count, int entrance, int i) {
        return table2.get(i) >= searchValue2 && count == entrance;
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
