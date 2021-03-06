package com.github.viyadb.spark.streaming.record

import java.sql.Timestamp
import java.util.GregorianCalendar

import com.github.viyadb.spark.Configs._
import com.github.viyadb.spark.UnitSpec
import com.github.viyadb.spark.batch.OutputSchema
import com.github.viyadb.spark.streaming.parser.{Record, RecordParser, TsvRecordParser}

class TsvRecordParserSpec extends UnitSpec {

  "TsvRecordParser" should "parse TSV input" in {
    val tableConf = TableConf(
      name = "foo",
      dimensions = Seq(
        DimensionConf(name = "app"),
        DimensionConf(name = "date", `type` = Some("time"), format = Some("%Y-%m-%d %H:%M:%S")),
        DimensionConf(name = "network"),
        DimensionConf(name = "city")
      ),
      metrics = Seq(
        MetricConf(name = "revenue", `type` = "double_sum"),
        MetricConf(name = "sessions", `type` = "long_sum"),
        MetricConf(name = "installs", `type` = "int_sum")
      )
    )

    val indexerConf = IndexerConf(
      deepStorePath = "",
      realTime = RealTimeConf(
        parseSpec = Some(ParseSpecConf(
          format = "tsv",
          columns = Some(Seq("app", "date", "network", "network_id", "city", "sessions", "installs", "revenue")),
          timeFormats = Some(Map("date" -> "%Y-%m-%d %H:%M:%S"))
        ))
      ),
      batch = BatchConf()
    )

    val jobConf = JobConf(
      indexer = indexerConf,
      tableConfigs = Seq(tableConf)
    )

    val recordParser = RecordParser.create(jobConf)
    assert(recordParser.getClass == classOf[TsvRecordParser])

    val tsvContent = Seq(
      "a.b.c\t2017-01-01 11:43:55\tfacebook\t123\tNew York\t30\t4\t0.1",
      "x.y.z\t2017-01-03 12:13:00\tgoogle\t321\tBoston\t50\t5\t11.1",
      "q.w.e\t2016-12-12 01:20:01\tfacebook\t123\tSan Francisco\t10\t6\t8.0"
    )

    val rows = tsvContent.map(tsv => recordParser.parseRecord("", tsv).get)
    assert(rows.size == 3)

    assert(rows(0) == new Record(Array("a.b.c", new Timestamp(
      new GregorianCalendar(2017, 0, 1, 11, 43, 55).getTimeInMillis), "facebook", "New York", 0.1, 30L, 4)))

    assert(rows(1) == new Record(Array("x.y.z", new Timestamp(
      new GregorianCalendar(2017, 0, 3, 12, 13, 0).getTimeInMillis), "google", "Boston", 11.1, 50L, 5)))

    assert(rows(2) == new Record(Array("q.w.e", new Timestamp(
      new GregorianCalendar(2016, 11, 12, 1, 20, 1).getTimeInMillis), "facebook", "San Francisco", 8.0, 10L, 6)))

    val outputFormat = new OutputSchema(tableConf)

    assert(Set(
      "a.b.c\t2017-01-01 11:43:55\tfacebook\tNew York\t0.1\t30\t4",
      "x.y.z\t2017-01-03 12:13:00\tgoogle\tBoston\t11.1\t50\t5",
      "q.w.e\t2016-12-12 01:20:01\tfacebook\tSan Francisco\t8.0\t10\t6"
    ) == rows.map(r => outputFormat.toTsvLine(r)).toSet)
  }
}