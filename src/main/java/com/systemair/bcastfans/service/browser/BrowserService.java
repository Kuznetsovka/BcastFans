package com.systemair.bcastfans.service.browser;

import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import com.systemair.bcastfans.staticClasses.SingletonBrowserClass;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface BrowserService {
    String positiveLimit = "100";
    String negativeLimit = "0";
    SingletonBrowserClass sbc = SingletonBrowserClass.getInstanceOfSingletonBrowserClass();

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

    Fan calculate(String airFlow, String airDrop, TypeMontage typeMontage, SubType subType, String dimension, List<String> selectedList);

    void inputTextByLabel(String findTextLabel, String newValue) throws InterruptedException;

    Fan calculate(String airFlow, String airDrop, TypeMontage typeMontage, SubType subType, String dimension);

    void clearTypeMontage();

    boolean checkAvailableFanModel(String model, List<String> selectedList);

    void fillFlowAndDrop(String airFlow, String airDrop);

    boolean isContinueFan(String price, SubType subType, String model);

    void sorting();

    void hidingDiagram();

    void grouping();

    void changeMeasureValueOnTableByIndex(String newValue, int index);
}
