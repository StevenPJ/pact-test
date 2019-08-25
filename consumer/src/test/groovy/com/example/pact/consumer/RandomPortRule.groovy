package com.example.pact.consumer

import org.junit.rules.ExternalResource

class RandomPortRule extends ExternalResource {
    // Value of port is stored in environment variable so that it can be used in property e.g. @SpringBootTest(properties = "myUrlProperty:http://localhost:${RANDOM_PORT}")
    public static String ENV_VARIABLE = "RANDOM_PORT"

    @Override
    protected void before() throws Throwable {
        try {
            ServerSocket serverSocket = new ServerSocket(0)
            System.setProperty(ENV_VARIABLE, String.valueOf(serverSocket.getLocalPort()))
        } catch (Exception e) {
            println "hey"
            throw e
        }
    }

    int getPort() {
        return Integer.valueOf(System.getProperty(ENV_VARIABLE))
    }
}
