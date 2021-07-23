package tests.suites;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages("tests.delete")
@IncludeClassNamePatterns({"^.*Tests?$"})
@SuiteDisplayName("Удаление продуктов из проекта")
//@IncludeTags("smoke")
public class DeleteAllOrders {
}
