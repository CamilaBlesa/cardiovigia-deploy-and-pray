package ar.edu.unsam.cardiovigia.bdd;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@ConfigurationParameter(
        key = FEATURES_PROPERTY_NAME,
        value = "classpath:features"
)
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "ar.edu.unsam.cardiovigia.bdd"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty"
)
public class CucumberTest {
}
