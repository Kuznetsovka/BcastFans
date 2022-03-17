package com.systemair.bcastfans.service;

import com.systemair.bcastfans.MyCatchException;
import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.exchangers.domain.exchangers.Exchanger;
import com.systemair.exchangers.service.ExchangersService;
import com.systemair.exchangers.domain.Process;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.SneakyThrows;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.systemair.bcastfans.staticClasses.UtilClass.parseCell;

public class ExcelServiceImpl implements ExcelService {

    public static final int START_CELL_RESULT_HEATER = 40;
    public static final int START_CELL_RESULT_COOLER = 53;
    private final ExchangersService exchangersService;
    private String fileExcelOLPath;

    public ExcelServiceImpl(ExchangersService exchangersService) {
        this.exchangersService = exchangersService;
    }

    @Override
    public Workbook loadWorkbook(Window window, String path) {
        Workbook workbook = null;
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open file");
            fileChooser.setInitialDirectory(new File(path));
            File fileExcel = fileChooser.showOpenDialog(window);
            if (fileExcel != null) {
                fileExcelOLPath = fileExcel.getAbsolutePath();
                workbook = readWorkbook(fileExcelOLPath);
            }
        } catch (IllegalArgumentException e) {
            try {
                throw new MyCatchException(e.getMessage(), Alert.AlertType.ERROR);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
        }
        return workbook;
    }

