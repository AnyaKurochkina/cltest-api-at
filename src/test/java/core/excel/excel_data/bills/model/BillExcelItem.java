package core.excel.excel_data.bills.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BillExcelItem {

    private int rowNum;
    //Проект
    private String project;
    //Заказ
    private String order;
    //Услуга
    private String service;
    //Тарифный класс по услуге
    private String tariffClassOfService;
    //Дата начала
    private String startDate;
    //Дата окончания
    private String endDate;
    //Кол-во дней в периоде
    private String amountOfDaysInPeriod;
    //Единица измерения
    private String unitOfMeasuring;
    //Единица расчета
    private String unitOfCalculation;
    //Суммарное потребление в днях
    private String sumConsumingInDays;
    //Объем
    private String volume;
    //Цена за сутки, ₽ без НДС
    private String priceForDayWithoutTax;
    //Цена за сутки, ₽ с НДС (20%)
    private String priceForDayWithTax;
    //Сумма, без НДС, ₽
    private String sumWithoutTax;
    //Сумма, с НДС (20%), ₽
    private String sumWithTax;
}
