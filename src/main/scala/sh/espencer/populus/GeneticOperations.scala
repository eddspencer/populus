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

import scala.util.Random

/**
  * The genetic operators define the framework of the genetic algorithm, normally these will not
  * need to be updated, and you can alter the algorithm by just changing the genetic algorithm
  * functions and configuration parameters
  *
  * @tparam Gene       type of gene
  * @tparam Chromosome type of chromosome
  * @author Edd Spencer
  */
trait GeneticOperations[Gene, Chromosome] {

  this: GeneticAlgorithm[Gene, Chromosome] with GeneProducer[Gene] =>

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
  protected[populus] def crossover(pool: Pool): Pool = {
    val size = pool.size
    if (size > 1) {
      val mid = size >> 1
      val top = pool.slice(mid, size)
      val bottom = pool.take(mid)

      val crossed = for (
        parents <- bottom.zip(top)
        if config.crossoverRate > Random.nextFloat
      ) yield {
        crossover(parents._1, parents._2)
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
    * @param c1 chromosome 1
    * @param c2 chromosome 2
    * @return new pair of chromosomes
    */
  protected[populus] def crossover(c1: Chromosome, c2: Chromosome): (Chromosome, Chromosome) = {
    val s1 = fromChromosome(c1)
    val s2 = fromChromosome(c2)
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
  protected[populus] def mutatePool(pool: Pool): Pool = {
    val mutations = for (chromosome <- pool if config.mutationRate > Random.nextFloat) yield {
      mutate(chromosome)
    }
    mutations ++ pool
  }

  /**
    * Mutates random genes in the chromosome, selecting new genes from the gene pool using the
    * given mutation rate
    *
    * @param chromosome starting chromosome
    * @return new mutated chromosome
    */
  protected[populus] def mutate(chromosome: Chromosome): Chromosome = {
    val mutated = fromChromosome(chromosome).map(gene =>
      if (config.mutationRate > Random.nextFloat) geneStream.head else gene
    )
    toChromosome(mutated)
  }

}
