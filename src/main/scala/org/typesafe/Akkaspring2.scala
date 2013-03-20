package org.typesafe

import akka.actor._
import akka.pattern.ask
import akka.util.duration._
import akka.util.Timeout
import javax.inject.Inject
import org.springframework.context.annotation. AnnotationConfigApplicationContext
import org.springframework.beans.factory.annotation.Configurable

/**
 * Akka Actor constructed with `Props[Counter2]` or `Props(new Counter2)`
 * with injected resources. `@Configuarble` enables injection of spring beans
 * into the actor.
 * http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/aop.html#aop-atconfigurable
 */
@Configurable(preConstruction = true)
class Counter2 extends Actor {

  // the service that will be automatically injected
  @Inject
  var countingService: CountingService = _

  var count = 0

  def receive = {
    case Tick => count = countingService.increment(count)
    case Get  => sender ! count
  }
}


/**
 * The main class that establishes the Spring app context and then kicks off the application. Notice how the app
 * context is being asked for the ActorSystem and the Actor. Note however that it is the ActorSystem that manages the
 * lifecycle of the Actor as is normal for Akka. Spring's app context simply produces the Actor and wires it up with its
 * required dependencies (in our case just the CountingService).
 */
object Akkaspring2 extends App {
  val ctx = new AnnotationConfigApplicationContext
  ctx.scan("org.typesafe")
  ctx.refresh()

  val system = ctx.getBean(classOf[ActorSystem])

  val counter = system.actorOf(Props[Counter2])

  counter ! Tick
  counter ! Tick
  counter ! Tick

  implicit val timeout = Timeout(5 seconds)

  // wait for the result and print it, then shut down the services
  (counter ? Get) andThen {
    case count => println("Count is " + count)
  } onComplete { _ => system.shutdown() }
}
