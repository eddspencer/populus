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

import sh.espencer.populus.stats.{GeneticStatsKeys, HasGeneticStats}

import scala.util.Random

/**
  * The genetic operators define the framework of the genetic algorithm, normally these will not
  * need to be updated, and you can alter the algorithm by just changing the genetic algorithm
  * functions and configuration parameters
  *
  * @tparam G type of gene
  * @tparam C type of chromosome
  * @author Edd Spencer
  */
trait GeneticOperations[G, C] {

  this: GeneticAlgorithm[G, C]
    with GeneProducer[G]
    with HasGeneticStats =>

  val config: HasGeneticConfig

  /**
    * Switches chromosomes around in the pool, expects the pool to be ordered by fitness from
    * the output of select. This will split the pool in half and then apply the chromosome
    * crossover function to each pair that matches indexes on each side of the split, creating
    * new child chromosomes and adding them to the pool
    *
    * @param pool source pool
    * @return new pool
    */
  protected[populus] def crossoverPool(
    pool: Pool
  ): Pool = time(GeneticStatsKeys.crossoverPool.toString) {
    val size = pool.size
    if (size > 1) {
      val mid = size >> 1
      val top = pool.slice(mid, size)
      val bottom = pool.take(mid)

      val crossed = for (
        parents <- bottom.zip(top)
        if config.crossoverRate > Random.nextFloat
      ) yield {
        val (c1, c2) = crossover(parents._1.data, parents._2.data)
        (createChromosome(c1), createChromosome(c2))
      }

      val offSprings = crossed.unzip
      pool ++ offSprings._1 ++ offSprings._2
    } else {
      pool
    }
  }

  /**
    * Crosses two chromosomes by randomly swapping genes pairs between the two using the given
    * crossover rate
    *
    * @param cData1 chromosome data 1
    * @param cData2 chromosome data 2
    * @return new pair of chromosomes
    */
  protected[populus] def crossover(
    cData1: C,
    cData2: C
  ): (C, C) = time(GeneticStatsKeys.crossover.toString) {
    val s1 = fromChromosome(cData1)
    val s2 = fromChromosome(cData2)
    val offSprings = s1.zip(s2).map {
      case (g1, g2) =>
        if (config.crossoverRate > Random.nextFloat) (g1, g2) else (g2, g1)
    }.unzip

    (toChromosome(offSprings._1), toChromosome(offSprings._2))
  }

  /**
    * Applies the mutation to the whole pool to create some mutated children
    *
    * @param pool pool to mutate
    * @return new mutated pool
    */
  protected[populus] def mutatePool(
    pool: Pool
  ): Pool = time(GeneticStatsKeys.mutatePool.toString) {
    val mutations = for (chromosome <- pool if config.mutationRate > Random.nextFloat) yield {
      createChromosome(mutate(chromosome.data))
    }
    mutations ++ pool
  }

  /**
    * Mutates random genes in the chromosome, selecting new genes from the gene pool using the
    * given mutation rate
    *
    * @param cData starting chromosome
    * @return new mutated chromosome
    */
  protected[populus] def mutate(
    cData: C
  ): C = time(GeneticStatsKeys.mutate.toString) {
    val mutated = fromChromosome(cData).map(gene =>
      if (config.mutationRate > Random.nextFloat) geneStream.head else gene
    )
    toChromosome(mutated)
  }

}
