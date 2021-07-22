package tests.suites;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages("tests")
@IncludeClassNamePatterns({"^.*Tests?$"})
@SuiteDisplayName("Набор тестов для заказа продуктов")
@IncludeTags("regress")
public class Suite2 {
}
