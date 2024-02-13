package core.excel.excel_core.field;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FieldInfoMapping {
    //Счета
    PROJECT("Проект"),
    ORDER("Заказ"),
    SERVICE("Услуга"),
    TARIFF_CLASS_OF_SERVICE("Тарифный класс по услуге"),
    START_DATE("Дата начала"),
    END_DATE("Дата окончания"),
    AMOUNT_OF_DAYS_IN_PERIOD("Кол-во дней в периоде"),
    UNIT_OF_MEASURING("Единица измерения"),
    UNIT_OF_CALCULATION("Единица расчета"),
    SUM_CONSUMING_IN_DAYS("Суммарное потребление в днях"),
    VOLUME("Объем"),
    PRICE_FOR_DAY_WITHOUT_TAX("Цена за сутки, ₽ без НДС"),
    PRICE_FOR_DAY_WITH_TAX("Цена за сутки, ₽ с НДС (20%)"),
    SUM_WITHOUT_TAX("Сумма, без НДС, ₽"),
    SUM_WITH_TAX("Сумма, с НДС (20%), ₽"),
    ;

    private final String name;
}
