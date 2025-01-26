package ru.vtb.javaPro;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class TestRunner {
    public static void main(String[] args) {
        Runner runner1 = new Runner(1, "1111", 2.5);
        runner1.runMethod3();
        Class<Runner> runnerClass = Runner.class;
        try {
            Method method = runnerClass.getDeclaredMethod("runMethod3");
            System.out.println(method);
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
                System.out.println("Нашли Test");
            }
        }
        System.out.println("-------------");
        runTests(Runner.class);

    }

    static void runTests(Class c) {
        try {
            Class<?> cClass = c;
            AnnotatedElement element = cClass;
            Annotation[] annotations = element.getAnnotations();
            for (Annotation ann : annotations) {
                System.out.println(ann.annotationType().getName());
                if (ann instanceof Test test) {
                    System.out.println("Нашли Test");
                }
            }
            System.out.println("-------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}