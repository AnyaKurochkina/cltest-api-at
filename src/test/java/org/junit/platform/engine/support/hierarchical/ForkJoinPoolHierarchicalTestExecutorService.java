//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.junit.platform.engine.support.hierarchical;


import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.ObjectPoolService;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.junit.MarkDelete;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.engine.descriptor.JupiterTestDescriptor;
import org.junit.jupiter.engine.descriptor.MethodBasedTestDescriptor;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node.ExecutionMode;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@API(
        status = Status.EXPERIMENTAL,
        since = "1.3"
)
@Log4j2
public class ForkJoinPoolHierarchicalTestExecutorService implements HierarchicalTestExecutorService {
    private final ForkJoinPool forkJoinPool;
    public static AtomicInteger parallelism = new AtomicInteger();

    public ForkJoinPoolHierarchicalTestExecutorService(ConfigurationParameters configurationParameters) {
        this(createConfiguration(configurationParameters));
    }

    @API(
            status = Status.EXPERIMENTAL,
            since = "1.7"
    )
    public ForkJoinPoolHierarchicalTestExecutorService(ParallelExecutionConfiguration configuration) {
        this.forkJoinPool = this.createForkJoinPool(configuration);
        parallelism.set(this.forkJoinPool.getParallelism());
        LoggerFactory.getLogger(this.getClass()).config(() -> "Using ForkJoinPool with parallelism of " + parallelism.get());
        System.out.printf("Parallelism = %d%n", ForkJoinPoolHierarchicalTestExecutorService.parallelism.get());
        System.out.printf("HeapSize = %.2fGB%n", Runtime.getRuntime().totalMemory() / (1024 * 1024 * 1024.0));
    }

    private static ParallelExecutionConfiguration createConfiguration(ConfigurationParameters configurationParameters) {
        ParallelExecutionConfigurationStrategy strategy = DefaultParallelExecutionConfigurationStrategy.getStrategy(configurationParameters);
        return strategy.createConfiguration(configurationParameters);
    }

    private ForkJoinPool createForkJoinPool(ParallelExecutionConfiguration configuration) {
        ForkJoinWorkerThreadFactory threadFactory = new ForkJoinPoolHierarchicalTestExecutorService.WorkerThreadFactory();
        return Try.call(() -> {
            Constructor<ForkJoinPool> constructor = ForkJoinPool.class.getDeclaredConstructor(Integer.TYPE, ForkJoinWorkerThreadFactory.class, UncaughtExceptionHandler.class, Boolean.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Predicate.class, Long.TYPE, TimeUnit.class);
            return constructor.newInstance(configuration.getParallelism(), threadFactory, null, false, configuration.getCorePoolSize(), configuration.getMaxPoolSize(), configuration.getMinimumRunnable(), null, configuration.getKeepAliveSeconds(), TimeUnit.SECONDS);
        }).orElseTry(() -> new ForkJoinPool(configuration.getParallelism(), threadFactory, (UncaughtExceptionHandler) null, false)).getOrThrow((cause) -> new JUnitException("Failed to create ForkJoinPool", cause));
    }

    public Future<Void> submit(TestTask testTask) {
        ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask exclusiveTask = new ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask(testTask);
        if (!this.isAlreadyRunningInForkJoinPool()) {
            return this.forkJoinPool.submit(exclusiveTask);
        } else if (testTask.getExecutionMode() == ExecutionMode.CONCURRENT && ForkJoinTask.getSurplusQueuedTaskCount() < parallelism.get()) {
            return exclusiveTask.fork();
        } else {
            exclusiveTask.compute();
            return null;
        }
    }

    private boolean isAlreadyRunningInForkJoinPool() {
        return ForkJoinTask.getPool() == this.forkJoinPool;
    }


    private static final ConcurrentHashMap<String, AbstractTestDescriptor> allTestMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<TestTask, String> deleteTests = new ConcurrentHashMap<>();
    final AtomicBoolean del = new AtomicBoolean(false);


    public static void addNode(AbstractTestDescriptor testDescriptor) {
        if (!testDescriptor.getChildren().isEmpty()) {
            testDescriptor.getChildren().forEach(t -> addNode((JupiterTestDescriptor) t));
        }
        if (testDescriptor instanceof MethodBasedTestDescriptor) {
            if (!((MethodBasedTestDescriptor) testDescriptor).getTestMethod().isAnnotationPresent(Disabled.class) &&
                    !((MethodBasedTestDescriptor) testDescriptor).getTestClass().isAnnotationPresent(Disabled.class))
                allTestMap.put(testDescriptor.getUniqueId().toString(), testDescriptor);
        }
    }

    public List<TestTask> invokeDeleteTest() {
        List<TestTask> list = new ArrayList<>();
        if (!deleteTests.isEmpty()) {
            list = new ArrayList<>(deleteTests.keySet());
            deleteTests.clear();
        }
        return list;
    }

    public void invokeAllRef(List<? extends TestTask> tasks2) {
        invokeAll(tasks2);
    }

    final AtomicBoolean first = new AtomicBoolean(true);

    @SneakyThrows
    private AbstractTestDescriptor getTestDescriptorFromTestTask(TestTask task) {
        Field field = task.getClass().getDeclaredField("testDescriptor");
        field.setAccessible(true);
        return (AbstractTestDescriptor) field.get(task);
    }

