package com.example.lin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ConnectionPool {
    private int inUse;
    private Stack<Connection> connections;
    private static ConnectionPool  pool;
    private final int MAX_CONNECTIONS = 10;
    private Semaphore semaphore = new Semaphore(MAX_CONNECTIONS, true);
    private String url = "jdbc:mysql://localhost:3306/sql_store";
    private String user = "root";
    private String password = "12345678";

    static {
        try {
            pool = new ConnectionPool();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static ConnectionPool getConnectionPool(){
        return pool;
    }

    private ConnectionPool() throws SQLException {
        connections = new Stack<>();
        inUse = 0;
        makeConnections();
    }
    private void makeConnections() throws SQLException {
        for(int i = 0; i<MAX_CONNECTIONS; i++){
            Connection connection = DriverManager.getConnection(url, user, password);
            connections.push(connection);
        }
    }
    public Connection giveMe() throws InterruptedException {
        synchronized (this){
            semaphore.acquire();
            inUse++;
            //System.out.println("currently in use are: "+inUse+" connections");
            return connections.pop();
        }
    }

    public void release(Connection connection){
        semaphore.release();
        connections.push(connection);
        inUse--;


    }
    public int getConnectionsInUse(){
        return inUse;
    }



}
