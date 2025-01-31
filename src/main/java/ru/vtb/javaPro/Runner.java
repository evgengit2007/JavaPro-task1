package ru.vtb.javaPro;

public class Runner {
    public Runner() {
    }

    @BeforeSuite
    public static void runMethodBeforeSuite() {
        System.out.println("runMethodBeforeSuite started");
    }

    @AfterSuite
    public static void runMethodAfterSuite() {
        System.out.println("runMethodAfterSuite started");
    }

    @BeforeTest
    public void runMethodBeforeTest() {
        System.out.println("  runMethodBeforeTest started");
    }

    @BeforeTest
    public void runMethodBeforeTestDouble() {
        System.out.println("  runMethodBeforeTestDouble started");
    }
    @AfterTest
    public void runMethodAfterTest() {
        System.out.println("  runMethodAfterTest started");
    }

    @Test(priority = 1)
    public void runMethodTest1() {
        System.out.println("    runMethodTest1 started");
    }

    @Test(priority = 2)
    public void runMethodTest2() {
        System.out.println("    runMethodTest2 started");
    }

    @Test
    public void runMethodTestDefault() {
        System.out.println("    runMethodTestDefault started");
    }

    @Test
    public void runMethodTestDefaultDouble() {
        System.out.println("    runMethodTestDefaultDouble started");
    }
    @Test(priority = 3)
    public void runMethodTest3() {
        System.out.println("    runMethodTest3 started");
    }

    @CsvSource("10,Java,20,true,2.5,2025")
    public void csvMethod(int a, String b, int c, boolean d, double e, long l) {
        System.out.println("    csvMethod started: a = " + a +
                "; b = " + b + "; c = " + c + "; d = " + d +
                "; e = " + e + "; l = " + l);
    }
    @CsvSource("10,true,20,Java,2.5,2025")
    public void csvMethodDouble(int a, boolean b, int c, String s, double d, long l) {
        System.out.println("    csvMethodDouble started: a = " + a +
                "; b = " + b + "; s = " + s + "; d = " + d +
                "; l = " + l);
    }
}
