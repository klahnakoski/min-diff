package com.mindiff;

import com.mindiff.service.FilterServer;

public class Main {
    public static void main(String[] args) {
        new FilterServer().start(8080);
    }
}
