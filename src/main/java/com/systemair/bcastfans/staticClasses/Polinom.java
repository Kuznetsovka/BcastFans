package com.systemair.bcastfans.staticClasses;

import lombok.SneakyThrows;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.*;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;

public class Polinom implements FreeRefFunction {
    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec ) {
        if (args.length != 4) {
            return ErrorEval.VALUE_INVALID;
        }
        List<List<Double>> table;
        double result;
        int flow, index, searchValue;
        try {
            ValueEval v1 = OperandResolver.getSingleValue( args[0],
                                                           ec.getRowIndex(), 
                                                           ec.getColumnIndex() );
            ValueEval v2 = OperandResolver.getSingleValue( args[1],
                                                           ec.getRowIndex(),
                                                           ec.getColumnIndex() );
            List<List<ValueEval>> v3 = getListRow(args[2]);
            ValueEval v4 = OperandResolver.getSingleValue( args[3],
                    ec.getRowIndex(),
                    ec.getColumnIndex() ) ;

            flow  = OperandResolver.coerceValueToInt( v1 ) ;
            searchValue  = OperandResolver.coerceValueToInt( v2 ) ;
            index = OperandResolver.coerceValueToInt( v4 ) ;
            table = getTableDoubles(v3);
            result = calculateDropByPolinom(flow, searchValue, table, index);
            checkValue(result);
        } catch (EvaluationException e) {
            e.printStackTrace() ;
            return e.getErrorEval();
        }
        return new NumberEval( result ) ;
    }

    @SneakyThrows
    private List<List<Double>> getTableDoubles(List<List<ValueEval>> v3) {
        List<List<Double> > list = new ArrayList<>();
        List<Double> row = new ArrayList<>();
        for (List<ValueEval> rows : v3) {
            for (ValueEval cell : rows) {
                row.add(OperandResolver.coerceValueToDouble( cell ));
            }
            list.add(row);
            row = new ArrayList<>();
        }
        return list;
    }

    @SneakyThrows
    private List<List<ValueEval>> getListRow(ValueEval arg) {
        int lastRow = 0,firstRow = 0;
        if (arg instanceof AreaEval) {
            lastRow = ((AreaEval) arg).getLastRow();
            firstRow = ((AreaEval) arg).getFirstRow();
        }
        return getValues(arg, lastRow == firstRow);
    }

    private List<List<ValueEval>> getValues(ValueEval eval, boolean lastRowOnly) throws EvaluationException {
        if (eval instanceof AreaEval) {
            AreaEval ae = (AreaEval)eval;
            List<List<ValueEval> > list = new ArrayList<>();
            List<ValueEval> row = new ArrayList<>();
            int startRow = lastRowOnly ? ae.getLastRow() : ae.getFirstRow();
            for (int r = startRow; r <= ae.getLastRow(); r++) {
                for (int c = ae.getFirstColumn(); c <= ae.getLastColumn(); c++) {
                    row.add(OperandResolver.getSingleValue(ae.getAbsoluteValue(r, c), r, c));
                }
                list.add(row);
                row = new ArrayList<>();
            }
            return list;
        } else {
            return null;
        }
    }

    public Integer calculateDropByPolinom(Integer flow, Integer searchValue, List<List<Double>> table, int index ) {
        for (List<Double> row : table) {
            if (row.get(0).intValue() == searchValue) {
                switch(index){
                    case 6:
                        return Math.toIntExact((long) (getPart(flow, 6, row.get(1)) + getPart(flow, 5, row.get(2)) + getPart(flow, 4, row.get(3)) + getPart(flow, 3, row.get(4)) + getPart(flow, 2, row.get(5)) + getPart(flow, 1, row.get(6)) + getPart(flow, 0, row.get(7))));
                    case 5:
                        return Math.toIntExact((long) (getPart(flow, 5, row.get(1)) + getPart(flow, 4, row.get(2)) + getPart(flow, 3, row.get(3)) + getPart(flow, 2, row.get(4)) + getPart(flow, 1, row.get(5)) + getPart(flow, 0, row.get(6))));
                    case 4:
                        return Math.toIntExact((long) (getPart(flow, 4, row.get(1)) + getPart(flow, 3, row.get(2)) + getPart(flow, 2, row.get(3)) + getPart(flow, 1, row.get(4)) + getPart(flow, 0, row.get(5))));
                    case 3:
                        return Math.toIntExact((long) (getPart(flow, 3, row.get(1)) + getPart(flow, 2, row.get(2)) + getPart(flow, 1, row.get(3)) + getPart(flow, 0, row.get(4))));
                    case 2:
                        return Math.toIntExact((long) (getPart(flow, 2, row.get(1)) + getPart(flow, 1, row.get(2)) + getPart(flow, 0, row.get(3))));
                }
            
            }
        }
        return null;
    }


    private Double getPart(int flow, int index, Double coef) {
        return coef * pow(flow, index);
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
