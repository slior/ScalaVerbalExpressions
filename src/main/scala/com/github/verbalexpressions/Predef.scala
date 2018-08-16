package com.github.verbalexpressions

/**
  * Predefined useful regular expressions
  */
object Predef
{

  import VerbalExpression._

  //^\d+$
  def positiveInteger = $.mustStartLine().digits().mustEndLine()

  //^-\d+$
  def negativeInteger = $.mustStartLine().andThen("-").digits().mustEndLine()

}
