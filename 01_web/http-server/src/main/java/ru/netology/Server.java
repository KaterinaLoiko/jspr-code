package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Server implements Runnable, RequestHandler {

  final List validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html",
      "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html",
      "/events.js");
  Socket socket;

  public Server(Socket socket) {
   this.socket = socket;
  }

  @Override
  public void run() {
    try {
      final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      final var out = new BufferedOutputStream(socket.getOutputStream());
      // read only request line for simplicity
      // must be in form GET /path HTTP/1.1
      final var requestLine = in.readLine();
      final var parts = requestLine.split(" ");

      if (parts.length != 3) {
        return;
      }

      final var path = parts[1];
      if (!validPaths.contains(path)) {
        out.write((
            "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n"
        ).getBytes());
        out.flush();
        return;
      }

      final var filePath = Path.of(".", "/01_web/http-server/public", path);
      final var mimeType = Files.probeContentType(filePath);

      // special case for classic
      if (path.equals("/classic.html")) {
        final var template = Files.readString(filePath);
        final var content = template.replace(
            "{time}",
            LocalDateTime.now().toString()
        ).getBytes();
        write(out, mimeType, content.length);
        out.write(content);
        out.flush();
        return;
      }
      final var length = Files.size(filePath);
      write(out, mimeType, length);
      Files.copy(filePath, out);
      out.flush();
    } catch (IOException ex){
      ex.printStackTrace();
    }
  }

  private void write(BufferedOutputStream out, String mimeType, long length) throws IOException {
    out.write((
        "HTTP/1.1 200 OK\r\n" +
            "Content-Type: " + mimeType + "\r\n" +
            "Content-Length: " + length + "\r\n" +
            "Connection: close\r\n" +
            "\r\n"
    ).getBytes());
  }

  @Override
  public void addHandler(String type, String path, Handler handler) {

  }
}