    @Override
    public void fillHeaterFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapHeater) {
        fillExchangerToExcel(worksheet, mapHeater, START_CELL_RESULT_HEATER);
    }

    @Override
    public void fillCoolerFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapCooler) {
        fillExchangerToExcel(worksheet, mapCooler, START_CELL_RESULT_COOLER);
    }

    private void fillExchangerToExcel(Sheet worksheet, Map<Integer, Exchanger> exchanger, int startCellResult) {
        Exchanger currentExchanger;
        for (int row = 1; row < exchanger.size(); row++) {
            currentExchanger = exchanger.get(row);
            if (currentExchanger == null || currentExchanger.getResult() == null) continue;
            fillCell(worksheet, startCellResult, row, currentExchanger.getModelSystemair());
            fillCell(worksheet, startCellResult + 1, row, currentExchanger.getResult().getCapacity().getValueWithMeasure());
            fillCell(worksheet, startCellResult + 2, row, currentExchanger.getResult().getFluidFlow().getValueWithMeasure());
            fillCell(worksheet, startCellResult + 3, row, currentExchanger.getResult().getFluidDrop().getValueWithMeasure());
            fillCell(worksheet, startCellResult + 4, row, Integer.parseInt(currentExchanger.getResult().getAirDrop().getValue()));
            fillCell(worksheet, startCellResult - 5, row, Double.parseDouble(currentExchanger.getResult().getTOut().getValue()));
        }
    }

    @Override
    public void fillExchangersFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapHeaters, Map<Integer, Exchanger> mapCoolers) {
        fillHeaterFromGUI(worksheet, mapHeaters);
        fillCoolerFromGUI(worksheet, mapCoolers);
        writeWorkbook(worksheet.getWorkbook(), fileExcelOLPath);
    }

    @Override
    public Workbook reOpen() {
        return readWorkbook(fileExcelOLPath);
    }

    public static Workbook readWorkbook(String filename) {
        try {
            if (filename.contains(".xlsx")) {
                return new XSSFWorkbook(filename);
            } else {
                return new HSSFWorkbook(new FileInputStream(filename));
            }
        } catch (Exception e) {
            try {
                throw new MyCatchException(e.getMessage(), Alert.AlertType.ERROR);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static void writeWorkbook(Workbook wb, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            try {
                throw new MyCatchException(e.getMessage(), Alert.AlertType.ERROR);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void fillCell(Sheet worksheet, int cellIdx, int rowIdx, String value) {
        Row row = (worksheet.getRow(rowIdx) == null) ? worksheet.createRow(rowIdx) : worksheet.getRow(rowIdx);
        Cell cell = (row.getCell(cellIdx) == null) ? row.createCell(cellIdx) : row.getCell(cellIdx);
        cell.setCellValue(value);
    }

    private void fillCell(Sheet worksheet, int cellIdx, int rowIdx, double value) {
        Row row = (worksheet.getRow(rowIdx) == null) ? worksheet.createRow(rowIdx) : worksheet.getRow(rowIdx);
        Cell cell = (row.getCell(cellIdx) == null) ? row.createCell(cellIdx) : row.getCell(cellIdx);
        cell.setCellValue(value);
    }

    @Override
    public void fillWorksheetFromGUI(Sheet worksheet, TableView<FanUnit> table) {
        String url;
        int lastColumn = table.getColumns().size();
        int countSystems = table.getItems().size();
        Cell[] cell = new XSSFCell[lastColumn];
        for (int count = 0; count < countSystems; count++) {
            FanUnit cells = table.getItems().get(count);
            if (cells.getFan() != null)
                url = cells.getFan().getShortLink();
            else
                url = "";
            for (Map.Entry<Integer, String> entry : cells.getRow().entrySet()) {
                Integer column = entry.getKey();
                String value = entry.getValue();
                cell[column] = worksheet.getRow(count + 1).createCell(column, CellType.STRING);
                if (value != null)
                    cell[column].setCellValue(value);
                if (column == 7 && !url.isEmpty()) {
                    Hyperlink link = worksheet.getWorkbook().getCreationHelper().createHyperlink(HyperlinkType.URL);
                    link.setAddress(url);
                    cell[column].setHyperlink(link);
                }
            }
        }
        worksheet.autoSizeColumn(1);
    }

    @Override
    public void createCellsInWorksheet(Sheet worksheet, TableView<FanUnit> table) {
        int countSystems = table.getItems().size();
        for (int i = 0; i < countSystems + 1; i++) {
            worksheet.createRow(i);
        }
    }

    @Override
    public Map<Integer, Exchanger> getHeaterExchangers(Sheet worksheet) {
        int columnStart = 33;
        int columnFinish = 40;
        Process process = Process.HEAT;
        return getExchangerMapFromExcel(worksheet, columnStart, columnFinish, process);
    }

    @Override
    public Map<Integer, Exchanger> getCoolerExchangers(Sheet worksheet) {
        int columnStart = 46;
        int columnFinish = 53;
        Process process = Process.COOL;
        return getExchangerMapFromExcel(worksheet, columnStart, columnFinish, process);
    }

    private Map<Integer, Exchanger> getExchangerMapFromExcel(Sheet worksheet, int columnStart, int columnFinish, Process process) {
        Map<Integer, Exchanger> exchangerMaps = new HashMap<>();
        int row = 0;
        ArrayList<String> rows;
        Cell cell = null;
        try {
            while (worksheet.getRow(++row).getCell(2).getCellType() != CellType.BLANK) {
                rows = new ArrayList<>();
                if (isEmptyExchanger(worksheet, columnStart, row)) {
                    exchangerMaps.put(row, null);
                    continue;
                }
                if (worksheet.getRow(row).getCell(columnStart - 1).getCellType() == CellType.BLANK) continue;
                for (int column = columnStart; column <= columnFinish; column++) {
                    cell = worksheet.getRow(row).getCell(column);
                    rows.add(parseCell(cell));
                }
                rows.add(parseCell(worksheet.getRow(row).getCell(4)));
                rows.add(parseCell(worksheet.getRow(row).getCell(2)));
                if (rows.size() == 10)
                    exchangerMaps.put(row, exchangersService.getExchanger(rows, process));
            }
        } catch (IllegalArgumentException e) {
            try {
                throw new MyCatchException("Ошибка считывания данных, не соответствующий аргумент, адрес ячейки " + cell.getAddress(), Alert.AlertType.WARNING);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
        }
        return exchangerMaps;
    }

    private boolean isEmptyExchanger(Sheet worksheet, int columnStart, int row) {
        if (worksheet.getRow(row).getCell(columnStart - 1) == null) return true;
        return parseCell(worksheet.getRow(row).getCell(columnStart - 1)).isEmpty();
    }


    @Override
    public ArrayList<ArrayList<String>> loadFansWorksheet(Sheet worksheet) {
        int lastColumn = 7;
        int row = 0;
        ArrayList<ArrayList<String>> cells = new ArrayList<>();
        ArrayList<String> rows;
        Cell cell = null;
        try {
            while (worksheet.getRow(++row).getCell(1).getCellType() != CellType.BLANK) {
                rows = new ArrayList<>();
                for (int column = 0; column < lastColumn; column++) {
                    cell = worksheet.getRow(row).getCell(column);
                    if (cell != null)
                        rows.add(parseCell(cell));
                }
                if (!rows.isEmpty())
                    cells.add(rows);
            }
        } catch (Exception e) {
            try {
                throw new MyCatchException("Ошибка считывания данных, не должно быть формул, ячейка:" + Objects.requireNonNull(cell).getAddress().formatAsString(), Alert.AlertType.WARNING);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
        }
        return cells;
    }

    @Override
    public void setHeader(Sheet worksheet, TableView<FanUnit> table) {
        int lastColumn = table.getColumns().size();
        Cell[] cell = new XSSFCell[lastColumn];
        for (int i = 0; i < lastColumn; i++) {
            cell[i] = worksheet.getRow(0).createCell(i, CellType.STRING);
        }
        cell[0].setCellValue("Считать?");
        cell[1].setCellValue("N");
        cell[2].setCellValue("Расход");
        cell[3].setCellValue("Потери");
        cell[4].setCellValue("Тип монтажа");
        cell[5].setCellValue("Тип установки");
        cell[6].setCellValue("Типоразмер");
        cell[7].setCellValue("Модель");
        cell[8].setCellValue("Артикул");
        cell[9].setCellValue("Мощность");
        cell[10].setCellValue("Фазность");
        cell[11].setCellValue("Цена");

//        cell[12].setCellValue("Температура входа");
//        cell[13].setCellValue("Влажность входа");
//        cell[14].setCellValue("Температура выхода");
//        cell[15].setCellValue("Среда");
//        cell[16].setCellValue("Процент смеси");
//        cell[17].setCellValue("Температура входа жидкости");
//        cell[18].setCellValue("Температура выхода жидкости");
//        cell[19].setCellValue("Модель нагревателя");
//        cell[20].setCellValue("Мощность");
//        cell[21].setCellValue("Расход жидности");
//        cell[22].setCellValue("Потери по жидности");
//        cell[23].setCellValue("Потери");
//
//        cell[24].setCellValue("Температура входа");
//        cell[25].setCellValue("Влажность входа");
//        cell[26].setCellValue("Температура выхода");
//        cell[27].setCellValue("Среда");
//        cell[28].setCellValue("Процент смеси");
//        cell[29].setCellValue("Температура входа жидкости");
//        cell[30].setCellValue("Температура выхода жидкости");
//        cell[31].setCellValue("Модель охладителя");
//        cell[32].setCellValue("Мощность");
//        cell[33].setCellValue("Расход жидности");
//        cell[34].setCellValue("Потери по жидности");
//        cell[35].setCellValue("Потери");
    }

}
