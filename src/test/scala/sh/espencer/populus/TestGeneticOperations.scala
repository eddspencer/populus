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
package sh.espencer.populus

import org.scalatest.{Matchers, WordSpec}
import sh.espencer.populus.examples.NumberGeneticAlgorithm

class TestGeneticOperations extends WordSpec with Matchers {

  "GeneticOperations" when {
    type Chromosome = Iterable[Int]

    "crossover population" should {

      "do nothing on small pool" in {
        val pool = Seq(Iterable(1, 3, 4))
        val crossed = new NumberGeneticAlgorithm().crossover(pool)
        crossed shouldEqual pool
      }

      "increase population by crossover rate" in {
        val ga = new NumberGeneticAlgorithm()
        val pool = ga.randomPool()
        val crossed = ga.crossover(pool)
        val growth = crossed.size - pool.size
        growth should be > 0
      }

      "create next generation using chromosome crossover" in {
        val nextGeneration = Iterable(42)
        val ga = new NumberGeneticAlgorithm() {
          // Mark all crossed chromosomes
          override def crossover(
            c1: Chromosome, c2: Chromosome
          ): (Chromosome, Chromosome) = (nextGeneration, nextGeneration)
        }
        val pool = ga.randomPool()
        val crossed = ga.crossover(pool)

        val growth = crossed.size - pool.size
        val nextGenSize = crossed.count(_.eq(nextGeneration))
        nextGenSize shouldEqual growth
      }

    }

    "crossover chromosome" should {
      "randomly mix genes" in {
        val c1 = 1 to 50
        val c2 = 51 to 100
        val crossed = new NumberGeneticAlgorithm(100).crossover(c1, c2)

        crossed._1 should not equal c1
        crossed._2 should not equal c2

        val allGenes = crossed._1 ++ crossed._2
        allGenes.toList.sorted shouldEqual (1 to 100).toList
      }

    }

    "mutating population" should {

      "increase population by mutation rate" in {
        val ga = new NumberGeneticAlgorithm()
        val pool = ga.randomPool()
        val mutated = ga.mutatePool(pool)

        val growth = mutated.size - pool.size
        growth should be > 0
      }

      "mutate using chromosome mutation" in {
        val nextGeneration = Iterable(42)
        val ga = new NumberGeneticAlgorithm() {
          // Mark all mutated chromosomes
          override def mutate(chromosome: Chromosome): Chromosome = nextGeneration
        }

        val pool = ga.randomPool()
        val mutated = ga.mutatePool(pool)

        val growth = mutated.size - pool.size
        val nextGenSize = mutated.count(_.eq(nextGeneration))
        nextGenSize shouldEqual growth
      }

    }

    "mutating chromosome" should {

      "randomly mutate genes" in {
        val chromosome = 1 to 100
        val mutated = new NumberGeneticAlgorithm(100).mutate(chromosome)
        mutated.size shouldEqual chromosome.size
        mutated should not equal chromosome
      }

    }

  }

}
