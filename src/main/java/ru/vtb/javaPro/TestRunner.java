package ru.vtb.javaPro;

import com.sun.jdi.InvocationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class TestRunner {
    public static void main(String[] args) throws Exception{
        runTests(Runner.class);
        // для проверки
        Runner runner1 = new Runner(1, "1111", 2.5);
        runner1.runMethodTest1();
        Class<Runner> runnerClass = Runner.class;
        try {
            Method method = runnerClass.getDeclaredMethod("runMethodTest1");
            System.out.println(method);
            method.setAccessible(true);
            method.invoke(runner1);
            Annotation annotation = method.getDeclaredAnnotation(Test.class);
            System.out.println(annotation);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        Annotation[] annotations = runnerClass.getAnnotations();
        for (Annotation ann: annotations) {
            System.out.println(ann.annotationType().getName());
            if (ann instanceof Test) {
                System.out.println("Запущен Test");
            }
        }
        System.out.println("-------------");

    }

    static void runTests(Class c) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int priority;
        TreeMap<Integer, HashMap<Method, String>> treeMap = new TreeMap<>();
        Constructor constructor = c.getDeclaredConstructor();
        Object obj = constructor.newInstance();
        Method[] methods = c.getDeclaredMethods();
        try {
            for (Method method : methods) {
                System.out.println("method: " + method);
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    System.out.println("annotation: " + annotation);
                    if (annotation instanceof BeforeSuite) {
                        if (treeMap.containsKey(-1)) {
                            throw new RuntimeException("Annotation " + BeforeSuite.class + " more 1");
                        }
                        HashMap<Method, String> hashMap = new HashMap<>();
                        hashMap.put(method, "");
                        treeMap.put(-1, hashMap);
                    } else if (annotation instanceof AfterSuite) {
                        if (treeMap.containsKey(-2)) {
                            throw new RuntimeException("Annotation " + AfterSuite.class + " more 1");
                        }
                        HashMap<Method, String> hashMap = new HashMap<>();
                        hashMap.put(method, "");
                        treeMap.put(-2, hashMap);
                    } else {
                        String str = null;
                        if (annotation instanceof CsvSource) {
                            str = ((CsvSource) annotation).value();
                            System.out.println(str);
                        }
                        if (annotation instanceof Test) {
                            priority = ((Test) annotation).priority();
                        } else {
                            priority = treeMap.lastKey() + 1;
                        }
                        if (treeMap.containsKey(priority)) {
                            HashMap<Method, String> hashMap = new HashMap<>(treeMap.get(priority));
                            hashMap.put(method, str);
                            treeMap.put(priority, hashMap);
                        } else {
                            HashMap<Method, String> hashMap = new HashMap<>();
                            hashMap.put(method, str);
                            treeMap.put(priority, hashMap);
                        }
                    }
                }
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        // запишем с максимальным индексом метод с аннотацией AfterSuite (было -2, стало макс. значение индекса в TreeMap)
        treeMap.put(treeMap.lastKey()+1, treeMap.get(-2));
        treeMap.remove(-2);

        System.out.println("---Print list methods---");
        for (Map.Entry<Integer, HashMap<Method, String>> mapEntry: treeMap.entrySet()) {
            System.out.printf("Key: %s  Value: %s \n", mapEntry.getKey(), mapEntry.getValue());
        }

        // выполнение методов с аннотациями по порядку: сначала с BeforeSuite, затем с Test, затем с AfterSuite
        for (Map.Entry<Integer, HashMap<Method, String>> mapEntry: treeMap.entrySet()) {
            if (mapEntry.getKey() == -1) {
                // Выполнение метода с аннотацией BeforeSuite
                for (Method method: mapEntry.getValue().keySet()) {
                    method.setAccessible(true);
//                    method.invoke(obj, 1);
                    try {
                        method.invoke(null);
                    } catch (NullPointerException e) {
                        throw new RuntimeException("Method " + method.getName() + " is not static");
                    }
                }
            } else if (mapEntry.getKey() == treeMap.lastKey()) {
                for (Method method: mapEntry.getValue().keySet()) {
                    // Выполнение метода с аннотацией AfterSuite
                    method.setAccessible(true);
                    try {
                        method.invoke(null);
                    } catch (NullPointerException e) {
                        throw new RuntimeException("Method " + method.getName() + " is not static");
                    }
                }
            } else {
                for (Map.Entry<Method, String> hashMap : mapEntry.getValue().entrySet()) {
                    hashMap.getKey().setAccessible(true);
                    if (hashMap.getValue() == null) {
                        hashMap.getKey().invoke(obj, null);
                    } else {
                        Object[] arrObj = hashMap.getValue().split(", ");
//                        String[] arr = hashMap.getValue().split(", ");
//                        Object[] arrObj = new Object[arr.length];
                        for (Object str: arrObj) {
                            System.out.println(str);
//                            arrObj[0] = Integer.valueOf(str);
                        }
                        hashMap.getKey().invoke(obj, arrObj);
                    }
                }
            }
        }

        System.out.println("--End work method runTests--");
    }
}
