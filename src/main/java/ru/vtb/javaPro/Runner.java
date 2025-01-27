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
    public static void runMethodBefore() {
        System.out.println("runMethodBefore started");
    }

/*
    @BeforeSuite
    public static void runMethodBeforeDouble() {
        System.out.println("runMethodBeforeDouble запущен");
    }
*/

    @AfterSuite
    public static void runMethodAfter() {
        System.out.println("runMethodAfter started");
    }

    @Test(priority = 1)
    public void runMethodTest1() {
        System.out.println("runMethodTest1 started");
    }

    @Test(priority = 2)
    public void runMethodTest2() {
        System.out.println("runMethodTest2 started");
    }

    @Test
    public void runMethodTestDefault() {
        System.out.println("runMethodTestDefault started");
    }

    @Test
    public void runMethodTestDefaultDouble() {
        System.out.println("runMethodTestDefaultDouble started");
    }
    @Test(priority = 3)
    public void runMethodTest3() {
        System.out.println("runMethodTest3 started");
    }

    @CsvSource("10, Java, 20, true")
    public void testMethod(int a, String b, int c, boolean d) {
        System.out.println("testMethod started:/n" + "a = " + a + "/nb = " + b);
    }
}
