package ru.vtb.javaPro;

public class Runner {
    private int fieldInt;
    private String fieldStr;
    private Double fieldDouble;

    public Runner() {
    }

    public Runner(int fieldInt, String fieldStr, Double fieldDouble) {
        this.fieldInt = fieldInt;
        this.fieldStr = fieldStr;
        this.fieldDouble = fieldDouble;
    }

    @BeforeSuite
    public static void runMethodBefore(int a) {
        System.out.println("runMethodBefore запущен");
    }

/*
    @BeforeSuite
    public static void runMethodBeforeDouble(int a) {
        System.out.println("runMethodBeforeDouble запущен");
    }
*/

    @AfterSuite
    public static String runMethodAfter(String str) {
        return "runMethodAfter запущен. Параметр: " + str;
    }

    @Test(priority = 1)
    public void runMethodTest1() {
        System.out.println("runMethodTest1 запущен");
    }

    @Test(priority = 2)
    public void runMethodTest2() {
        System.out.println("runMethodTest2 запущен");
    }

    @Test
    public void runMethodTestDefault() {
        System.out.println("runMethodTestDefault запущен");
    }

    @Test
    public void runMethodTestDefaultDouble() {
        System.out.println("runMethodTestDefaultDouble запущен");
    }
    @Test(priority = 3)
    public void runMethodTest3() {
        System.out.println("runMethodTest3 запущен");
    }
}
