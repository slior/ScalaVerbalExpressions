package com.github.verbalexpressions

case class VerbalExpression(prefix: String = "", expression: String = "", suffix: String = "", modifiers: Int = 0) {
  import java.util.regex.Pattern
  import java.util.regex.Pattern.quote

  def replace(source: String, value: String) = source.replaceAll(toString, value)

  def add(value: String) = copy(expression = expression + value)

  def andThen(value: String) = add(s"(${quote(value)})")
  def then = andThen _
  def find = andThen _

  def maybe(value: String) = add(s"(${quote(value)})?")

  def multiple(value: String) = add(s"${quote(value)}+")
  def many = multiple _

  def anything = add("(.*)")
  def anythingBut(value: String) = add(s"([^${quote(value)}]*)")

  def something = add("(.+)")
  def somethingBut(value: String) = add(s"([^${quote(value)}]+)")

  def whitespace(enable: Boolean = true) = add(if (enable) "\\s+" else "\\S+")
  def word(enable: Boolean = true) = add(if (enable) "\\w+" else "\\W+")
  def digits(enable: Boolean = true) = add(if (enable) "\\d+" else "\\D+")

  def anyOf(value: String) = add(s"[${quote(value)}]")
  def any = anyOf _

  def lineBreak = add("(\\n|(\\r\\n))")
  def startOfLine(enable: Boolean = true) = copy(prefix = if (enable) "^" else "")
  def endOfLine(enable: Boolean = true) = copy(suffix = if (enable) "$" else "")

  def or(value: String) = copy("(" + prefix, expression + ")|(" + value, ")" + suffix)
  def or(value: VerbalExpression): VerbalExpression = or(value.expression)

  def range(args: (Any, Any)*) = add(s"[${(args map {case (a, b) => s"$a-$b"}).mkString}]")

  private[this] val charModToInt = Map(
    'd' -> Pattern.UNIX_LINES,
    'i' -> Pattern.CASE_INSENSITIVE,
    'x' -> Pattern.COMMENTS,
    'm' -> Pattern.MULTILINE,
    's' -> Pattern.DOTALL,
    'u' -> Pattern.UNICODE_CASE,
    'U' -> Pattern.UNICODE_CHARACTER_CLASS
  )

  def addModifier(modifier: Char) = copy(modifiers = modifiers | charModToInt(modifier))
  def removeModifier(modifier: Char) = copy(modifiers = modifiers ^ charModToInt(modifier))

  def modify(modifier: Char, add: Boolean) = if (add) addModifier(modifier) else removeModifier(modifier)

  def withAnyCase(enable: Boolean = true) = modify('i', enable)
  def searchOneLine(enable: Boolean = true) = modify('m', enable)
  def stopAtFirst(enable: Boolean = false) = modify('g', enable)

  def repeat(n: Int) = add(s"{$n}")
  def repeat(atleast: Int, atmost: Int) = add(s"{$atleast,$atmost}")
  def repeatAtleast(n: Int) = add(s"{$n,}")

  def beginCapture = add("(")
  def endCapture = add(")")

  def reluctant = add("?")
  def possessive = add("+")

  lazy val compile = Pattern.compile(prefix + expression + suffix, modifiers)
  def test(toTest: String) = Pattern.matches(toString, toTest)

  override def toString = compile.pattern()
}
