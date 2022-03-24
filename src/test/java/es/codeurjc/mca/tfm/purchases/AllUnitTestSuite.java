package es.codeurjc.mca.tfm.purchases;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Unit Test suite")
@SelectPackages("es.codeurjc.mca.tfm.purchases.unit")
@IncludeTags({"UnitTest"})
public class AllUnitTestSuite {

}
