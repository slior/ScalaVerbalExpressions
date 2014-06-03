package com.github.verbalexpressions

case class VerbalExpression(prefix: String = "", expression: String = "", suffix: String = "", modifiers: Int = 0) {
  import java.util.regex.Pattern

  private[this] def sanitize(value: String) = if(value == null) value else Pattern.quote(value)

  def replace(source: String, value: String) = source.replaceAll(toString, value)

  def add(value: String) = copy(expression = expression + value)

  def andThen(value: String) = add(s"(${sanitize(value)})")

  def then = andThen _

  def find = andThen _

  def maybe(value: String) = add(s"(${sanitize(value)})?")

  def anything = add("(.*)")

  def anythingBut(value: String) = add(s"([^${sanitize(value)}]*)")

  def something = add("(.+)")

  def somethingBut(value: String) = add(s"([^${sanitize(value)}]+)")

  def tab = add("\\t")

  def word = add("\\w+")

  def anyOf(value: String) = add(s"[${sanitize(value)}]")

  def any = anyOf _

  def lineBreak = add("(\\n|(\\r\\n))")

  def br = lineBreak

  def startOfLine(enable: Boolean = true) = copy(prefix = if (enable) "^" else "")

  def endOfLine(enable: Boolean = true) = copy(suffix = if (enable) "$" else "")

  def or(value: String) = copy("(" + prefix, expression + ")|(" + value, ")" + suffix)

  def or(value: VerbalExpression): VerbalExpression = or(value.expression)

  def range(from: Any, to: Any) = add(s"[$from-$to]")

  def range(args: Array[Any]) = {
    val ranges = (0 to args.length - 1).filter( _ % 2 == 0).foldRight(List[String]()) { (from, res) =>
       args(from) + "-" + args(from+1) :: res
    }
    add(s"[${ranges.mkString}]")
  }

  private[this] val charModToInt = Map(
    'd' -> Pattern.UNIX_LINES,
    'i' -> Pattern.CASE_INSENSITIVE,
    'x' -> Pattern.COMMENTS,
    'm' -> Pattern.MULTILINE,
    's' -> Pattern.DOTALL,
    'u' -> Pattern.UNICODE_CASE,
    'U' -> Pattern.UNICODE_CHARACTER_CLASS
  )

  def modify(modifier: Char, add: Boolean) =  copy(modifiers = if (add) modifiers | charModToInt(modifier) else modifiers ^ charModToInt(modifier))

  def addModifier(modifier: Char) = modify(modifier, true)

  def removeModifier(modifier: Char) = modify(modifier, false)

  def withAnyCase(enable: Boolean = true) = modify('i', enable)

  def searchOneLine(enable: Boolean = true) = modify('m', enable)

  def stopAtFirst(enable: Boolean = false) = modify('g', enable)

  def repeatPrevious(n: Int) = ???

  def repeatPrevious(atleast: Int, atmost: Int) = ???

  def multiple(value: String) = add(s"${sanitize(value)}+")

  def beginCapture = add("(")

  def endCapture = add(")")

  def pattern = Pattern.compile(prefix + expression + suffix, modifiers)

  def test(toTest: String) = Pattern.matches(toString, toTest)

  override def toString = pattern.pattern()
}
