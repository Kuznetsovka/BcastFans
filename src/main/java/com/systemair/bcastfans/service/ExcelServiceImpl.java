package com.systemair.bcastfans.service;

import com.systemair.bcastfans.MyCatchException;
import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.exchangers.domain.exchangers.Exchanger;
import com.systemair.exchangers.domain.exchangers.Heater;
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
import com.systemair.exchangers.domain.Process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.systemair.bcastfans.staticClasses.UtilClass.parseCell;

public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOGGER = Logger.getLogger(ExcelServiceImpl.class.getName());
    private final ExchangersService exchangersService;

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
            File file = fileChooser.showOpenDialog(window);
            if (file != null) {
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    if (file.getName().contains(".xlsx")) {
                        workbook = new XSSFWorkbook(inputStream);
                    } else {
                        workbook = new HSSFWorkbook(inputStream);
                    }
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch(IllegalArgumentException e){
            throw new MyCatchException(e.getMessage(), Alert.AlertType.ERROR);
        }
        return workbook;
    }

    @Override
    public void fillHeaterFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapHeater) {
        int countSystems = mapHeater.size();
        int columnStart = 40;
        int countColumn = 5;
        Cell[] cell = new XSSFCell[countColumn];
        for (int row = 1; row < countSystems; row++) {
            if (mapHeater.get(row) == null) continue;
            String modelSystemair = mapHeater.get(row).getModelByVeabModel(mapHeater.get(row).getResult().getModel());
            fillCell(worksheet, cell, row, columnStart, modelSystemair);
            fillCell(worksheet, cell, row, columnStart++, String.valueOf(mapHeater.get(row).getResult().getCapacity()));
            fillCell(worksheet, cell, row, columnStart++, String.valueOf(mapHeater.get(row).getResult().getFluidFlow()));
            fillCell(worksheet, cell, row, columnStart++, String.valueOf(mapHeater.get(row).getResult().getFluidDrop()));
            fillCell(worksheet, cell, row, columnStart++, String.valueOf(mapHeater.get(row).getResult().getAirDrop()));
        }
    }

    @Override
    public void fillCoolerFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapCooler) {
        int countSystems = mapCooler.size();
        int columnStart = 53;
        int countColumn = 5;
        Cell[] cell = new XSSFCell[countColumn];
        for (int row = 1; row < countSystems; row++) {
            fillCell(worksheet, cell, row, columnStart, mapCooler.get(row).getResult().getModel());
            fillCell(worksheet, cell, row, columnStart++, String.valueOf(mapCooler.get(row).getResult().getCapacity()));
            fillCell(worksheet, cell, row, columnStart++, String.valueOf(mapCooler.get(row).getResult().getFluidFlow()));
            fillCell(worksheet, cell, row, columnStart++, String.valueOf(mapCooler.get(row).getResult().getFluidDrop()));
            fillCell(worksheet, cell, row, columnStart++, String.valueOf(mapCooler.get(row).getResult().getAirDrop()));
        }
    }

    private void fillCell(Sheet worksheet, Cell[] cell, int row, int col, String value) {
        cell[col] = worksheet.getRow(row).createCell(col, CellType.STRING);
        cell[col].setCellValue(value);
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
            throw new MyCatchException("Ошибка считывания данных, не соответствующий аргумент, адрес ячейки" + cell.getAddress(), Alert.AlertType.WARNING);
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
