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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

import scala.collection.mutable

/**
  * Trait to add stats functions to object
  */
trait HasGeneticStats {

  /**
    * Map of stats, created after run is complete
    */
  lazy val stats: Map[String, GeneticStats] = statsDescriptive.mapValues(toStat).toMap

  /**
    * Map of descriptive statistics accumulated throughout run
    */
  private val statsDescriptive: mutable.Map[String, DescriptiveStatistics] = mutable.Map.empty

  /**
    * Whether profiling is enabled
    *
    * @return enabled
    */
  def statsEnabled: Boolean = false

  /**
    * At the end of processing this is called to convert the gathered stats
    *
    * @param statDesc gathered stats
    * @return geneti stats
    */
  def toStat(statDesc: DescriptiveStatistics): GeneticStats = {
    SimpleGeneticStats(
      statDesc.getMin,
      statDesc.getMax,
      statDesc.getMean,
      statDesc.getN,
      statDesc.getSum,
      statDesc.getVariance,
      statDesc.getPercentile(25),
      statDesc.getPercentile(50),
      statDesc.getPercentile(75)
    )
  }

  /**
    * Map allowing you to override particular stats
    */
  val statsEnabledByKey: mutable.Map[String, Boolean] = mutable.Map.empty

  /**
    * Times a function and applies the result to the given function parameter
    *
    * @param key   profile key
    * @param block function to time
    * @tparam R result type
    * @return result of block
    */
  def time[R](key: String)(block: => R): R = {
    if (statsEnabledByKey.getOrElse(key, statsEnabled)) {
      val start = System.nanoTime()
      val result = block // call-by-name
      val end = System.nanoTime()
      statsDescriptive.getOrElseUpdate(key, new DescriptiveStatistics()).addValue((end - start).toDouble)
      result
    } else {
      block
    }
  }

}
