package vandebron.http

import akka.http.scaladsl.unmarshalling.Unmarshaller
import vandebron.domain.CustomerNumber

trait BaseRoute {
  implicit val customerNumberUnmarshaller: Unmarshaller[String, Set[CustomerNumber]] =
    Unmarshaller.strict(customerNumbersStr => customerNumbersStr.split(",").map(CustomerNumber.from).flatMap(_.toOption).toSet)
}
