import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;

public class Main {

  public static void main(String[] args) {

    HttpServer.create()
        .port(8888)
        .route(routes ->
            routes.post("/test/{param}", (request, response) ->
                response.sendString(request.receive()
                    .asString()
                    .map(s -> s + ' ' + request.param("param") + '!')
                    .log("http-server"))))
        .bindNow();

    HttpClient client = HttpClient.create().protocol(HttpProtocol.H2C, HttpProtocol.HTTP11)
        .port(8888);

    for (int i = 0; i < 3; i++) {
      client.get()
          .uri("/test/World")
          .responseContent()
          .aggregate()
          .asString()
          .log("http-client")
          .block();
    }
  }
}
