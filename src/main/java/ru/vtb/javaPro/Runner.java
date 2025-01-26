package ru.vtb.javaPro;

@Test("2.0")
public class Runner {
    private int fieldInt;
    private String fieldStr;
    private Double fieldDouble;

    public Runner(int fieldInt, String fieldStr, Double fieldDouble) {
        this.fieldInt = fieldInt;
        this.fieldStr = fieldStr;
        this.fieldDouble = fieldDouble;
    }

    @BeforeSuite
    public static void runMethod1(int a) {
        System.out.println("runMethod1 запущен");
    }

    @AfterSuite
    public static String runMethod2(String str) {
        return "runMethod2 запущен. Параметр: " + str;
    }

    @Test
    public void runMethod3() {
        System.out.println("runMethod3 запущен");
    }
}
