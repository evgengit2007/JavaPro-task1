package ru.vtb.javaPro;

import com.sun.jdi.InvocationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class TestRunner {
    public static void main(String[] args) throws Exception{
        runTests(Runner.class);
    }

    static void runTests(Class c) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int priority;
        TreeMap<Integer, HashMap<Method, String>> treeMap = new TreeMap<>();
        Method methodBeforeTest = null;
        Method methodAfterTest = null;
        Constructor constructor = c.getDeclaredConstructor();
        Object obj = constructor.newInstance();
        Method[] methods = c.getDeclaredMethods();
        try {
            // Определим методы BeforeTest и AfterTest, их не пишем в treeMap
            for (Method method : methods) {
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof BeforeTest) {
                        methodBeforeTest = method;
                    } else if (annotation instanceof AfterTest) {
                        methodAfterTest = method;
                    } else if (annotation instanceof BeforeSuite) {
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

        // Отладка
/*
        System.out.println("---Print list methods---");
        for (Map.Entry<Integer, HashMap<Method, String>> mapEntry: treeMap.entrySet()) {
            System.out.printf("Key: %s  Value: %s \n", mapEntry.getKey(), mapEntry.getValue());
        }
*/

        // выполнение методов с аннотациями по порядку: сначала с BeforeSuite,
        // затем с Test и другие, затем с AfterSuite
        // если есть методы с аннотациями BeforeTest и AfterTest (их может быть несколько),
        // то они выполняются до и соответственно после каждого метода
        // кроме BeforeSuite и AfterSuite
        for (Map.Entry<Integer, HashMap<Method, String>> mapEntry: treeMap.entrySet()) {
            if (mapEntry.getKey() == -1) {
                // Выполнение метода с аннотацией BeforeSuite
                for (Method method: mapEntry.getValue().keySet()) {
                    method.setAccessible(true);
                    try {
                        method.invoke(null);
                    } catch (NullPointerException e) {
                        throw new RuntimeException("Method " + method.getName() + " is not static");
                    }
                }
            } else if (mapEntry.getKey().equals(treeMap.lastKey())) {
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
                    Method method = hashMap.getKey();
                    String str = hashMap.getValue();
                    runMethod(methodBeforeTest, null, obj);
                    runMethod(method, str, obj);
                    runMethod(methodAfterTest, null, obj);
                }
            }
        }
    }
    static void runMethod(Method method, String str, Object obj) throws InvocationTargetException, IllegalAccessException
    {
        if (method == null) return;
        method.setAccessible(true);
        if (str == null) {
            method.invoke(obj);
        } else {
            Class[] parameterTypes = method.getParameterTypes();
            String[] arrStr = str.split(", ");
            // отладка
/*
                        for (Class classobj: parameterTypes) {
                            System.out.println(classobj.getName());
                        }
                        for (Object str: arrStr) {
                            System.out.println(str);
                        }
*/
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
