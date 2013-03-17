package org.typesafe

import akka.actor._
import akka.pattern.ask
import akka.util.duration._
import akka.util.Timeout

import javax.inject.{Inject, Named}
import org.springframework.context.annotation.{Bean, AnnotationConfigApplicationContext, Configuration, Scope}

case object Tick
case object Get

/**
 * Simple POJO service to return an incremented number. @Named makes this component available for injection. Spring will
 * associate the bean with a singleton scope by default.
 */
@Named
class CountingService {
  def increment = {count: Int =>
    count + 1
  }
}

/**
 * Akka Actor available for injection and declared with a prototype scope. As stated for CountingService Spring will
 * associate a singleton scope by default. Singletons have no place in Akka when it comes
 * to Akka.
 *
 * @param countingService the service that will be automatically injected. We will use this service to increment a
 *                        number.
 */
@Named
@Scope("prototype")
class Counter @Inject() (countingService: CountingService) extends Actor {

  var count = 0

  def receive = {
    case Tick => count = countingService.increment(count)
    case Get  => sender ! count
  }
}

/**
 * Spring specific configuration that is responsible for creating an ActorSystem and configuring it as necessary. The
 * actorSystem bean will be a singleton.
 */
@Configuration
class AppConfiguration {
  @Bean
  def actorSystem = ActorSystem("Akkaspring")
}

/**
 * The main class that establishes the Spring app context and then kicks off the application. Notice how the app
 * context is being asked for the ActorSystem and the Actor. Note however that it is the ActorSystem that manages the
 * lifecycle of the Actor as is normal for Akka. Spring's app context simple produces the Actor and wires it up with its
 * required beans (in our case just the CountingService).
 */
object Akkaspring extends App {
  val ctx = new AnnotationConfigApplicationContext
  ctx.scan("org.typesafe")
  ctx.refresh()

  val system = ctx.getBean(classOf[ActorSystem])

  val counter = system.actorOf(Props().withCreator(ctx.getBean(classOf[Counter])))

  counter ! Tick
  counter ! Tick
  counter ! Tick

  implicit val timeout = Timeout(5 seconds)

  (counter ? Get) onSuccess {
    case count => println("Count is " + count)
  }

  // You shouldn't normally require sleeping, but our example will execute asynchronously of course and we need to
  // ensure that there is enough time elapsed so that our actor can respond.
  Thread.sleep(500L)

  system.shutdown()
}
