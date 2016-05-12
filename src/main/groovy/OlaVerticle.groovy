import io.vertx.ext.circuitbreaker.groovy.CircuitBreaker

def breaker = CircuitBreaker.create("ola", vertx,
        vertx.getOrCreateContext().config()["breaker"])

vertx.eventBus().consumer("ola", { message ->
  def name = message.body()
  message.reply(response(name, [:]))
});

vertx.eventBus().consumer("ola/chain", { message ->
  def name = message.body();

  breaker.executeWithFallback(
          { future ->
            vertx.eventBus().send("hola/chain", name, {
              ar ->
                if (ar.failed()) future.fail("failure while invoking hola")
                else {
                  message.reply(response(name, ar.result().body()))
                  println("sending trace");
                  future.complete();
                }
            })
          },
          { v ->
            message.reply(response(name, ["hola": "failed!"]))
          }
  )
})

vertx.eventBus().consumer("ola/health", { message -> message.reply("I'm ok") });

Map response(String name, Map resp) {
  resp["ola"] = "Ola" + " " + name;
  return resp;
}