//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.junit.platform.engine.support.hierarchical;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.function.Predicate;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.engine.descriptor.*;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutorService.TestTask;
import org.junit.platform.engine.support.hierarchical.Node.ExecutionMode;

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

    public void invokeAll(List<? extends TestTask> tasks) {
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

    private static ConcurrentSkipListMap<Integer, CountDownLatch> tests = new ConcurrentSkipListMap<>();

    private void forkConcurrentTasks(List<? extends TestTask> tasks, Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> nonConcurrentTasks, Deque<ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask> concurrentTasksInReverseOrder) {
        tasks.sort(Comparator.comparingInt(i -> {
            JupiterTestDescriptor testDescriptor = null;
            Order o = null;
            try {
                Field field = i.getClass().getDeclaredField("testDescriptor");
                field.setAccessible(true);
                testDescriptor = (JupiterTestDescriptor) field.get(i);
                if (testDescriptor instanceof ClassTestDescriptor) {
                    o = ((ClassTestDescriptor) testDescriptor).getTestClass().getAnnotation(Order.class);
                } else if (testDescriptor instanceof MethodBasedTestDescriptor) {
                    o = ((MethodBasedTestDescriptor) testDescriptor).getTestMethod().getAnnotation(Order.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return o != null ? o.value() : 1;
        }));


        Map<Integer, Integer> ordersCount = new HashMap<>();
        Iterator var1 = tasks.iterator();
        while (var1.hasNext()) {
            TestTask testTask = (TestTask) var1.next();
            try {
                Field field = testTask.getClass().getDeclaredField("testDescriptor");
                field.setAccessible(true);
                JupiterTestDescriptor testDescriptor = (JupiterTestDescriptor) field.get(testTask);
                if (testDescriptor instanceof ClassTestDescriptor) {
                    Class<?> clz = ((ClassTestDescriptor) testDescriptor).getTestClass();
                    Order order = clz.getAnnotation(Order.class);
                    if (order != null)
                        ordersCount.put(order.value(), (ordersCount.getOrDefault(order.value(), 0)) + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ordersCount.forEach((k, v) -> {
            tests.put(k, new CountDownLatch(v));
        });

        Iterator var4 = tasks.iterator();
        while (var4.hasNext()) {
            TestTask testTask = (TestTask) var4.next();
            ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask exclusiveTask = new ForkJoinPoolHierarchicalTestExecutorService.ExclusiveTask(testTask);
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

    static class ExclusiveTask extends RecursiveAction {
        private final TestTask testTask;

        ExclusiveTask(TestTask testTask) {
            this.testTask = testTask;
        }

        public void compute() {

            Integer order = null;
            AbstractTestDescriptor testDescriptor = null;
            try {
                Field field = testTask.getClass().getDeclaredField("testDescriptor");
                field.setAccessible(true);
                testDescriptor = (AbstractTestDescriptor) field.get(testTask);
                if (testDescriptor instanceof ClassTestDescriptor) {
                    Class<?> clz = ((ClassTestDescriptor) testDescriptor).getTestClass();
                    Order o = clz.getAnnotation(Order.class);
                    if (o != null) {
                        order = o.value();
                        int i = 1;
                        CountDownLatch c = tests.lowerEntry(order).getValue();
                        if (c != null)
                            c.await();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


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

                if (order != null) {
                    CountDownLatch c = tests.get(order);
                    if (c != null)
                        c.countDown();
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
