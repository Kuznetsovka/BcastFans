package com.systemair.bcastfans.service;

import com.systemair.bcastfans.MyCatchException;
import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.exchangers.domain.Process;
import com.systemair.exchangers.domain.exchangers.Exchanger;
import com.systemair.exchangers.service.ExchangersService;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.systemair.bcastfans.staticClasses.UtilClass.parseCell;

public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOGGER = Logger.getLogger(ExcelServiceImpl.class.getName());
    private final ExchangersService exchangersService;
    private String fileExcelOLPath;
    public ExcelServiceImpl(ExchangersService exchangersService) {
        this.exchangersService = exchangersService;
    }

    @SneakyThrows
    @Override
    public Workbook loadWorkbook(Window window, String path) {
        Workbook workbook = null;
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open file");
            fileChooser.setInitialDirectory(new File(path));
            File fileExcelOL = fileChooser.showOpenDialog(window);
            if (fileExcelOL != null) {
                try {
                    FileInputStream inputStream = new FileInputStream(fileExcelOL);
                    if (fileExcelOL.getName().contains(".xlsx")) {
                        workbook = new XSSFWorkbook(inputStream);
                    } else {
                        workbook = new HSSFWorkbook(inputStream);
                    }
                    fileExcelOLPath = fileExcelOL.getAbsolutePath();
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IllegalArgumentException e) {
            throw new MyCatchException(e.getMessage(), Alert.AlertType.ERROR);
        }
        return workbook;
    }

    @Override
    public void fillHeaterFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapHeater) {
        int countSystems = mapHeater.size();
        int columnStart = 40;
        int countColumn = 5;
        for (int row = 1; row < countSystems; row++) {
            if (mapHeater.get(row) == null) continue;
            String modelSystemair = mapHeater.get(row).getModelByVeabModel(mapHeater.get(row).getResult().getModel());
            fillCell(worksheet, columnStart++, row, modelSystemair);
            fillCell(worksheet, columnStart++, row, String.valueOf(mapHeater.get(row).getResult().getCapacity()));
            fillCell(worksheet, columnStart++, row, String.valueOf(mapHeater.get(row).getResult().getFluidFlow()));
            fillCell(worksheet, columnStart++, row, String.valueOf(mapHeater.get(row).getResult().getFluidDrop()));
            fillCell(worksheet, columnStart++, row, String.valueOf(mapHeater.get(row).getResult().getAirDrop()));
        }
    }

    @Override
    public void fillCoolerFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapCooler) {
        int countSystems = mapCooler.size();
        int columnStart = 53;
        int countColumn = 5;
        for (int row = 1; row < countSystems; row++) {
            if (mapCooler.get(row) == null) continue;
            String modelSystemair = mapCooler.get(row).getModelByVeabModel(mapCooler.get(row).getResult().getModel());
            fillCell(worksheet, columnStart++, row, modelSystemair);
            fillCell(worksheet, columnStart++, row, String.valueOf(mapCooler.get(row).getResult().getCapacity()));
            fillCell(worksheet, columnStart++, row, String.valueOf(mapCooler.get(row).getResult().getFluidFlow()));
            fillCell(worksheet, columnStart++, row, String.valueOf(mapCooler.get(row).getResult().getFluidDrop()));
            fillCell(worksheet, columnStart++, row, String.valueOf(mapCooler.get(row).getResult().getAirDrop()));
        }
    }

    @Override
    public void fillExchangersFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapHeaters, Map<Integer, Exchanger> mapCoolers) {
        fillHeaterFromGUI(worksheet, mapHeaters);
        fillCoolerFromGUI(worksheet, mapCoolers);
        writeWorkbook(worksheet.getWorkbook(),fileExcelOLPath);
    }

    @Override
    public Workbook reOpen() {
        return readWorkbook(fileExcelOLPath);
    }

    @SneakyThrows
    public static Workbook readWorkbook(String filename) {
        try {
            if (filename.contains(".xlsx")) {
                return new XSSFWorkbook(filename);
            } else {
                return new HSSFWorkbook(new FileInputStream(filename));
            }
        } catch (Exception e) {
            throw new MyCatchException(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @SneakyThrows
    public static void writeWorkbook(Workbook wb, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            throw new MyCatchException(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void fillCell(Sheet worksheet, int cellIdx, int rowIdx, String value) {
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

    @SneakyThrows
    private Map<Integer, Exchanger> getExchangerMapFromExcel(Sheet worksheet, int columnStart, int columnFinish, Process process) {
        Map<Integer, Exchanger> exchangerMaps = new HashMap<>();
        int row = 0;
        ArrayList<String> rows;
        Cell cell = null;
        try {
            while (worksheet.getRow(++row).getCell(2).getCellType() != CellType.BLANK) {
                rows = new ArrayList<>();
                if (worksheet.getRow(row).getCell(columnStart - 1) == null) {
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
            //TODO решить проблему не соответствия моделей
        } catch (IllegalArgumentException e) {
            throw new MyCatchException("Ошибка считывания данных, не соответствующий аргумент, адрес ячейки " + cell.getAddress(), Alert.AlertType.WARNING);
        }
        return exchangerMaps;
    }


    @SneakyThrows
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
            throw new MyCatchException("Ошибка считывания данных, не должно быть формул, ячейка:" + cell.getAddress().formatAsString(), Alert.AlertType.WARNING);
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

        cell[12].setCellValue("Температура входа");
        cell[13].setCellValue("Влажность входа");
        cell[14].setCellValue("Температура выхода");
        cell[15].setCellValue("Среда");
        cell[16].setCellValue("Процент смеси");
        cell[17].setCellValue("Температура входа жидкости");
        cell[18].setCellValue("Температура выхода жидкости");
        cell[19].setCellValue("Модель нагревателя");
        cell[20].setCellValue("Мощность");
        cell[21].setCellValue("Расход жидности");
        cell[22].setCellValue("Потери по жидности");
        cell[23].setCellValue("Потери");

        cell[24].setCellValue("Температура входа");
        cell[25].setCellValue("Влажность входа");
        cell[26].setCellValue("Температура выхода");
        cell[27].setCellValue("Среда");
        cell[28].setCellValue("Процент смеси");
        cell[29].setCellValue("Температура входа жидкости");
        cell[30].setCellValue("Температура выхода жидкости");
        cell[31].setCellValue("Модель охладителя");
        cell[32].setCellValue("Мощность");
        cell[33].setCellValue("Расход жидности");
        cell[34].setCellValue("Потери по жидности");
        cell[35].setCellValue("Потери");
    }

}
