package org.mockito.internal.progress

import scala.collection.JavaConverters._
import org.hamcrest.Matcher

/**
 * provide access to the locally stored matchers created by the `argThat` method when evaluating byname arguments
 */
object ThreadSafeMockingProgress2 extends ThreadSafeMockingProgress {
  def pullLocalizedMatchers = ThreadSafeMockingProgress.threadSafely().getArgumentMatcherStorage.pullLocalizedMatchers()

  def reportMatchers(matchers: java.util.List[Matcher[_]]) = {
    matchers.asScala.foreach(m => ThreadSafeMockingProgress.threadSafely().getArgumentMatcherStorage.reportMatcher(m))
  }
}


