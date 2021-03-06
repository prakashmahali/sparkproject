package com.github.viyadb.spark.streaming.parser

import java.sql.Timestamp

class JavaValueParser(nullNumericAsZero: Boolean = true,
                      nullStringAsEmpty: Boolean = true) extends Serializable {

  def parseByte(value: Object): Byte = {
    value match {
      case n: java.lang.Byte => n
      case n: java.lang.Number => n.byteValue()
      case s: java.lang.String => java.lang.Byte.parseByte(s)
      case null => if (nullNumericAsZero) 0 else throw new NullPointerException()
    }
  }

  def parseShort(value: Object): Short = {
    value match {
      case n: java.lang.Short => n
      case n: java.lang.Number => n.shortValue()
      case s: java.lang.String => java.lang.Short.parseShort(s)
      case null => if (nullNumericAsZero) 0 else throw new NullPointerException()
    }
  }

  def parseInt(value: Object): Integer = {
    value match {
      case n: java.lang.Integer => n
      case n: java.lang.Number => n.intValue()
      case s: java.lang.String => java.lang.Integer.parseInt(s)
      case null => if (nullNumericAsZero) 0 else throw new NullPointerException()
    }
  }

  def parseLong(value: Object): Long = {
    value match {
      case n: java.lang.Long => n
      case n: java.lang.Number => n.intValue()
      case s: java.lang.String => java.lang.Long.parseLong(s)
      case null => if (nullNumericAsZero) 0L else throw new NullPointerException()
    }
  }

  def parseFloat(value: Object): Float = {
    value match {
      case n: java.lang.Float => n
      case n: java.lang.Number => n.floatValue()
      case s: java.lang.String => java.lang.Float.parseFloat(s)
      case null => if (nullNumericAsZero) 0.0F else throw new NullPointerException()
    }
  }

  def parseDouble(value: Object): Double = {
    value match {
      case n: java.lang.Double => n
      case n: java.lang.Number => n.doubleValue()
      case s: java.lang.String => java.lang.Double.parseDouble(s)
      case null => if (nullNumericAsZero) 0.0 else throw new NullPointerException()
    }
  }

  def parseString(value: Object): String = {
    value match {
      case null => if (nullStringAsEmpty) "" else throw new NullPointerException()
      case s: java.lang.String => s
      case other => other.toString
    }
  }

  def parseTimestamp(value: Object): Option[Timestamp] = {
    value match {
      case n: java.lang.Long => Some(new Timestamp(n))
      case n: java.lang.Number => Some(new Timestamp(n.longValue()))
      case t: Timestamp => Some(t)
      case _ => None
    }
  }
}
