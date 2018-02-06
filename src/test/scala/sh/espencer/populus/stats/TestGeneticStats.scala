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
import org.scalatest.{Matchers, WordSpec}
import sh.espencer.populus.examples.NumberGeneticAlgorithm

import scala.collection.mutable

class TestGeneticStats extends WordSpec with Matchers {

  "HasStats" should {
    "time block" in {
      val hasStats = new HasStats {
        override def statsEnabled: Boolean = true
      }
      hasStats.time("test")(1 + 11)
      hasStats.stats("test").getN shouldEqual 1
    }

    "disabled by default" in {
      val hasStats = new HasStats {}
      hasStats.time("test")(1 + 11)
      hasStats.stats.size shouldEqual 0
    }

    "allow key specific overrides" in {
      val hasStats = new HasStats {}
      hasStats.statsEnabledByKey += ("test" -> true)
      hasStats.time("test")(1 + 11)
      hasStats.stats("test").getN shouldEqual 1
    }
  }

  "GeneticAlgorithm Stats" should {
    def run(): (mutable.Map[String, DescriptiveStatistics], Int) = {
      val ga = new NumberGeneticAlgorithm() {
        override def statsEnabled: Boolean = true
      }
      val (_, generations) = ga.evolution()
      (ga.stats, generations)
    }

    "profile can be extended easily" in {
      val ga = new NumberGeneticAlgorithm() {
        override def statsEnabled: Boolean = true

        override def fitness(chromosome: Iterable[Int]): Double = time("fitness") {
          super.fitness(chromosome)
        }
      }

      val (_, generations) = ga.evolution()
      ga.stats("fitness").getN should be >= 0L
    }

    "profile selects" in {
      val (profile, generations) = run()
      profile(GeneticStatsKeys.select.toString).getN shouldEqual generations
    }

    "profile crossovers" in {
      val (profile, generations) = run()
      profile(GeneticStatsKeys.crossover.toString).getN should be > generations.toLong
      profile(GeneticStatsKeys.crossoverPool.toString).getN shouldEqual generations
    }

    "profile mutations" in {
      val (profile, generations) = run()
      profile(GeneticStatsKeys.mutate.toString).getN should be > generations.toLong
      profile(GeneticStatsKeys.mutatePool.toString).getN shouldEqual generations
    }

    "profile randomPool" in {
      val (profile, generations) = run()
      profile(GeneticStatsKeys.randomPool.toString).getN shouldEqual 1
    }
  }
}
