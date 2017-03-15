package com.example.std.gettingstarted.util;

import java.util.UUID;

public class IdGenerator {
    public String next() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
