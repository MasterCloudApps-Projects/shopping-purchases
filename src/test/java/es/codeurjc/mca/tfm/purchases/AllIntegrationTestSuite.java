package es.codeurjc.mca.tfm.purchases;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Integration Test suite")
@SelectPackages("es.codeurjc.mca.tfm.purchases.integration")
@IncludeTags({"IntegrationTest"})
public class AllIntegrationTestSuite {

}
