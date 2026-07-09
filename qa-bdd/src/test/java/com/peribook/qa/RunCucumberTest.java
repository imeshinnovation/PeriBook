package com.peribook.qa;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.peribook.qa.steps",
    plugin = {"pretty", "html:target/site/serenity/cucumber.html"},
    tags = "not (@websocket or @two-tabs or @manual)"
)
public class RunCucumberTest {
}
<!-- 2026-07-09 -->
