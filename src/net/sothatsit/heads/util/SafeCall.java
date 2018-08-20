package net.sothatsit.heads.util;

import com.google.common.base.Predicate;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SafeCall {

    private final ExceptionDetailer exceptionDetailer;

    private SafeCall() {
        exceptionDetailer = ExceptionDetailer.constructorDetailer();
    }

    protected RuntimeException fail(String message) {
        throw exceptionDetailer.detail(new IllegalStateException(message));
    }

    protected RuntimeException fail(String message, Throwable cause) {
        throw exceptionDetailer.detail(new IllegalStateException(message, cause));
    }

    public static Runnable runnable(Runnable runnable, String name) {
        return new SafeRunnable(runnable, name);
    }

    public static <T, R> SafeFunction<T, R> function(Function<T, R> function, String name) {
        return new SafeFunction<>(function, name);
    }

    public static <T, R> NonNullSafeFunction<T, R> nonNullFunction(Function<T, R> function, String name) {
        return new NonNullSafeFunction<>(function, name);
    }

    public static <T> SafePredicate<T> predicate(Predicate<T> predicate, String name) {
        return new SafePredicate<>(predicate, name);
    }

    public static <T> NonNullSafePredicate<T> nonNullPredicate(Predicate<T> predicate, String name) {
        return new NonNullSafePredicate<>(predicate, name);
    }

    public static <V> SafeCallable<V> callable(Callable<V> callable, String name) {
        return new SafeCallable<>(callable, name);
    }

    public static <V> NonNullSafeCallable<V> nonNullCallable(Callable<V> callable, String name) {
        return new NonNullSafeCallable<>(callable, name);
    }

    public static <T> SafeConsumer<T> consumer(Consumer<T> consumer, String name) {
        return new SafeConsumer<>(consumer, name);
    }

    public static <T> NonNullSafeConsumer<T> nonNullConsumer(Consumer<T> consumer, String name) {
        return new NonNullSafeConsumer<>(consumer, name);
    }

    public static class SafeRunnable extends SafeCall implements Runnable {

        private final Runnable runnable;
        protected final String name;

        private SafeRunnable(Runnable runnable, String name) {
            Checks.ensureNonNull(runnable, "runnable");
            Checks.ensureNonNull(name, "name");

            this.runnable = runnable;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch(Exception e) {
                throw fail("Exception thrown when calling function " + name, e);
            }
        }

        @Override
        public String toString() {
            return "Safe " + runnable + " (" + name + ")";
        }

    }

    public static class SafeFunction<T, R> extends SafeCall implements Function<T, R> {

        private final Function<T, R> function;
        protected final String name;

        private SafeFunction(Function<T, R> function, String name) {
            Checks.ensureNonNull(function, "function");
            Checks.ensureNonNull(name, "name");

            this.function = function;
            this.name = name;
        }

        @Override
        public R apply(T t) {
            try {
                return function.apply(t);
            } catch(Exception e) {
                throw fail("Exception thrown when calling function " + name, e);
            }
        }

        @Override
        public String toString() {
            return "Safe " + function + " (" + name + ")";
        }

    }

    public static class NonNullSafeFunction<T, R> extends SafeFunction<T, R> {

        private NonNullSafeFunction(Function<T, R> function, String name) {
            super(function, name);
        }

        @Override
        public R apply(T t) {
            Checks.ensureNonNull(t, "argument");

            R returnValue = super.apply(t);

            if(returnValue == null)
                throw fail(name + " function returned a null value");

            return returnValue;
        }

        @Override
        public String toString() {
            return "NonNull " + super.toString();
        }

    }

    public static class SafePredicate<T> extends SafeCall implements Predicate<T> {

        private final Predicate<T> predicate;
        protected final String name;

        private SafePredicate(Predicate<T> predicate, String name) {
            Checks.ensureNonNull(predicate, "predicate");
            Checks.ensureNonNull(name, "name");

            this.predicate = predicate;
            this.name = name;
        }

        @Override
        public boolean apply(T t) {
            try {
                return predicate.apply(t);
            } catch(Exception e) {
                throw fail("Exception thrown when calling predicate " + name, e);
            }
        }

        @Override
        public String toString() {
            return "Safe " + predicate + " (" + name + ")";
        }

    }

    public static class NonNullSafePredicate<T> extends SafePredicate<T> {

        private NonNullSafePredicate(Predicate<T> predicate, String name) {
            super(predicate, name);
        }

        @Override
        public boolean apply(T t) {
            Checks.ensureNonNull(t, "argument");

            return super.apply(t);
        }

        @Override
        public String toString() {
            return "NonNull " + super.toString();
        }

    }

    public static class SafeCallable<V> extends SafeCall implements Callable<V> {

        private final Callable<V> callable;
        protected final String name;

        private SafeCallable(Callable<V> callable, String name) {
            Checks.ensureNonNull(callable, "callable");
            Checks.ensureNonNull(name, "name");

            this.callable = callable;
            this.name = name;
        }

        @Override
        public V call() {
            try {
                return callable.call();
            } catch(Exception e) {
                throw fail("Exception thrown when calling callable " + name, e);
            }
        }

        @Override
        public String toString() {
            return "Safe " + callable + " (" + name + ")";
        }

    }

    public static class NonNullSafeCallable<V> extends SafeCallable<V> {

        private NonNullSafeCallable(Callable<V> callable, String name) {
            super(callable, name);
        }

        @Override
        public V call() {
            V returnValue = super.call();

            if(returnValue == null)
                throw fail(name + " callable returned a null value");

            return returnValue;
        }

        @Override
        public String toString() {
            return "NonNull " + super.toString();
        }

    }

    public static class SafeConsumer<T> extends SafeCall implements Consumer<T> {

        private final Consumer<T> consumer;
        protected final String name;

        private SafeConsumer(Consumer<T> consumer, String name) {
            Checks.ensureNonNull(consumer, "consumer");
            Checks.ensureNonNull(name, "name");

            this.consumer = consumer;
            this.name = name;
        }

        @Override
        public void accept(T t) {
            try {
                consumer.accept(t);
            } catch(Exception e) {
                throw fail("Exception thrown when calling predicate " + name, e);
            }
        }

        @Override
        public String toString() {
            return "Safe " + consumer + " (" + name + ")";
        }

    }

    public static class NonNullSafeConsumer<T> extends SafeConsumer<T> {

        private NonNullSafeConsumer(Consumer<T> consumer, String name) {
            super(consumer, name);
        }

        @Override
        public void accept(T t) {
            Checks.ensureNonNull(t, "argument");

            super.accept(t);
        }

        @Override
        public String toString() {
            return "NonNull " + super.toString();
        }

    }

}
