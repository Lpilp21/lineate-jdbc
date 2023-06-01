package com.example.lin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.example.lin.ConnectionPool.getConnectionPool;

@SpringBootApplication
public class LinApplication {

	public static void main(String[] args) throws SQLException, InterruptedException {
		String query = "select * from Orders";

		ExecutorService executor = Executors.newCachedThreadPool();
		for (int i = 0; i<100; i++){
			executor.submit(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("Before obtaining connection. Currently in use: " + getConnectionPool().getConnectionsInUse() + " connections.");
						Connection connection = getConnectionPool().giveMe();
						Thread.sleep(2000);
						getConnectionPool().release(connection);
						System.out.println("After releasing connection. Currently in use: " + getConnectionPool().getConnectionsInUse() + " connections.");
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}

			});


		}
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.DAYS);


	}
}
