package org.specs2
package matcher

import execute._
import specification.core.Env

import scala.concurrent._
import duration._
import runner._
import control.ExecuteActions._
import org.specs2.main.Arguments

class FutureMatchersSpec extends Specification with ResultMatchers with specification.Retries {

  lazy val env = Env(Arguments("threadsnb 4"))
  lazy val timeFactor = env.arguments.execute.timeFactor
  lazy val sleepTime = 50 * timeFactor.toLong
  implicit lazy val ee = env.executionEnv
  implicit lazy val ec = env.executionContext
  class MyTimeout extends TimeoutException

 def is = section("travis") ^ sequential ^ s2"""

 In this specification `Future` means `scala.concurrent.Future`

 Any `Matcher[T]` can be transformed into a `Matcher[Future[T]]` with the `await` method
 test ${ Future.apply(1) must be_>(0).await }

 with a retries number and timeout
 ${ Future { sleep(sleepTime); 1 } must be_>(0).await(retries = 3, timeout = 100.millis) }
 ${ (Future { sleep(sleepTime * 3); 1 } must be_>(0).await(retries = 4, timeout = 10.millis)) returns "Timeout" }

 with a retries number only
 ${ Future { sleep(sleepTime); 1 } must be_>(0).retryAwait(2) }

 with a timeout only
 ${ Future { sleep(sleepTime); 1 } must be_>(0).awaitFor(200.millis) }

 timeout applies only to `TimeoutException` itself, not subclasses
 ${ (Future { throw new TimeoutException } must throwA[TimeoutException].await) returns "Timeout" }
 ${ Future { throw new MyTimeout } must throwA[MyTimeout].await }

 A `Future` returning a `Matcher[T]` can be transformed into a `Result`
 ${ Future(1 === 1).await }

 A `throwA[T]` matcher can be used to match a failed future with the `await` method
 ${ Future.failed[Int](new RuntimeException) must throwA[RuntimeException].await }
 ${ { Future.failed[Int](new RuntimeException) must be_===(1).await } must throwA[RuntimeException] }

 A Future expression throwing an exception must not be matched
 ${ ({ throw new Exception("boom"); Future(1) } must throwAn[Exception].await) must throwAn[Exception] }

 In a mutable spec with a negated matcher $e1
 In a mutable spec with a scope $e2

 A Future should be retried the specified number of times in case of a timeout $e3
 A Future should not be called more than the expected number of times $e4
""" ^ step(env.shutdown)

  def e1 = {
    val thrown = new FutureMatchers with MustThrownExpectations {
      def result = Future(true) must beFalse.awaitFor(1 second)
    }
   thrown.result must throwA[FailureException]
  }

  def e2 = {
    val thrown = new mutable.Specification with FutureMatchers {
      "timeout ko" in new Scope {
        Future {
          try sleep(100) catch { case _: InterruptedException => () }
          1 must_== 2
        }.awaitFor(50.millis)
      }
    }

    ClassRunner.report(env)(thrown).runOption(env.specs2ExecutionEnv).get.failures === 1
  }

  def e3 = {
    val retries = 2
    var times = 0
    val duration = 50l
    def future = Future {
      times += 1
      if (retries != times)
        try Thread.sleep(duration * 4) catch { case _: Throwable => 0 }
      0
    }
    future must be_==(0).await(retries, duration.millis)
  }

  def e4 = {
    val retries = 0
    var times = 0
    def future = Future {
      times += 1
      0
    }
    future must be_==(0).retryAwait(retries)
    times must be_==(1)
  }

  def sleep(millis: Long): Unit = try {
    Thread.sleep(millis)
  } catch { case _: InterruptedException => () }
}
