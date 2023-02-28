package com.mongo.mongo;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mongo.mongo.LexemeBuffer.expr;
import static com.mongo.mongo.LexemeBuffer.lexAnalyze;

@Service
public class ConverterFromFormulaToDouble {

    public static Double isFormula(XSSFWorkbook workbook, String formula, int rowMax, int endCall) {
        // в данный момент программа понимает только формулы sum
        if (formula.contains("SUM")) {
            return isSum(workbook, formula, rowMax, endCall);
        }
        return null;
    }


    private static Double isSum(XSSFWorkbook workbook, String formula, int rowMax, int endCall) {

        double result = 0.0;
        int startSub = formula.indexOf("(");
        int endSub = formula.indexOf(")");

        //если формула сложения использует диапазон клеток,
        // но не суммирует дополнительные отдельные клетки
        if (formula.contains(":") && !formula.contains(";")) {
            String[] sRange = formula.substring(startSub + 1, endSub).split(":");
            List<Integer> cellIndex = new ArrayList<>();
            List<Integer> roww = new ArrayList<>();
            for (String s1 : sRange) {
                for (int k = 0; k < rowMax; k++) {
                    for (int l = 0; l < endCall; l++) {
                        if (workbook.getSheetAt(0).getRow(k).getCell(l) != null) {
                            if (workbook.getSheetAt(0).getRow(k).getCell(l).getReference().equals(s1)) {
                                roww.add(k);
                                cellIndex.add(l);
                            }
                        }
                    }
                }
            }
            if (Objects.equals(roww.get(0), roww.get(1))) {
                for (int k = 0; k <= cellIndex.get(1) - cellIndex.get(0); k++) {
                    result = result + workbook.getSheetAt(0).getRow(roww.get(0))
                            .getCell(cellIndex.get(0) + k).getNumericCellValue();
                }
                return result;
            } else {
                int x = roww.get(1) - roww.get(0);
                for (int i = 0; i <= x; i++) {
                    for (int k = 0; k <= cellIndex.get(1) - cellIndex.get(0); k++) {
                        result = result + workbook.getSheetAt(0).getRow(roww.get(0) + i)
                                .getCell(cellIndex.get(0) + k).getNumericCellValue();
                    }
                }
            }
            return result;
        }

        // если основные математические действия производятся над отдельными ячейками
        //
        if (formula.contains("+") || formula.contains("-") || formula.contains("/") || formula.contains("*")) {

            List<Character> actions = new ArrayList<>();
            String[] sRange = formula.substring(startSub + 1, endSub).split("\\+|/|-|\\*");
            for (char c : formula.toCharArray()) {
                if (c == '/' || c == '+' || c == '-' || c == '*') {
                    actions.add(c);
                }
            }

            List<Integer> numForArithmeticOper = new ArrayList<>();

            for (String s : sRange) {
                for (int k = 0; k < rowMax; k++) {
                    for (int l = 0; l < endCall; l++) {
                        if (workbook.getSheetAt(0).getRow(k).getCell(l) != null) {
                            if (workbook.getSheetAt(0).getRow(k).getCell(l).getReference().equals(s)) {
                                numForArithmeticOper.add((int) workbook.getSheetAt(0).getRow(k).getCell(l)
                                        .getNumericCellValue());
                            }
                        }
                    }
                }
            }
            StringBuilder actionsPlusNum = new StringBuilder();
            actionsPlusNum.append(numForArithmeticOper.get(0));
            for (int i = 1; i < numForArithmeticOper.size(); i++) {
                actionsPlusNum.append(actions.get(i - 1));
                actionsPlusNum.append(numForArithmeticOper.get(i));
            }
            List<Lexeme> lexemes = lexAnalyze(actionsPlusNum.toString());
            LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
            return result = expr(lexemeBuffer);
        }
        return null;
    }
}
