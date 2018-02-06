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

import sh.espencer.populus.stats.{GeneticStatsKeys, HasStats}

import scala.annotation.tailrec

/**
  * The solver of the genetic algorithm, this just processes each evolution one at a time until
  * the stop condition is met.
  *
  * @tparam Gene       type of gene
  * @tparam Chromosome type of chromosome
  * @author Edd Spencer
  */
trait GeneticSolver[Gene, Chromosome] extends HasStats {

  this: GeneticAlgorithm[Gene, Chromosome]
    with GeneticOperations[Gene, Chromosome]
    with GeneProducer[Gene] =>

  val config: HasGeneticConfig

  /**
    * Evolve the algorithm until the stop condition is met, the starting population is a pool of
    * random chromosomes of populationSize
    *
    * @return resulting population, ordered by score and the number of generations it took to get
    *         there
    */
  final def evolution(): (Pool, Int) = {
    evolution(randomPool())
  }

  /**
    * Recursively evolve the population, incrementing the generations each time until the stop
    * condition is met
    *
    * @param pool       starting pool of evolution
    * @param generation starting generation
    * @return resulting population nad generation
    */
  @tailrec protected[populus] final def evolution(pool: Pool, generation: Int = 1): (Pool, Int) = {
    val newGeneration = reProduction(pool)
    if (stopCondition(newGeneration, generation)) {
      (newGeneration, generation)
    } else {
      evolution(newGeneration, generation + 1)
    }
  }

  /**
    * Generates a random pool of chromosomes
    *
    * @return new pool of chromosomes
    */
  protected[populus] def randomPool(): Pool = time(GeneticStatsKeys.randomPool.toString) {
    for {_ <- 1 to config.populationSize} yield toChromosome(geneStream)
  }

  /**
    * Applies a reproduction cycle to the pool, which selects the fittest chromosomes, crossed
    * and mutates them
    *
    * @param pool pool
    * @return new pool of the next generation
    */
  protected[populus] def reProduction(pool: Pool): Pool = {
    if (pool.lengthCompare(3) < 0) {
      pool
    } else {
      val selection = select(pool)
      val crossed = crossoverPool(selection)
      mutatePool(crossed)
    }
  }

  /**
    * Unfitness function to sort population in descending order of fitness
    *
    * @param chromosome chromosome to score
    * @return
    */
  protected def unfitness(chromosome: Chromosome): Double = 1.0 - fitness(chromosome)

  /**
    * Select the fittest chromosomes from the pool, will only take cutOff percentage of the
    * total size on the pool.
    *
    * @param pool source pool
    * @return new pool of fittest chromosomes
    */
  protected[populus] def select(pool: Pool): Pool = time(GeneticStatsKeys.select.toString) {
    val n = Math.min(config.populationSize, percentageToIndex(pool.size, config.selectionCutOff))
    pool.sortBy(unfitness).take(n)
  }

  /**
    * Gets the index equal to the given percentage of the size, cannot be greater than size -1
    * or less than 0.
    *
    * @param size       size to calculate ratio from
    * @param percentage percentage to take
    * @return index
    */
  protected def percentageToIndex(size: Int, percentage: Double): Int = {
    val ratio = Math.max(0, percentage)
    val index = (size * ratio).floor.toInt
    Math.min(size - 1, index)
  }

}
