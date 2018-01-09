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
package sh.espencer.populus.examples

import sh.espencer.populus.{GeneticConfig, IterableGeneticAlgorithm}

/**
  * Finds a list of numbers that add up to 42
  */
class NumberGeneticAlgorithm(
  override val chromosomeSize: Int = 3,
  override val stickyGenes: Set[Int] = Set.empty
)
  extends IterableGeneticAlgorithm[Int] {

  // Set high mutation and crossover rates for testing
  val config = GeneticConfig(0.9, 0.7, 0.7, 1000)

  override protected val genePool: Array[Int] = {
    (0 to 100).toArray
  }

  override protected def fitness(chromosome: Iterable[Int]): Double = {
    val sum = chromosome.sum
    if (sum > 42) 0 else sum.toDouble / 42
  }

  override protected def stopCondition(pool: Pool, generation: Int): Boolean = {
    pool.head.sum == 42 || generation > 1000
  }

}
