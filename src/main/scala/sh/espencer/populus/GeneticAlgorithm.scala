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

import scala.collection.Iterable

/**
  * Genetic algorithm defines the functions that can be configured to alter the genetic evolution
  * of the process.
  *
  * @tparam Gene       type of gene
  * @tparam Chromosome type of chromosome
  * @author Edd Spencer
  */
trait GeneticAlgorithm[Gene, Chromosome] {

  type Pool = Seq[Chromosome]

  /**
    * Fitness function creates a score for a chromosome
    *
    * @param chromosome chromosome to score
    * @return the score
    */
  protected def fitness(chromosome: Chromosome): Double

  /**
    * Stop condition defines when to stop evolution
    *
    * @param pool       current pool of chromosomes
    * @param generation current generation
    * @return whether to stop
    */
  protected def stopCondition(pool: Pool, generation: Int): Boolean

  /**
    * Builds a chromosome object from a stream of genes
    *
    * @param genes gene sequence
    * @return chromosome
    */
  protected def toChromosome(genes: Stream[Gene]): Chromosome

  /**
    * Converts a chromosome to a stream of genes
    *
    * @param chromosome chromosome to convert
    * @return gene sequence
    */
  protected def fromChromosome(chromosome: Chromosome): Stream[Gene]

}

/**
  * Standard genetic algorithm that randomly selects genes from a pool
  *
  * @tparam Gene       type of gene
  * @tparam Chromosome type of chromosome
  * @author Edd Spencer
  */
trait StandardGeneticAlgorithm[Gene, Chromosome] extends GeneticAlgorithm[Gene, Chromosome]
  with RandomGeneProducer[Gene]
  with GeneticSolver[Gene, Chromosome]
  with GeneticOperations[Gene, Chromosome]

/**
  * Genetic algorithm solver for chromosomes that are iterable collections of genes, uses the
  * random gene selector by default
  *
  * @tparam Gene type of gene
  * @author Edd Spencer
  */
trait IterableGeneticAlgorithm[Gene] extends GeneticAlgorithm[Gene, Iterable[Gene]]
  with RandomGeneProducer[Gene]
  with GeneticSolver[Gene, Iterable[Gene]]
  with GeneticOperations[Gene, Iterable[Gene]] {

  /**
    * Chromosome size as a method in case it is not static
    *
    * @return the chromosome size
    */
  protected def chromosomeSize(): Int

  /**
    * If some sticky genes are set then will always be included in every chromosome
    */
  val stickyGenes: Iterable[Gene] = Iterable.empty

  /**
    * Cache the size of sticky genes in case the iterable re-calculates
    */
  private lazy val numStickyGenes: Int = stickyGenes.size

  override protected def toChromosome(genes: Stream[Gene]): Iterable[Gene] = {
    stickyGenes ++ genes.take(chromosomeSize() - numStickyGenes)
  }

  override protected def fromChromosome(chromosome: Iterable[Gene]): Stream[Gene] = {
    chromosome.toStream
  }

}
