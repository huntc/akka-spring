package org.typesafe

import akka.actor._
import akka.pattern.ask
import akka.util.duration._
import akka.util.Timeout

import javax.inject.{Inject, Named}
import org.springframework.context.annotation.{Bean, AnnotationConfigApplicationContext, Configuration, Scope}

case object Tick
case object Get

@Named
class CountingService {
  def increment = {count: Int =>
    count + 1
  }
}

@Named
@Scope("prototype")
class Counter @Inject() (countingService: CountingService) extends Actor {

  var count = 0

  def receive = {
    case Tick => countingService.increment(count)
    case Get  => sender ! count
  }
}

@Configuration
class AppConfiguration {
  @Bean
  def actorSystem = {
    ActorSystem("Akkaspring")
  }
}

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

  system.shutdown()
}
