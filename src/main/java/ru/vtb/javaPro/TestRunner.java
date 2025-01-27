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
        TreeMap<Integer, ArrayList<Method>> treeMap = new TreeMap<>();
        Constructor constructor = c.getDeclaredConstructor();
        Object obj = constructor.newInstance();
        Method[] methods = c.getDeclaredMethods();
        try {
            for (Method method : methods) {
                System.out.println(method);
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    System.out.println(annotation);
                    if (annotation instanceof Test) {
                        int priority = ((Test) annotation).priority();
                        if (treeMap.containsKey(priority)) {
                            ArrayList<Method> listTest = new ArrayList<>(treeMap.get(priority));
                            listTest.add(method);
                            treeMap.put(priority, listTest);
                        } else {
                            treeMap.put(priority, new ArrayList<>(Collections.singleton(method)));
                        }
                    } else if (annotation instanceof BeforeSuite) {
                        if (treeMap.containsKey(-1)) {
                            throw new RuntimeException("Аннотации " + BeforeSuite.class + " больше 1");
                        }
                        treeMap.put(-1, new ArrayList<>(Collections.singleton(method)));
                    } else if (annotation instanceof AfterSuite) {
                        if (treeMap.containsKey(-2)) {
                            throw new RuntimeException("Аннотации " + AfterSuite.class + " больше 1");
                        }
                        treeMap.put(-2, new ArrayList<>(Collections.singleton(method)));
                    }
                }
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        // запишем с максимальным индексом метод с аннотацией AfterSuite (было -2, стало макс. значение индекса в TreeMap)
        treeMap.put(treeMap.lastKey()+1, treeMap.get(-2));
        treeMap.remove(-2);
        System.out.println("---Вывод списка методов---");
        System.out.println(treeMap.get(-1));
        for (Map.Entry<Integer, ArrayList<Method>> arrayListMap: treeMap.entrySet()) {
            System.out.printf("Key: %s  Value: %s \n", arrayListMap.getKey(), arrayListMap.getValue());
        }

        // выполнение методов с аннотациями по порядку: сначала с BeforeSuite, затем с Test, затем с AfterSuite
        for (Map.Entry<Integer, ArrayList<Method>> arrayListMap: treeMap.entrySet()) {
            if (arrayListMap.getKey() == -1) {
                // Выполнение метода с аннотацией BeforeSuite
                for (Method method: arrayListMap.getValue()) {
                    method.setAccessible(true);
                    method.invoke(obj, 1);
                }
            } else if (arrayListMap.getKey() == treeMap.lastKey()) {
                for (Method method: arrayListMap.getValue()) {
                    // Выполнение метода с аннотацией AfterSuite
                    method.setAccessible(true);
                    System.out.println(method.invoke(obj, "Тестовая строка"));
                }
            } else {
                for (Method method : arrayListMap.getValue()) {
                    method.setAccessible(true);
                    method.invoke(obj);
                }
            }
        }

        System.out.println("--Конец работы метода runTests--");
    }
}
