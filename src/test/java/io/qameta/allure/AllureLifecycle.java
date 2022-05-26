package io.qameta.allure;

import io.qameta.allure.internal.AllureStorage;
import io.qameta.allure.internal.AllureThreadContext;
import io.qameta.allure.listener.ContainerLifecycleListener;
import io.qameta.allure.listener.FixtureLifecycleListener;
import io.qameta.allure.listener.LifecycleNotifier;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Attachment;
import io.qameta.allure.model.FixtureResult;
import io.qameta.allure.model.Stage;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.model.TestResultContainer;
import io.qameta.allure.model.WithAttachments;
import io.qameta.allure.model.WithSteps;
import io.qameta.allure.util.PropertiesUtils;
import io.qameta.allure.util.ServiceLoaderUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testit.junit5.RunningHandler;
import ru.testit.junit5.StepsAspects;
import ru.testit.utils.UniqueTest;

public class AllureLifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(AllureLifecycle.class);
    private final AllureResultsWriter writer;
    private final AllureStorage storage;
    private final AllureThreadContext threadContext;
    private final LifecycleNotifier notifier;

    public AllureLifecycle() {
        this(getDefaultWriter());
    }

    public AllureLifecycle(AllureResultsWriter writer) {
        this(writer, getDefaultNotifier());
    }

    AllureLifecycle(AllureResultsWriter writer, LifecycleNotifier lifecycleNotifier) {
        this.notifier = lifecycleNotifier;
        this.writer = writer;
        this.storage = new AllureStorage();
        this.threadContext = new AllureThreadContext();
    }

    public void startTestContainer(String containerUuid, TestResultContainer container) {
        this.storage.getContainer(containerUuid).ifPresent((parent) -> {
            synchronized (this.storage) {
                parent.getChildren().add(container.getUuid());
            }
        });
        this.startTestContainer(container);
    }

    public void startTestContainer(TestResultContainer container) {
        this.notifier.beforeContainerStart(container);
        container.setStart(System.currentTimeMillis());
        this.storage.put(container.getUuid(), container);
        this.notifier.afterContainerStart(container);
    }

    public void updateTestContainer(String uuid, Consumer<TestResultContainer> update) {
        Optional<TestResultContainer> found = this.storage.getContainer(uuid);
        if (!found.isPresent()) {
            LOGGER.error("Could not update test container: container with uuid {} not found", uuid);
        } else {
            TestResultContainer container = (TestResultContainer) found.get();
            this.notifier.beforeContainerUpdate(container);
            update.accept(container);
            this.notifier.afterContainerUpdate(container);
        }
    }

    public void stopTestContainer(String uuid) {
        Optional<TestResultContainer> found = this.storage.getContainer(uuid);
        if (!found.isPresent()) {
            LOGGER.error("Could not stop test container: container with uuid {} not found", uuid);
        } else {
            TestResultContainer container = (TestResultContainer) found.get();
            this.notifier.beforeContainerStop(container);
            container.setStop(System.currentTimeMillis());
            this.notifier.afterContainerUpdate(container);
        }
    }

    public void writeTestContainer(String uuid) {
        Optional<TestResultContainer> found = this.storage.getContainer(uuid);
        if (!found.isPresent()) {
            LOGGER.error("Could not write test container: container with uuid {} not found", uuid);
        } else {
            TestResultContainer container = (TestResultContainer) found.get();
            this.notifier.beforeContainerWrite(container);
            this.writer.write(container);
            this.storage.remove(uuid);
            this.notifier.afterContainerWrite(container);
        }
    }

    public void startPrepareFixture(String containerUuid, String uuid, FixtureResult result) {
        this.storage.getContainer(containerUuid).ifPresent((container) -> {
            synchronized (this.storage) {
                container.getBefores().add(result);
            }
        });
        this.notifier.beforeFixtureStart(result);
        this.startFixture(uuid, result);
        this.notifier.afterFixtureStart(result);
    }

    public void startTearDownFixture(String containerUuid, String uuid, FixtureResult result) {
        this.storage.getContainer(containerUuid).ifPresent((container) -> {
            synchronized (this.storage) {
                container.getAfters().add(result);
            }
        });
        this.notifier.beforeFixtureStart(result);
        this.startFixture(uuid, result);
        this.notifier.afterFixtureStart(result);
    }

    private void startFixture(String uuid, FixtureResult result) {
        this.storage.put(uuid, result);
        result.setStage(Stage.RUNNING);
        result.setStart(System.currentTimeMillis());
        this.threadContext.clear();
        this.threadContext.start(uuid);
    }

    public void updateFixture(Consumer<FixtureResult> update) {
        Optional<String> root = this.threadContext.getRoot();
        if (!root.isPresent()) {
            LOGGER.error("Could not update test fixture: no test fixture running");
        } else {
            String uuid = (String) root.get();
            this.updateFixture(uuid, update);
        }
    }

    public void updateFixture(String uuid, Consumer<FixtureResult> update) {
        Optional<FixtureResult> found = this.storage.getFixture(uuid);
        if (!found.isPresent()) {
            LOGGER.error("Could not update test fixture: test fixture with uuid {} not found", uuid);
        } else {
            FixtureResult fixture = (FixtureResult) found.get();
            this.notifier.beforeFixtureUpdate(fixture);
            update.accept(fixture);
            this.notifier.afterFixtureUpdate(fixture);
        }
    }

    public void stopFixture(String uuid) {
        Optional<FixtureResult> found = this.storage.getFixture(uuid);
        if (!found.isPresent()) {
            LOGGER.error("Could not stop test fixture: test fixture with uuid {} not found", uuid);
        } else {
            FixtureResult fixture = (FixtureResult) found.get();
            this.notifier.beforeFixtureStop(fixture);
            fixture.setStage(Stage.FINISHED);
            fixture.setStop(System.currentTimeMillis());
            this.storage.remove(uuid);
            this.threadContext.clear();
            this.notifier.afterFixtureStop(fixture);
        }
    }

    public Optional<String> getCurrentTestCase() {
        return this.threadContext.getRoot();
    }

    public Optional<String> getCurrentTestCaseOrStep() {
        return this.threadContext.getCurrent();
    }

    public boolean setCurrentTestCase(String uuid) {
        Optional<TestResult> found = this.storage.getTestResult(uuid);
        if (!found.isPresent()) {
            return false;
        } else {
            this.threadContext.clear();
            this.threadContext.start(uuid);
            return true;
        }
    }

    public void scheduleTestCase(String containerUuid, TestResult result) {
        this.storage.getContainer(containerUuid).ifPresent((container) -> {
            synchronized (this.storage) {
                container.getChildren().add(result.getUuid());
            }
        });
        this.scheduleTestCase(result);
    }

    public void scheduleTestCase(TestResult result) {
        this.notifier.beforeTestSchedule(result);
        result.setStage(Stage.SCHEDULED);
        this.storage.put(result.getUuid(), result);
        this.notifier.afterTestSchedule(result);
    }

    public void startTestCase(String uuid) {
        this.threadContext.clear();
        Optional<TestResult> found = this.storage.getTestResult(uuid);
        if (!found.isPresent()) {
            LOGGER.error("Could not start test case: test case with uuid {} is not scheduled", uuid);
        } else {
            TestResult testResult = (TestResult) found.get();
            this.notifier.beforeTestStart(testResult);
            testResult.setStage(Stage.RUNNING).setStart(System.currentTimeMillis());
            this.threadContext.start(uuid);
            this.notifier.afterTestStart(testResult);
        }
    }

    public void updateTestCase(Consumer<TestResult> update) {
        Optional<String> root = this.threadContext.getRoot();
        if (!root.isPresent()) {
            LOGGER.error("Could not update test case: no test case running");
        } else {
            String uuid = (String) root.get();
            this.updateTestCase(uuid, update);
        }
    }

    public void updateTestCase(String uuid, Consumer<TestResult> update) {
        Optional<TestResult> found = this.storage.getTestResult(uuid);
        if (!found.isPresent()) {
            LOGGER.error("Could not update test case: test case with uuid {} not found", uuid);
        } else {
            TestResult testResult = (TestResult) found.get();
            this.notifier.beforeTestUpdate(testResult);
            update.accept(testResult);
            this.notifier.afterTestUpdate(testResult);
        }
    }

    public void stopTestCase(String uuid) {
        Optional<TestResult> found = this.storage.getTestResult(uuid);
        if (!found.isPresent()) {
            LOGGER.error("Could not stop test case: test case with uuid {} not found", uuid);
        } else {
            TestResult testResult = (TestResult) found.get();
            this.notifier.beforeTestStop(testResult);
            testResult.setStage(Stage.FINISHED).setStop(System.currentTimeMillis());
            this.threadContext.clear();
            this.notifier.afterTestStop(testResult);
        }
    }

    public void writeTestCase(String uuid) {
        Optional<TestResult> found = this.storage.getTestResult(uuid);
        if (!found.isPresent()) {
            LOGGER.error("Could not write test case: test case with uuid {} not found", uuid);
        } else {
            TestResult testResult = (TestResult) found.get();
            this.notifier.beforeTestWrite(testResult);
            this.writer.write(testResult);
            this.storage.remove(uuid);
            this.notifier.afterTestWrite(testResult);
        }
    }

    public void startStep(String uuid, StepResult result) {
        Optional<String> current = this.threadContext.getCurrent();
        if (!current.isPresent()) {
            LOGGER.error("Could not start step: no test case running");
        } else {
            String parentUuid = (String) current.get();
            this.startStep(parentUuid, uuid, result);
        }
    }

    public void startStep(String parentUuid, String uuid, StepResult result) {
        this.notifier.beforeStepStart(result);
        result.setStage(Stage.RUNNING);
        result.setStart(System.currentTimeMillis());
        this.threadContext.start(uuid);
        this.storage.put(uuid, result);
        this.storage.get(parentUuid, WithSteps.class).ifPresent((parentStep) -> {
            synchronized (this.storage) {
                parentStep.getSteps().add(result);
            }
        });
        this.notifier.afterStepStart(result);
    }

    public void updateStep(Consumer<StepResult> update) {
        Optional<String> current = this.threadContext.getCurrent();
        if (!current.isPresent()) {
            LOGGER.error("Could not update step: no step running");
        } else {
            String uuid = (String) current.get();
            this.updateStep(uuid, update);
        }
    }

    public void updateStep(String uuid, Consumer<StepResult> update) {
        Optional<StepResult> found = this.storage.getStep(uuid);
        if (!found.isPresent()) {
            LOGGER.error("Could not update step: step with uuid {} not found", uuid);
        } else {
            StepResult step = (StepResult) found.get();
            this.notifier.beforeStepUpdate(step);
            update.accept(step);
            this.notifier.afterStepUpdate(step);
        }
    }

    public void stopStep() {
        String root = (String) this.threadContext.getRoot().orElse(null);
        Optional<String> current = this.threadContext.getCurrent().filter((uuidx) -> {
            return !Objects.equals(uuidx, root);
        });
        if (!current.isPresent()) {
            LOGGER.error("Could not stop step: no step running");
        } else {
            String uuid = (String) current.get();
            this.stopStep(uuid);
        }
    }

    public void stopStep(String uuid) {
        Optional<StepResult> found = this.storage.getStep(uuid);
        if (!found.isPresent()) {
            LOGGER.error("Could not stop step: step with uuid {} not found", uuid);
        } else {
            StepResult step = (StepResult) found.get();
            this.notifier.beforeStepStop(step);
            step.setStage(Stage.FINISHED);
            step.setStop(System.currentTimeMillis());
            this.storage.remove(uuid);
            this.threadContext.stop();
            this.notifier.afterStepStop(step);
        }
    }

    public void addAttachment(String name, String type, String fileExtension, byte[] body) {
        this.addAttachment(name, type, fileExtension, new ByteArrayInputStream(body));
        ru.testit.model.request.Attachment attachment = new ru.testit.model.request.Attachment();
        attachment.setFileName(name + "." + fileExtension);
        attachment.setBytes(body);
        UniqueTest.addAttachment(attachment);
    }

    public void addAttachment(String name, String type, String fileExtension, InputStream stream) {
        this.writeAttachment(this.prepareAttachment(name, type, fileExtension), stream);
    }

    public String prepareAttachment(String name, String type, String fileExtension) {
        String extension = (String) Optional.ofNullable(fileExtension).filter((ext) -> {
            return !ext.isEmpty();
        }).map((ext) -> {
            return ext.charAt(0) == '.' ? ext : "." + ext;
        }).orElse("");
        String source = UUID.randomUUID().toString() + "-attachment" + extension;
        Optional<String> current = this.threadContext.getCurrent();
        if (!current.isPresent()) {
            LOGGER.error("Could not add attachment: no test is running");
            return source;
        } else {
            Attachment attachment = (new Attachment()).setName(this.isEmpty(name) ? null : name).setType(this.isEmpty(type) ? null : type).setSource(source);
            String uuid = (String) current.get();
            this.storage.get(uuid, WithAttachments.class).ifPresent((withAttachments) -> {
                synchronized (this.storage) {
                    withAttachments.getAttachments().add(attachment);
                }
            });
            return attachment.getSource();
        }
    }

    public void writeAttachment(String attachmentSource, InputStream stream) {
        this.writer.write(attachmentSource, stream);
    }

    private boolean isEmpty(String s) {
        return Objects.isNull(s) || s.isEmpty();
    }

    private static FileSystemResultsWriter getDefaultWriter() {
        Properties properties = PropertiesUtils.loadAllureProperties();
        String path = properties.getProperty("allure.results.directory", "allure-results");
        return new FileSystemResultsWriter(Paths.get(path));
    }

    private static LifecycleNotifier getDefaultNotifier() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return new LifecycleNotifier(ServiceLoaderUtils.load(ContainerLifecycleListener.class, classLoader), ServiceLoaderUtils.load(TestLifecycleListener.class, classLoader), ServiceLoaderUtils.load(FixtureLifecycleListener.class, classLoader), ServiceLoaderUtils.load(StepLifecycleListener.class, classLoader));
    }
}
