/*
 * Copyright (C) 2017 Edd Spencer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sh.espencer.populus.stats

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.TimeUnit

/**
  * Trait of statistics object, can be extended as required
  */
trait GeneticStats {
  val min: Long
  val max: Long
  val mean: Long
  val count: Long
  val sum: Long
  val variance: Long
  val percentile25: Long
  val percentile50: Long
  val percentile75: Long

  private def convert(x: Long, timeUnit: TimeUnit): Long = {
    timeUnit.convert(x, TimeUnit.NANOSECONDS)
  }

  def min(timeUnit: TimeUnit): Long = convert(min, timeUnit)

  def max(timeUnit: TimeUnit): Long = convert(max, timeUnit)

  def mean(timeUnit: TimeUnit): Long = convert(mean, timeUnit)

  def sum(timeUnit: TimeUnit): Long = convert(sum, timeUnit)

  def variance(timeUnit: TimeUnit): Long = convert(variance, timeUnit)

  def percentile25(timeUnit: TimeUnit): Long = convert(percentile25, timeUnit)

  def percentile50(timeUnit: TimeUnit): Long = convert(percentile50, timeUnit)

  def percentile75(timeUnit: TimeUnit): Long = convert(percentile75, timeUnit)

  def toString(timeUnit: TimeUnit): String = {
    f"| min ${min(timeUnit)}%15d | max ${max(timeUnit)}%15d | mean ${mean(timeUnit)}%15d " +
      f"| count $count%15d | sum ${sum(timeUnit)}%15d " +
      f"| var ${variance(timeUnit)}%15d | per25 ${percentile25(timeUnit)}%15d " +
      f"| per50 ${percentile50(timeUnit)}%15d | per75 ${percentile75(timeUnit)}%15d |"
  }
}

/**
  * Standard statistics object
  */
case class SimpleGeneticStats(
  min: Long,
  max: Long,
  mean: Long,
  count: Long,
  sum: Long,
  variance: Long,
  percentile25: Long,
  percentile50: Long,
  percentile75: Long
) extends GeneticStats
