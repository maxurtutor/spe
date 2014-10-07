package org.maxur

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class MailSendSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://localhost:9090")
		.inferHtmlResources()
		.acceptHeader("""*/*""")
		.acceptEncodingHeader("""gzip,deflate""")
		.contentTypeHeader("""application/x-www-form-urlencoded; charset=windows-1251""")
		.userAgentHeader("""Apache-HttpClient/4.3.2 (java 1.5)""")

	val headers_0 = Map("""Cache-Control""" -> """no-cache""")

    val uri1 = """localhost"""

	val scn = scenario("RecordedSimulation")
             .repeat(100) {
		 exec(http("request_0")
			.post("""/service/mail""")
			.headers(headers_0)
			.formParam("""message""", """Hello"""))
			.pause(100 milliseconds)
	      }

	setUp(scn.inject(atOnceUsers(100))).protocols(httpProtocol)
}
