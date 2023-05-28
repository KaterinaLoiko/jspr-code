package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

  public static void main(String[] args) throws IOException {

    final ExecutorService threadPool = Executors.newFixedThreadPool(64);
    ServerSocket serverSocket = new ServerSocket(9999);
    try {
      while (true) {
        final var socket = serverSocket.accept();
        threadPool.execute(new Server(socket));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
