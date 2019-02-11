package com.ekaen.sjl95.myble;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String A = "00001800-0000-1000-8000-00805f9b34fb";
    public static String B = "00001801-0000-1000-8000-00805f9b34fb";
    public static String C = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    public static String A_1 = "00002a00-0000-1000-8000-00805f9b34fb";
    public static String A_2 = "00002a01-0000-1000-8000-00805f9b34fb";
    public static String A_3 = "00002a04-0000-1000-8000-00805f9b34fb";
    public static String C_1 = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static String C_2 = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002a00-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put(A, "A");
        attributes.put(B, "B");
        attributes.put(C, "C");

        // Sample Characteristics.
        attributes.put(A_1, "A1");
        attributes.put(A_2, "A2");
        attributes.put(A_3, "A3");
        attributes.put(C_1, "C1");
        attributes.put(C_2, "C2");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
