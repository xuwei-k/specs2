package org.specs2
package reporter

object Fuga extends _root_.org.specs2.mutable.Specification {
  println("あああああああ Fuga")
  "this is my specification" should {
    "have one example" in {
      1 must_== 1
    }
  }
}
class Hoge extends _root_.org.specs2.mutable.Specification {
  println("いいいいいいい Hoge")
  "this is my specification" should {
    "have one example" in {
      1 must_== 1
    }
  }
}

class HtmlOptionsSpec extends Specification {
  def is = s2"""

 The html options can be passed to the html template for rendering

   boolean options must only be added to variables when they are true $boolean

"""

  def boolean = {
    (options.templateVariables must haveKey("nostats")) and
    (options.templateVariables must not haveKey("search"))
  }

  def options = HtmlOptions(
      outDir = HtmlOptions.outDir
    , baseDir = HtmlOptions.baseDir
    , template = HtmlOptions.outDir.toFilePath
    , variables = Map()
    , noStats = true
    , search = false
    , toc = false
    , tocEntryMaxSize    = 18
    , warnMissingSeeRefs = true
  )
}