    public void invokeAll(List<? extends TestTask> tasks2) {
        ArrayList<TestTask> tasks = new ArrayList<>(tasks2);


        if (!del.get()) {
            Iterator var1 = tasks.iterator();
            while (var1.hasNext()) {
                TestTask testTask = (TestTask) var1.next();
                try {
                    AbstractTestDescriptor testDescriptor = getTestDescriptorFromTestTask(testTask);

                    if (first.get()) {
                        addNode(testDescriptor);
                    }
                    if (testDescriptor instanceof MethodBasedTestDescriptor) {
                        MarkDelete deleted = ((MethodBasedTestDescriptor) testDescriptor).getTestMethod().getAnnotation(MarkDelete.class);
                        if (deleted != null) {
                            deleteTests.put(testTask/*, deleted.value().getName()*/, "null");
                            var1.remove();
                            allTestMap.remove(testDescriptor.getUniqueId().toString());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //если запущены только @MarkDeleted тесты
        if (allTestMap.isEmpty() && !deleteTests.isEmpty()) {
            ObjectPoolService.deleteAllResources();
            tasks.addAll(invokeDeleteTest());
        }


        first.set(false);

        if (tasks.size() == 1) {
            (new ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask((TestTask) tasks.get(0))).compute();
        } else {
            Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> nonConcurrentTasks = new LinkedList<>();
            Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> concurrentTasksInReverseOrder = new LinkedList<>();
            this.forkConcurrentTasks(tasks, nonConcurrentTasks, concurrentTasksInReverseOrder);
            this.executeNonConcurrentTasks(nonConcurrentTasks);
            this.joinConcurrentTasksInReverseOrderToEnableWorkStealing(concurrentTasksInReverseOrder);
        }
    }


    private void forkConcurrentTasks(List<? extends TestTask> tasks, Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> nonConcurrentTasks, Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> concurrentTasksInReverseOrder) {

        Iterator var4 = tasks.iterator();
        while (var4.hasNext()) {
            TestTask testTask = (TestTask) var4.next();
            ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask exclusiveTask = new ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask(testTask);

            if (testTask.getExecutionMode() == ExecutionMode.CONCURRENT) {
                exclusiveTask.fork();
                concurrentTasksInReverseOrder.addFirst(exclusiveTask);

            } else {
                nonConcurrentTasks.add(exclusiveTask);
            }


        }

    }

    private void executeNonConcurrentTasks(Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> nonConcurrentTasks) {
        Iterator var2 = nonConcurrentTasks.iterator();

        while (var2.hasNext()) {
            ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask task = (ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask) var2.next();
            task.compute();
        }

    }

    private void joinConcurrentTasksInReverseOrderToEnableWorkStealing(Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> concurrentTasksInReverseOrder) {
        Iterator var2 = concurrentTasksInReverseOrder.iterator();

        while (var2.hasNext()) {
            ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask forkedTask = (ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask) var2.next();
            forkedTask.join();
        }

    }

    public void close() {
        this.forkJoinPool.shutdownNow();
    }

    static class WorkerThreadFactory implements ForkJoinWorkerThreadFactory {
        private final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        WorkerThreadFactory() {
        }

        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new ForkJoinPoolHierarchicalTestExecutorService.WorkerThread(pool, this.contextClassLoader);
        }
    }

    class ExclusiveTask extends RecursiveAction {
        private final TestTask testTask;

        ExclusiveTask(TestTask testTask) {
            this.testTask = testTask;
        }

        public void compute() {
            try {
                ResourceLock lock = this.testTask.getResourceLock().acquire();
                try {
                    this.testTask.execute();
                } catch (Throwable var5) {
                    if (lock != null) {
                        try {
                            lock.close();
                        } catch (Throwable var4) {
                            var5.addSuppressed(var4);
                        }
                    }

                    throw var5;
                } finally {
                    try {
                        AbstractTestDescriptor testDescriptor = getTestDescriptorFromTestTask(testTask);

                        if (testDescriptor instanceof MethodBasedTestDescriptor) {
                            allTestMap.remove(testDescriptor.getUniqueId().toString());
                        }

                        //если прошел последний тест
                        if (allTestMap.isEmpty() && !del.get()) {
                            del.set(true);
                            ObjectPoolService.deleteAllResources();
                            invokeAllRef(invokeDeleteTest());
                            log.debug("END ALL");
                        }

                        if (!allTestMap.isEmpty()) {
                            Set<Class<?>> currentClassListArgument = new HashSet<>();
                            for (AbstractTestDescriptor descriptor : allTestMap.values()) {
                                if (!(descriptor instanceof MethodBasedTestDescriptor))
                                    continue;
                                MethodBasedTestDescriptor methodBasedTestDescriptor = ((MethodBasedTestDescriptor) descriptor);
                                MarkDelete deleted = methodBasedTestDescriptor.getTestMethod().getAnnotation(MarkDelete.class);
                                if (deleted != null)
                                    continue;
                                currentClassListArgument.addAll(Arrays.asList(((MethodBasedTestDescriptor) descriptor).getTestMethod().getParameterTypes()));
                            }
                            ObjectPoolService.removeProducts(currentClassListArgument);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                if (lock != null) {
                    lock.close();
                }

            } catch (InterruptedException var6) {
                ExceptionUtils.throwAsUncheckedException(var6);
            }

        }
    }

    static class WorkerThread extends ForkJoinWorkerThread {
        WorkerThread(ForkJoinPool pool, ClassLoader contextClassLoader) {
            super(pool);
            this.setContextClassLoader(contextClassLoader);
        }
    }
}
