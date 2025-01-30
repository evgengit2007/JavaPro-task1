package ru.vtb.javaPro;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

    public class TestMethod implements Comparable<TestMethod> {
    private int priority;
    private Method method;

    public TestMethod() {
    }

    public TestMethod(int priority, Method method) {
        this.priority = priority;
        this.method = method;
    }

    public int getPriority() {
        return priority;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "TestMethod{" +
                "priority=" + priority +
                ", method=" + method +
                '}';
    }

    @Override
    public int compareTo(TestMethod o) {
        return this.priority - o.priority;
    }
}
