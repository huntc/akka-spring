package org.typesafe

import akka.actor._
import akka.pattern.ask
import akka.util.duration._
import akka.util.Timeout
import javax.inject.{Inject, Named}
import org.springframework.context.annotation.{Bean, AnnotationConfigApplicationContext, Configuration, Scope}
import org.springframework.context.annotation.aspectj.EnableSpringConfigured
import org.springframework.context.annotation.EnableLoadTimeWeaving

case object Tick
case object Get

/**
 * Simple POJO service to return an incremented number. @Named makes this component available for injection. Spring will
 * associate the bean with a singleton scope by default.
 */
@Named
class CountingService {
  def increment(count: Int) = count + 1
}

/**
 * Akka Actor available for injection and declared with a prototype scope. As stated for CountingService Spring will
 * associate a singleton scope by default. Singletons have no place in Akka when it comes to creating Actors (you can
 * have millions of Actors).
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
 *
 * `@EnableSpringConfigured` and `@EnableLoadTimeWeaving` annotations activates detection of
 * `@Configurable` beans.
 * http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/aop.html#aop-atconfigurable
 */
@Configuration
@EnableSpringConfigured
@EnableLoadTimeWeaving
class AppConfiguration {
  @Bean
  def actorSystem = ActorSystem("Akkaspring")
}

/**
 * The main class that establishes the Spring app context and then kicks off the application. Notice how the app
 * context is being asked for the ActorSystem and the Actor. Note however that it is the ActorSystem that manages the
 * lifecycle of the Actor as is normal for Akka. Spring's app context simply produces the Actor and wires it up with its
 * required dependencies (in our case just the CountingService).
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

  // wait for the result and print it, then shut down the services
  (counter ? Get) andThen {
    case count => println("Count is " + count)
  } onComplete { _ => system.shutdown() }
}
