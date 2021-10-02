package vandebron

import com.typesafe.config.ConfigFactory

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
  }
}
