package vandebron

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import vandebron.domain.queries.{OrderQuery, ProductQuery}
import vandebron.http.{OrderRoute, ProductRoute, QueryRoute}
import vandebron.repository.{RandomlyGeneratedOrderRepository, RandomlyGeneratedProductRepository}
import vandebron.service.{OrderApiService, ProductApiService, QueueServiceActor}

import scala.util.{Failure, Success}

object boot {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    val futureBinding = Http().newServerAt(Config.Http.host, Config.Http.port).bind(routes)
    futureBinding.onComplete {
      case Success(_) =>
        system.log.info("Server online at http://{}:{}/", Config.Http.host, Config.Http.port)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val orderStatusRepository = new RandomlyGeneratedOrderRepository
      val productRepository = new RandomlyGeneratedProductRepository

      val orderStatusQuery = new OrderQuery(orderStatusRepository)
      val productQuery = new ProductQuery(productRepository)

      val orderRoute = new OrderRoute(orderStatusQuery)
      val productRoute = new ProductRoute(productQuery)

      val orderApiService = new OrderApiService()(context.system)
      val productApiService = new ProductApiService()(context.system)
      val queueActor = context.spawn(new QueueServiceActor(orderApiService, productApiService)(context.executionContext).apply(), "QueueActor")
      context.watch(queueActor)

      val queryRoutes = new QueryRoute(queueActor)(context.system)

      val routes = Directives.concat(orderRoute.route, productRoute.route, queryRoutes.route)
      startHttpServer(routes)(context.system)

      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "AkkaHttpServer")
  }
}
