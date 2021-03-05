package ru.vtb.opk.apitest.data;

import org.testng.annotations.DataProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataProviderClass {


    @DataProvider(name = "СalculatorСalculate", parallel = true)
    public Iterator<Object[]> getDataForCalculate() {
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{"PAY_ROLL", 80000, 24, "true"});
        list.add(new Object[]{"PAY_ROLL", 80000, 24, "false"});
        list.add(new Object[]{"PAY_ROLL", 80000, 124, "true"});
        list.add(new Object[]{"PAY_ROLL", 0, -1, "true"});
        list.add(new Object[]{"NON_PAY", 80000, 24, "false"});
        //TODO list.add(new Object[]{"NON_PAY", 500000, 24, 24, "true"}); лишнее поле
        list.add(new Object[]{null, 500000, 24, "true"});
        return list.iterator();
    }
}