package ru.vtb.javaPro;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class TestRunner {
    public static void main(String[] args) throws Exception{
        runTests(Runner.class);
    }

    static void runTests(Class c) throws Exception{
        List<Method> beforeSuiteMethods = new ArrayList<>();
        List<Method> afterSuiteMethods = new ArrayList<>();
        List<Method> allMethods = new ArrayList<>();
        List<TestMethod> testMethods = new ArrayList<>();
        List<Method> beforeTestMethods = new ArrayList<>();
        List<Method> afterTestMethods = new ArrayList<>();
        List<Method> csvSourceMethods = new ArrayList<>();
        List<Integer> integerList = List.of(1,2,3,4,5,6,7,8,9,10);
        Constructor constructor = c.getDeclaredConstructor();
        Object obj = constructor.newInstance();
        Method[] methods = c.getDeclaredMethods();
        for (Method method: methods) {
            if (method.isAnnotationPresent(Test.class)) {
                Annotation annotation = method.getAnnotation(Test.class);
                int priority = ((Test) annotation).priority();
                if (!integerList.contains(priority)) throw new Exception("Priority not in array 1-10");
                testMethods.add(new TestMethod(priority, method));
            }
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (beforeSuiteMethods.size() > 1) throw new Exception("Double annotation BeforeSuite");
                if (!Modifier.toString(method.getModifiers()).contains("static"))
                    throw new Exception("Method with annotation BeforeSuite is not static");
                beforeSuiteMethods.add(method);
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                if (afterSuiteMethods.size() > 1) throw new Exception("Double annotation AfterSuite");
                if (!Modifier.toString(method.getModifiers()).contains("static"))
                    throw new Exception("Method with annotation AfterSuite is not static");
                afterSuiteMethods.add(method);
            }
            if (method.isAnnotationPresent(BeforeTest.class)) {
                beforeTestMethods.add(method);
            }
            if (method.isAnnotationPresent(AfterTest.class)) {
                afterTestMethods.add(method);
            }
            if (method.isAnnotationPresent(CsvSource.class)) {
                csvSourceMethods.add(method);
            }
        }
        testMethods = testMethods.stream()
                .sorted(Comparator.comparing(testMethod -> testMethod.getPriority()))
                .toList();
        for (TestMethod testMethod: testMethods) {
            allMethods.addAll(beforeTestMethods);
            allMethods.add(testMethod.getMethod());
            allMethods.addAll(afterTestMethods);
        }

        // Running methods in order
        // first method with BeforeSuite annotation
        if (beforeSuiteMethods.size() == 1) {
            beforeSuiteMethods.get(0).setAccessible(true);
            beforeSuiteMethods.get(0).invoke(null);
        }
        // Then methods with Test annotation
        for (int i = 0; i < allMethods.size(); i++) {
            runMethod(allMethods.get(i), obj, null);
        }
        // Then methods with CsvSource annotation
        for (Method method: csvSourceMethods) {
            Annotation annotation = method.getAnnotation(CsvSource.class);
            String str = ((CsvSource) annotation).value();
            runMethod(method, obj, str);
        }
        // Then methods with AfterSuite annotation
        if (afterSuiteMethods.size() == 1) {
            afterSuiteMethods.get(0).setAccessible(true);
            afterSuiteMethods.get(0).invoke(null);
        }
    }

    static void runMethod(Method method, Object obj, String param) throws InvocationTargetException, IllegalAccessException
    {
        if (method == null) return;
        method.setAccessible(true);
        if (param == null) {
            method.invoke(obj);
        } else {
            Class[] parameterTypes = method.getParameterTypes();
            String[] arrStr = param.split(", ");
            if (parameterTypes.length != arrStr.length) {
                throw new RuntimeException("Count parameters in the annotation not equals signature method");
            }
            Object[] arrObj = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                switch (parameterTypes[i].getName()) {
                    case "int": arrObj[i] = Integer.parseInt(arrStr[i]); break;
                    case "java.lang.String": arrObj[i] = arrStr[i]; break;
                    case "boolean": arrObj[i] = Boolean.parseBoolean(arrStr[i]); break;
                    case "double": arrObj[i] = Double.parseDouble(arrStr[i]); break;
                    case "long": arrObj[i] = Long.parseLong(arrStr[i]); break;
                }
            }
            method.invoke(obj, arrObj);
        }
    }
}
