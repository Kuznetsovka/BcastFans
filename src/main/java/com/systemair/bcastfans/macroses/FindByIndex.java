package com.systemair.bcastfans.macroses;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.*;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

import java.util.ArrayList;
import java.util.List;

public class FindByIndex implements FreeRefFunction {

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        List<String> table;
        int index;
        String result;
        try {
            List<ValueEval> v1 = getValues(args[0]);
            ValueEval v2 = OperandResolver.getSingleValue(args[1],
                    ec.getRowIndex(),
                    ec.getColumnIndex());
            table = getTableDoubles(v1);
            index = OperandResolver.coerceValueToInt(v2);
            result = table.get(index);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new StringEval(result);
    }

    private List<String> getTableDoubles(List<ValueEval> value) throws EvaluationException {
        List<String> list = new ArrayList<>();
        for (ValueEval cell : value) {
            list.add(OperandResolver.coerceValueToString(cell));
        }
        return list;
    }

    private List<ValueEval> getValues(ValueEval eval) throws EvaluationException {
        if (eval instanceof TwoDEval) {
            int column = ((AreaEval) eval).getFirstColumn();
            TwoDEval ae = ((TwoDEval) eval).getColumn(0);
            int rowCount = ((AreaEval) ae).getLastRow();
            int firstRow = ((AreaEval) ae).getFirstRow();
            List<ValueEval> list = new ArrayList<>();
            for (int i = firstRow; i <= rowCount; i++) {
                list.add(OperandResolver.getSingleValue(eval,i,column));
            }
            return list;
        }
        return null;
    }
}
