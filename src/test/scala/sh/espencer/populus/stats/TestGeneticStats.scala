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

import org.scalatest.{Matchers, WordSpec}
import sh.espencer.populus.examples.NumberGeneticAlgorithm

class TestGeneticStats extends WordSpec with Matchers {

  "HasStats" should {
    "time block" in {
      val hasStats = new HasGeneticStats {
        override def statsEnabled: Boolean = true
      }
      hasStats.time("test")(1 + 11)
      hasStats.stats("test").count shouldEqual 1
    }

    "disabled by default" in {
      val hasStats = new HasGeneticStats {}
      hasStats.time("test")(1 + 11)
      hasStats.stats.size shouldEqual 0
    }

    "allow key specific overrides" in {
      val hasStats = new HasGeneticStats {}
      hasStats.statsEnabledByKey += ("test" -> true)
      hasStats.time("test")(1 + 11)
      hasStats.stats("test").count shouldEqual 1
    }
  }

  "GeneticStats" should {
    "Format stats correctly" in {
      SimpleGeneticStats(1000000, 2000000, 3000000, 4, 5000000, 6000000, 7000000, 8000000, 9000000)
        .toString(TimeUnit.MILLISECONDS) shouldEqual
        "| min               1 | max               2 | mean               3 | " +
          "count               4 | sum               5 | var               6 | per25            " +
          "   7 | per50               8 | per75               9 |"

    }
  }

  "GeneticAlgorithm Stats" should {
    def run(): (Map[String, GeneticStats], Int) = {
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
      ga.stats("fitness").count should be >= 0L
    }

    "profile selects" in {
      val (profile, generations) = run()
      profile(GeneticStatsKeys.select.toString).count shouldEqual generations
    }

    "profile crossovers" in {
      val (profile, generations) = run()
      profile(GeneticStatsKeys.crossover.toString).count should be > generations.toLong
      profile(GeneticStatsKeys.crossoverPool.toString).count shouldEqual generations
    }

    "profile mutations" in {
      val (profile, generations) = run()
      profile(GeneticStatsKeys.mutate.toString).count should be > generations.toLong
      profile(GeneticStatsKeys.mutatePool.toString).count shouldEqual generations
    }

    "profile randomPool" in {
      val (profile, generations) = run()
      profile(GeneticStatsKeys.randomPool.toString).count shouldEqual 1
    }
  }
}
