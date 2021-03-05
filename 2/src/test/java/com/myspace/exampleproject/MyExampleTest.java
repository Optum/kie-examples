package com.myspace.exampleproject;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.jupiter.api.*;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyExampleTest extends JbpmJUnitBaseTestCase {

    private static final String namespace = "https://kiegroup.org/dmn/_C3611C76-584B-4282-A6DA-4AF2F242929A";
    private static final String modelName = "MyDMN";

    private static final Logger log = LoggerFactory.getLogger(MyExampleTest.class);

    private RuntimeManager runtimeManager;

    private RuntimeEngine runtimeEngine;

    private KieSession kieSession;

    private DMNRuntime dmnRuntime;

    public MyExampleTest() {
        super(true, true);
    }

    @BeforeAll
    public void init() {
        setupPoolingDataSource();

        Map<String, ResourceType> resources = new HashMap<>();
        resources.put("MyDMN.dmn", ResourceType.DMN);
        runtimeManager = createRuntimeManager(resources);

        runtimeEngine = getRuntimeEngine(null);
        kieSession = runtimeEngine.getKieSession();
        dmnRuntime = KieRuntimeFactory.of(runtimeEngine.getKieSession().getKieBase()).get(DMNRuntime.class);
    }

    @AfterAll
    public void destroy() {
        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();
    }

    @Test
    @DisplayName("String input \"2100-01-01\" is greater than date(\"2000-01-31\")")
    public void myTest1() {
        DMNModel dmnModel = dmnRuntime.getModel(namespace, modelName);
        DMNContext dmnContext = dmnRuntime.newContext();

        dmnContext.set("InputDateStr", "2100-01-01");

        DMNResult dmnResult = dmnRuntime.evaluateByName(dmnModel, dmnContext, "Decision-1");
        Boolean result = (Boolean) dmnResult.getDecisionResultByName("Decision-1").getResult();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("String input \"1900-01-01\" is less than date(\"2000-01-31\")")
    public void myTest2() {
        DMNModel dmnModel = dmnRuntime.getModel(namespace, modelName);
        DMNContext dmnContext = dmnRuntime.newContext();

        dmnContext.set("InputDateStr", "1900-01-01");

        DMNResult dmnResult = dmnRuntime.evaluateByName(dmnModel, dmnContext, "Decision-1");
        Boolean result = (Boolean) dmnResult.getDecisionResultByName("Decision-1").getResult();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("LocalDate.of(2100, 1, 1) is greater than date(\"2000-01-31\")")
    public void myTest3() {
        DMNModel dmnModel = dmnRuntime.getModel(namespace, modelName);
        DMNContext dmnContext = dmnRuntime.newContext();

        dmnContext.set("InputDate", LocalDate.of(2100, 1, 1));

        DMNResult dmnResult = dmnRuntime.evaluateByName(dmnModel, dmnContext, "Decision-2");
        Boolean result = (Boolean) dmnResult.getDecisionResultByName("Decision-2").getResult();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("LocalDate.of(1900, 1, 1) is less than date(\"2000-01-31\")")
    public void myTest4() {
        DMNModel dmnModel = dmnRuntime.getModel(namespace, modelName);
        DMNContext dmnContext = dmnRuntime.newContext();

        dmnContext.set("InputDate", LocalDate.of(1900, 1, 1));

        DMNResult dmnResult = dmnRuntime.evaluateByName(dmnModel, dmnContext, "Decision-2");
        Boolean result = (Boolean) dmnResult.getDecisionResultByName("Decision-2").getResult();

        assertThat(result).isFalse();
    }
}

