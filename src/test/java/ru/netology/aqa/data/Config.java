package ru.netology.aqa.data;

public class Config {
    public static String localhostURL = System.getProperty("aqa-final.localhostURL");
    public static String datajsonLocation = System.getProperty("aqa-final.datajsonLocation");
    public static int secondstowait = Integer.parseInt(System.getProperty("aqa-final.secondstowait"));
    public static String dbURL = System.getProperty("db.url");
    public static String dbUser = System.getProperty("db.user");
    public static String dbPassword = System.getProperty("db.password");
}
