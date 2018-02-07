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

/**
  * Trait of statistics object, can be extended as required
  */
trait GeneticStats {
  val min: Double
  val max: Double
  val mean: Double
  val count: Long
  val sum: Double
  val variance: Double
  val percentile25: Double
  val percentile50: Double
  val percentile75: Double

  /**
    * Convert to string with standard spacing so can easily compare values
    *
    * @return string representation of data
    */
  override def toString: String = {
    f"| min $min%15.2f | max $max%15.2f | mean $max%15.2f | count $count%15d | sum $sum%15.2f | " +
      f"var $variance%15.2f | per25 $percentile25%15.2f | per50 $percentile50%15.2f | " +
      f"per75 $percentile75%15.2f |"
  }
}

/**
  * Standard statistics object
  */
case class SimpleGeneticStats(
  min: Double,
  max: Double,
  mean: Double,
  count: Long,
  sum: Double,
  variance: Double,
  percentile25: Double,
  percentile50: Double,
  percentile75: Double
) extends GeneticStats
