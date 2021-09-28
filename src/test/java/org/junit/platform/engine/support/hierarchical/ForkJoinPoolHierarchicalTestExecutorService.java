//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.junit.platform.engine.support.hierarchical;


import core.helper.Deleted;
import lombok.SneakyThrows;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.jupiter.engine.descriptor.JupiterTestDescriptor;
import org.junit.jupiter.engine.descriptor.MethodBasedTestDescriptor;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node.ExecutionMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

@API(
        status = Status.EXPERIMENTAL,
        since = "1.3"
)
public class ForkJoinPoolHierarchicalTestExecutorService implements HierarchicalTestExecutorService {
    private final ForkJoinPool forkJoinPool;
    private final int parallelism;

    public ForkJoinPoolHierarchicalTestExecutorService(ConfigurationParameters configurationParameters) {
        this(createConfiguration(configurationParameters));
    }

    @API(
            status = Status.EXPERIMENTAL,
            since = "1.7"
    )
    public ForkJoinPoolHierarchicalTestExecutorService(ParallelExecutionConfiguration configuration) {
        this.forkJoinPool = this.createForkJoinPool(configuration);
        this.parallelism = this.forkJoinPool.getParallelism();
        LoggerFactory.getLogger(this.getClass()).config(() -> {
            return "Using ForkJoinPool with parallelism of " + this.parallelism;
        });
    }

    private static ParallelExecutionConfiguration createConfiguration(ConfigurationParameters configurationParameters) {
        ParallelExecutionConfigurationStrategy strategy = DefaultParallelExecutionConfigurationStrategy.getStrategy(configurationParameters);
        return strategy.createConfiguration(configurationParameters);
    }

    private ForkJoinPool createForkJoinPool(ParallelExecutionConfiguration configuration) {
        ForkJoinWorkerThreadFactory threadFactory = new ForkJoinPoolHierarchicalTestExecutorService.WorkerThreadFactory();
        return (ForkJoinPool) Try.call(() -> {
            Constructor<ForkJoinPool> constructor = ForkJoinPool.class.getDeclaredConstructor(Integer.TYPE, ForkJoinWorkerThreadFactory.class, UncaughtExceptionHandler.class, Boolean.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Predicate.class, Long.TYPE, TimeUnit.class);
            return (ForkJoinPool) constructor.newInstance(configuration.getParallelism(), threadFactory, null, false, configuration.getCorePoolSize(), configuration.getMaxPoolSize(), configuration.getMinimumRunnable(), null, configuration.getKeepAliveSeconds(), TimeUnit.SECONDS);
        }).orElseTry(() -> {
            return new ForkJoinPool(configuration.getParallelism(), threadFactory, (UncaughtExceptionHandler) null, false);
        }).getOrThrow((cause) -> {
            return new JUnitException("Failed to create ForkJoinPool", cause);
        });
    }

    public Future<Void> submit(TestTask testTask) {
        ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask exclusiveTask = new ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask(testTask);
        if (!this.isAlreadyRunningInForkJoinPool()) {
            return this.forkJoinPool.submit(exclusiveTask);
        } else if (testTask.getExecutionMode() == ExecutionMode.CONCURRENT && ForkJoinTask.getSurplusQueuedTaskCount() < this.parallelism) {
            return exclusiveTask.fork();
        } else {
            exclusiveTask.compute();
            return null;
        }
    }

    private boolean isAlreadyRunningInForkJoinPool() {
        return ForkJoinTask.getPool() == this.forkJoinPool;
    }


    private static final ConcurrentHashMap<String, JupiterTestDescriptor> mapTests = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, TestTask> deleteTests = new ConcurrentHashMap<>();
    final AtomicBoolean del = new AtomicBoolean(false);


