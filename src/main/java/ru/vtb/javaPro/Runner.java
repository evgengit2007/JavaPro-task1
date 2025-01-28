package ru.vtb.javaPro;

public class Runner {
    public Runner() {
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
    @BeforeTest
    public static void runMethodBeforeTest() {
        System.out.println("  runMethodBeforeTest started");
    }

    @AfterTest
    public static void runMethodAfterTest() {
        System.out.println("  runMethodAfterTest started");
    }

    @AfterSuite
    public static void runMethodAfter() {
        System.out.println("runMethodAfter started");
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

    @CsvSource("10, Java, 20, true, 2.5, 2025")
    public void testMethod(int a, String b, int c, boolean d, double e, long l) {
        System.out.println("    testMethod started: a = " + a +
                "; b = " + b + "; c = " + c + "; d = " + d +
                "; e = " + e + "; l = " + l);
    }
}
