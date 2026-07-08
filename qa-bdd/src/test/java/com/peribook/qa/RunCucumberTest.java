package com.peribook.qa;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.peribook.qa.steps",
    plugin = {"pretty", "html:target/site/serenity"},
    tags = "not (@websocket or @two-tabs)" // WebSocket scenarios requieren browser real
)
public class RunCucumberTest {}
