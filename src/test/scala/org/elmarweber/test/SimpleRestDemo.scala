package org.elmarweber.test
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._
import assertions._
import org.apache.commons.lang.RandomStringUtils

class SimpleRestDemo extends Simulation {
	val httpConf = httpConfig
			.baseURL("http://iic-cloud02:8080")
			.acceptHeader("application/json")


	val headers = Map(
			"Cache-Control" -> """no-cache""",
			"Content-Type" -> """application/json; charset=UTF-8""",
			"Pragma" -> """no-cache"""
	)

  val userDataFeeder = new Feeder[String] {
    def hasNext = true

    def next() = Map(
      "username" -> RandomStringUtils.randomAlphanumeric(10),
      "displayName" -> RandomStringUtils.randomAlphanumeric(20),
      "apiKey" -> RandomStringUtils.randomAlphanumeric(64))
  }

	val scn = scenario("Simple Create and Get")
    .during(200 seconds) {
      feed(userDataFeeder)
      .exec(http("create user")
            .post("/user/")
            .headers(headers)
            .body("""{
                    |    "username": "${username}",
                    |    "displayName": "${displayName}",
                    |    "apiKey": "${apiKey}"
                    |}""".stripMargin)
            .check(status.is(200))
        )
      .exec(http("get")
            .get("/user/${username}")
            .check(status.is(200))
      )
    }

	setUp(scn.users(100).ramp(100).protocolConfig(httpConf))
}
