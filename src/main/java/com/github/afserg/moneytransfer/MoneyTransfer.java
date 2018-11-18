package com.github.afserg.moneytransfer;

import com.github.afserg.entitylocker.EntityLocker;
import com.github.afserg.httphandler.JsonPostRequest;
import com.github.afserg.ioresponse.JsonMoneyTransferResponse;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MoneyTransfer {
    private static HttpServer startServer(final String ip, final int port, final String basePath,
                                  final HttpHandler httpHandler) throws IOException {
        final HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(ip, port), 0);
        httpServer.createContext(basePath, httpHandler);
        httpServer.start();
        return httpServer;
    }

    private static void initDatabase(final String connectionString,
                             final String scriptFileName) throws IOException, SQLException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(scriptFileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            String[] queries = sb.toString().split(";");

            try (Connection cnn = DriverManager.getConnection(connectionString);
                 Statement stm = cnn.createStatement()) {
                for (String query : queries) {
                    if (!query.trim().isEmpty()) {
                        stm.execute(query);
                    }
                }
            }

        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        final ResourceBundle rb = ResourceBundle.getBundle("MoneyTransfer");
        final String ip = rb.getString("ip");
        final int port = Integer.valueOf(rb.getString("port"));
        final String path = rb.getString("path");
        final String connectionString = rb.getString("jdbcConnectionString");
        final String dbScriptsFile = rb.getString("dbScriptsFile");
        final EntityLocker<String> locker = new EntityLocker<>();
        final HttpHandler httpHandler =
                new JsonPostRequest(
                        new JsonMoneyTransferResponse(locker, connectionString)
                );
        initDatabase(connectionString, dbScriptsFile);
        startServer(ip, port, path, httpHandler);
        System.out.println("Service available at http://" + ip + ":" + port + path);
    }
}
