package vandebron

import com.typesafe.config.ConfigFactory

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object Config {
  val config = ConfigFactory.load()

  object Http {
    private val http = config.getConfig("queuing-service.http")

    val host: String = http.getString("host")
    val port: Int = http.getInt("port")
  }

  object Queue {
    private val queue = config.getConfig("queuing-service.queue")
    val cap: Int = queue.getInt("cap")
    val duration: FiniteDuration = FiniteDuration(queue.getInt("duration"), TimeUnit.SECONDS)
    val interval: FiniteDuration = FiniteDuration(queue.getInt("interval"), TimeUnit.SECONDS)
  }
}
