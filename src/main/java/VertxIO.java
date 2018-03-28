import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import pojo.Movie;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Because Vert.x was designed for asynchronous communications it can deal with more concurrent network connections with
 * less threads than synchronous APIs such as Java servlets or java.net socket classes.
 *
 * Many networking libraries and frameworks rely on a simple threading strategy: each network client is being assigned a
 * thread upon connection, and this thread deals with the client until it disconnects. This is the case with Servlet or
 * networking code written with the java.io and java.net packages.
 *
 * The unit of deployment in Vert.x is called a Verticle. A verticle processes incoming events over an event-loop,
 * where events can be anything like receiving network buffers, timing events, or messages sent by other verticles.
 *
 * Each event shall be processed in a reasonable amount of time to not block the event loop. This means that thread
 * blocking operations shall not be performed while executed on the event loop
 *
 * Vert.x offers mechanisms to deal with blocking operations outside of the event loop. In any case Vert.x emits warnings
 * in logs when the event loop has been processing an event for too long
 *
 * Every event loop is attached to a thread. By default Vert.x attaches 2 event loops per CPU core thread.
 * The direct consequence is that a regular verticle always processes events on the same thread
 *
 * Incoming network data are being received from accepting threads then passed as events to the corresponding verticles.
 * When a verticle opens a network server and is deployed more than once, then the events are being distributed to the
 * verticle instances in a round-robin fashion which is very useful for maximizing CPU usage with lots of concurrent
 * networked requests.
 *
 * The Vert.x event bus is the main tool for different verticles to communicate through asynchronous message passing.
 *
 * The event bus supports the following communication patterns:
 *      -- Point-to-point messaging
 *      -- request-response messaging
 *      -- publish / subscribe for broadcasting messages.
 */
public class VertxIO  extends AbstractVerticle{
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);
        // A router can contain many routes.
        router.post("/home/post").handler(this::homePostHandler); //Specify a request handler for the route.only one handler for a route or override.
        router.get("/home/get").handler(this::homeGetHandler);

        vertx.createHttpServer().requestHandler(router::accept) // accept: provide a HttpServerRequest to a router.
                .listen(8080,ar -> {
                    if(ar.succeeded()) {
                        System.out.print("a handler that will be called when the server is actually listening (or has failed)."); // called once when started.
                    }
                });
    }

    private void homePostHandler(RoutingContext context) {
        HttpServerRequest req = context.request();
        // Convenience method for receiving the entire request body in one piece.
        // This handler will be called after all the body has been received
        req.bodyHandler(bodyBuffer -> {
            final JsonObject json  = bodyBuffer.toJsonObject();
            String name = json.getString("name");
            Integer age = json.getInteger("age");

            System.out.println("name is " + name + " and age is " + age); // success
        });



        HttpServerResponse response = context.response();
        MultiMap formData = req.formAttributes(); // not from data.
        System.out.println("here are the form data");
        for(Map.Entry<String,String> s :  formData) {
            System.out.println(s.getKey() + "===" + s.getValue()); // headers={"User-agent":"my-vertx/1.0"}
        }
        System.out.println("request path is " + req.path()); // http://localhost:8080/home/index => /home/index
        System.out.println("param is " + req.query()); // age=20%25name=will&dopa=yes


        System.out.println("here are the header details");
        MultiMap headers = req.headers();
        for(Map.Entry<String,String> s :  headers) {
            System.out.println(s.getKey() + "===" + s.getValue()); // headers={"User-agent":"my-vertx/1.0"}
        }


        System.out.println("here are the paras details");
        MultiMap params = req.params();
        for(Map.Entry<String,String> s :  params) {
            System.out.println(s.getKey() + "===" + s.getValue()); // params={"dopa":"yes"}
        }

        @Nullable JsonObject bodyAsJson = context.getBodyAsJson(); // get json post data

        context.put("title","wiki home"); // used in front page.not interested!

        List<Movie> jsonResponse = new ArrayList<>();

        Movie movie = new Movie();
        movie.setTitle("Titanic");
        movie.setDirector("James Cameron");
        movie.setBoxOffice(new BigDecimal(657567567));

        jsonResponse.add(movie);
        jsonResponse.add(new Movie("Transformer","Micheal Bay",new BigDecimal(45657)));


        response // each HttpServerRequest is associated with a HttpServerResponse instance.
                .putHeader("Content-type","application/json;charset=UTF-8") // produce json
                .putHeader("Access-Control-Allow-Origin","*") // cross origin
                .setChunked(true) // "Transfer-Encoding": "chunked"
                .end(Json.encodePrettily(jsonResponse)); // a single pure pojo is return as json object,list is return as a list of json object
    }
    private void homeGetHandler(RoutingContext context) {
        HttpServerRequest req = context.request();
        HttpServerResponse response = context.response();


        @Nullable String name = req.getParam("name");
        @Nullable String age = req.getParam("age");
        System.out.println("name parameter is " + name + " age is " + age ); // ?name=will&age=23

        response // each HttpServerRequest is associated with a HttpServerResponse instance.
                .putHeader("Content-type","text/html")
                .putHeader("Access-Control-Allow-Origin","*")
                .setChunked(true) // "Transfer-Encoding": "chunked"
                .write("content body string") // write string in response body
                .end("Hello from Vert.x"); // writes a String in UTF-8 encoding before ending the response.(appended after the written content in this case)

    }

    public static void main(String[] args) {
        Launcher.executeCommand("run",VertxIO.class.getName());
    }
}
