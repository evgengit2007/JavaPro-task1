package ru.vtb.javaPro;

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
                for (Method method : mapEntry.getValue().keySet()) {
                    method.setAccessible(true);
                    try {
                        method.invoke(null);
                    } catch (NullPointerException e) {
                        throw new RuntimeException("Method " + method.getName() + " is not static");
                    }
                }
            } else if (mapEntry.getKey() == -2) { // пропускаем, этот метод должен быть последним
            } else {
                for (Map.Entry<Method, String> hashMap : mapEntry.getValue().entrySet()) {
                    Method method = hashMap.getKey();
                    String str = hashMap.getValue();
                    runMethod(methodBeforeTest, obj, null);
                    runMethod(method, obj, str);
                    runMethod(methodAfterTest, obj, null);
                    System.out.println("  -------");
                }
            }
        }
        // запуск метода с аннотацией AfterSuite (значение индекса в TreeMap = -2)
        if (treeMap.get(-2) != null) {
            HashMap<Method, String> hashMap = treeMap.get(-2);
            for (Map.Entry<Method, String> method : hashMap.entrySet()) {
                // Выполнение метода с аннотацией AfterSuite
                method.getKey().setAccessible(true);
                try {
                    method.getKey().invoke(null);
                } catch (NullPointerException e) {
                    throw new RuntimeException("Method " + method.getKey().getName() + " is not static");
                }
            }
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
            // отладка
/*
            for (Class classobj: parameterTypes) {
                System.out.println(classobj.getName());
            }
            for (Object strTemp: arrStr) {
                System.out.println(strTemp);
            }
            System.out.println(parameterTypes.length);
            System.out.println(arrStr.length);
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
