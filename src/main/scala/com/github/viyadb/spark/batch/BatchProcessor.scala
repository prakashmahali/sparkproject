package com.github.viyadb.spark.batch

import com.github.viyadb.spark.Configs.JobConf
import com.github.viyadb.spark.processing.{Aggregator, OutputFieldsSelector, ProcessorChain}

class BatchProcessor(config: JobConf) extends ProcessorChain(config, Seq(
  new Aggregator(config),
  new OutputFieldsSelector(config)
))
