package com.myspace.exampleproject;

import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyExampleTest extends JbpmJUnitBaseTestCase {

    private static final Logger log = LoggerFactory.getLogger(MyExampleTest.class);

    private RuntimeManager runtimeManager;

    private RuntimeEngine runtimeEngine;

    private KieSession kieSession;

    public MyExampleTest() {
        super(true, true);
    }

    @BeforeAll
    public void init() {
        setupPoolingDataSource();

        Map<String, ResourceType> resources = new HashMap<>();
        resources.put("Process1.bpmn", ResourceType.BPMN2);
        resources.put("Process2.bpmn", ResourceType.BPMN2);
        resources.put("MyDMN.dmn", ResourceType.DMN);
        runtimeManager = createRuntimeManager(resources);

        runtimeEngine = getRuntimeEngine(null);
        kieSession = runtimeEngine.getKieSession();
    }

    @AfterAll
    public void destroy() {
        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();
    }

    @Test
    public void myTest1() {
        Map<String, Object> inputVariables = new HashMap<>();
        inputVariables.put("pInputBoolean", true);

        ProcessInstance pInstance = kieSession.startProcess("ExampleProject.Process1", inputVariables);
        assertProcessInstanceCompleted(pInstance.getId());
        Object myDataObject = ((RuleFlowProcessInstance) pInstance).getVariable("pMyDataObject");
        assertThat(myDataObject).isNotNull();
        assertThat(myDataObject).isInstanceOf(MyDataObject.class);
    }

    @Test
    public void myTest2() {
        Map<String, Object> inputVariables = new HashMap<>();
        inputVariables.put("pInputBoolean", true);

        ProcessInstance pInstance = kieSession.startProcess("ExampleProject.Process2", inputVariables);
        assertProcessInstanceCompleted(pInstance.getId());
        pInstance.getProcess();
        Object myDataObject = ((RuleFlowProcessInstance) pInstance).getVariable("pMyDataObject");
        assertThat(myDataObject).isNotNull();
        assertThat(myDataObject).isInstanceOf(HashMap.class);
    }
}

