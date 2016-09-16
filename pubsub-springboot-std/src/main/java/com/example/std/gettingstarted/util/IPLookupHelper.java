package com.example.std.gettingstarted.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class IPLookupHelper {

    public static String determinePublicIP() throws IOException {
        Process result = Runtime.getRuntime().exec("curl ipinfo.io/ip");
        BufferedReader output = new BufferedReader(new InputStreamReader(result.getInputStream()));
        return output.readLine();
    }
}