    public static void addNode(JupiterTestDescriptor testDescriptor) {
        try {
            if (testDescriptor.getChildren().size() > 0) {
                testDescriptor.getChildren().forEach(t -> {
                    try {
                        addNode((JupiterTestDescriptor) t);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (testDescriptor instanceof MethodBasedTestDescriptor) {
            mapTests.put(testDescriptor.getUniqueId().toString(), testDescriptor);
        }
    }
    public void invokeAll2() {
        invokeAll(new ArrayList<>(deleteTests.values()));
    }

    public void invokeAll(List<? extends TestTask> tasks2) {

        ArrayList<TestTask> tasks = new ArrayList<>(tasks2);


        if(!del.get()) {
            Iterator var1 = tasks.iterator();
            while (var1.hasNext()) {
                TestTask testTask = (TestTask) var1.next();
                Field field = null;
                try {
                    field = testTask.getClass().getDeclaredField("testDescriptor");
                    field.setAccessible(true);
                    JupiterTestDescriptor testDescriptor = (JupiterTestDescriptor) field.get(testTask);
                    if (mapTests.isEmpty()) {
                        addNode(testDescriptor);
                    }
                    if (testDescriptor instanceof MethodBasedTestDescriptor) {
                        Deleted deleted = ((MethodBasedTestDescriptor) testDescriptor).getTestMethod().getAnnotation(Deleted.class);
                        if (deleted != null) {
                            deleteTests.put(testTask.toString(), testTask);
                            System.out.println("!!!!!!!!!!!!!!! " + testTask);
                            var1.remove();
                            mapTests.remove(testDescriptor.getUniqueId().toString());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

//        tasks.forEach(testTask -> {
//            Field field = null;
//            try {
//                field = testTask.getClass().getDeclaredField("testDescriptor");
//                field.setAccessible(true);
//                JupiterTestDescriptor testDescriptor = (JupiterTestDescriptor) field.get(testTask);
//
//                if (testDescriptor instanceof MethodBasedTestDescriptor) {
//                    mapTests.remove(testDescriptor.getUniqueId().toString());
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//        if(mapTests.isEmpty()) {
//            tasks.addAll(deleteTests.values());
//        }

        System.out.println(tasks2.size());
//        ArrayList<TestTask> tasks = new ArrayList<>();
//        if(mapTests.containsKey("0")) {
//            tasks.add(mapTests.get("0"));
//        }
//        tasks.addAll(tasks2);
//
//        Iterator var1 = tasks.iterator();
//        while (var1.hasNext()) {
//            TestTask testTask = (TestTask) var1.next();
//            try {
//                Field field = testTask.getClass().getDeclaredField("testDescriptor");
//                field.setAccessible(true);
//                JupiterTestDescriptor testDescriptor = (JupiterTestDescriptor) field.get(testTask);
//                if (testDescriptor instanceof MethodBasedTestDescriptor) {
//                    Class<?> clz = ((MethodBasedTestDescriptor) testDescriptor).getTestClass();
//                    if(((MethodBasedTestDescriptor) testDescriptor).getTestMethod().isAnnotationPresent(Create.class)) {
//                        mapTests.put("0", testTask);
//                        synchronized (this){
//                            isTake++;
//                        }
//                        if(isTake != 2)
//                            var1.remove();
//                    }
//                }
//            } catch (Exception e){
//                System.out.println("invokeAll");
//            }
//        }
//
//        if(tasks.size() == 0)
//            return;


        if (tasks.size() == 1) {
            (new ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask((TestTask) tasks.get(0))).compute();
        } else {
            Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> nonConcurrentTasks = new LinkedList();
            Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> concurrentTasksInReverseOrder = new LinkedList();
            this.forkConcurrentTasks(tasks, nonConcurrentTasks, concurrentTasksInReverseOrder);
            this.executeNonConcurrentTasks(nonConcurrentTasks);
            this.joinConcurrentTasksInReverseOrderToEnableWorkStealing(concurrentTasksInReverseOrder);
        }
    }

//    private static ConcurrentSkipListMap<Integer, CountDownLatch> tests = new ConcurrentSkipListMap<>();
//    private static Properties prop = new Properties();

    private void forkConcurrentTasks(List<? extends TestTask> tasks, Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> nonConcurrentTasks, Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> concurrentTasksInReverseOrder) {
//        try (InputStream input = new FileInputStream("src/test/resources/config/classOrders.properties")) {
//            prop.load(input);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        tasks.sort(Comparator.comparingInt(i -> {
//            JupiterTestDescriptor testDescriptor = null;
//            int o = 1;
//            try {
//                Field field = i.getClass().getDeclaredField("testDescriptor");
//                field.setAccessible(true);
//                testDescriptor = (JupiterTestDescriptor) field.get(i);
//                if (testDescriptor instanceof ClassTestDescriptor) {
//                    //o = ((ClassTestDescriptor) testDescriptor).getTestClass().getAnnotation(Order.class);
//                    String className = ((ClassTestDescriptor) testDescriptor).getTestClass().getName();
//                    String order = prop.getProperty(className);
//                    if (order != null)
//                        o = Integer.parseInt(order);
//                } else if (testDescriptor instanceof MethodBasedTestDescriptor) {
//                    Order order = ((MethodBasedTestDescriptor) testDescriptor).getTestMethod().getAnnotation(Order.class);
//                    if (order != null)
//                        o = order.value();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return o;
//        }));
//
//
//        Map<Integer, Integer> ordersCount = new HashMap<>();
//        Iterator var1 = tasks.iterator();
//        while (var1.hasNext()) {
//            TestTask testTask = (TestTask) var1.next();
//            try {
//                Field field = testTask.getClass().getDeclaredField("testDescriptor");
//                field.setAccessible(true);
//                JupiterTestDescriptor testDescriptor = (JupiterTestDescriptor) field.get(testTask);
//                if (testDescriptor instanceof ClassTestDescriptor) {
//                   /* Class<?> clz = ((ClassTestDescriptor) testDescriptor).getTestClass();
//                    Order order = clz.getAnnotation(Order.class);
//                    if (order != null)
//                        ordersCount.put(order.value(), (ordersCount.getOrDefault(order.value(), 0)) + 1);*/
//
//                    String className = ((ClassTestDescriptor) testDescriptor).getTestClass().getName();
//                    String order = prop.getProperty(className);
//                    if (order != null)
//                        ordersCount.put(Integer.parseInt(order), (ordersCount.getOrDefault(Integer.parseInt(order), 0)) + 1);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        ordersCount.forEach((k, v) -> {
//            tests.put(k, new CountDownLatch(v));
//        });


        Iterator var4 = tasks.iterator();
        while (var4.hasNext()) {
            TestTask testTask = (TestTask) var4.next();
            ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask exclusiveTask = new ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask(testTask);


//            String order = null;
//            AbstractTestDescriptor testDescriptor = null;
//            try {
//                Field field = testTask.getClass().getDeclaredField("testDescriptor");
//                field.setAccessible(true);
//                testDescriptor = (AbstractTestDescriptor) field.get(testTask);
//                if (testDescriptor instanceof ClassTestDescriptor) {
//                    //Class<?> clz = ((ClassTestDescriptor) testDescriptor).getTestClass();
//                    //Order o = clz.getAnnotation(Order.class);
//                    String className = ((ClassTestDescriptor) testDescriptor).getTestClass().getName();
//                    order = prop.getProperty(className);
//                    if (order != null) {
//                        if (tests.lowerEntry(Integer.parseInt(order)) != null) {
//                            CountDownLatch c = tests.lowerEntry(Integer.parseInt(order)).getValue();
//                            if (c != null)
//                                c.await();
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            if (testTask.getExecutionMode() == ExecutionMode.CONCURRENT) {

                exclusiveTask.fork();
                concurrentTasksInReverseOrder.addLast(exclusiveTask);

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
                }

                if (lock != null) {
                    lock.close();
                }


                Field field = null;
                try {
                    field = testTask.getClass().getDeclaredField("testDescriptor");
                    field.setAccessible(true);
                    AbstractTestDescriptor testDescriptor = (AbstractTestDescriptor) field.get(testTask);

                    if (testDescriptor instanceof MethodBasedTestDescriptor) {
                        mapTests.remove(testDescriptor.getUniqueId().toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (mapTests.isEmpty() && !del.get()) {
                    del.set(true);
                    invokeAll2();
                }



//                Integer order = null;
//                AbstractTestDescriptor testDescriptor = null;
//                try {
//                    Field field = testTask.getClass().getDeclaredField("testDescriptor");
//                    field.setAccessible(true);
//                    testDescriptor = (AbstractTestDescriptor) field.get(testTask);
//                    if (testDescriptor instanceof ClassTestDescriptor) {
////                        Class<?> clz = ((ClassTestDescriptor) testDescriptor).getTestClass();
////                        Order o = clz.getAnnotation(Order.class);
//                        String className = ((ClassTestDescriptor) testDescriptor).getTestClass().getName();
//                        String o = prop.getProperty(className);
//                        if (o != null) {
//                            order = Integer.parseInt(o);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                if (order != null) {
//                    CountDownLatch c = tests.get(order);
//                    if (c != null)
//                        c.countDown();
//                }

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
