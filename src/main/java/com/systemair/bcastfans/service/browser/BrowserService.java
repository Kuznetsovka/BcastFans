package com.systemair.bcastfans.service.browser;

import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import com.systemair.bcastfans.staticClasses.SingletonBrowserClass;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface BrowserService {

    void selectTypeMontage(TypeMontage typeMontage);

    void selectTwoTypeFan(int i1, int i2, List<WebElement> list);

    void selectTypeFan(int index, List<WebElement> list);

    void initializeBrowser();

    void setNegativeLimit(String negativeLimit);

    void setPositiveLimit(String positiveLimit);

    void onCheckbox(boolean onAction, WebElement webElement);

    void onCheckboxDiagram(WebElement webElement);

    void selectSubType(SubType subType);

    void changeValueComboBoxByLabel(String findTextLabel, String newValue);

    void prepareStartPageBeforeCalculation();

    void inputTextByLabel(String findTextLabel, String newValue) throws InterruptedException;

    Fan calculate(String airFlow, String airDrop, TypeMontage typeMontage, SubType subType, String dimension, List<String>... selectedFans);

    void clearTypeMontage();

    Fan findFan(SubType subType, String dimension, List<String>... selectedList);

    void fillFlowAndDrop(String airFlow, String airDrop);

    boolean checkAvailableFanModel(String model, List<String> selectedList);

    boolean isContinueFan(String price, SubType subType, String model, String phase);

    void sorting();

    void hidingDiagram();

    void grouping();

    void changeMeasureValueOnTableByIndex(String newValue, int index);

    SingletonBrowserClass getSbc();
}
