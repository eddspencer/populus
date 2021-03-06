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
  * Wrapper class that caches the fitness of a chromosome
  *
  * @param data chromosome data
  * @param ga   genetic algorithm reference for calculating fitness
  * @tparam T type of chromosome data
  */
sealed class Chromosome[T](
  val data: T,
  ga: GeneticAlgorithm[_, T]
) {
  lazy val fitness: Double = ga.fitness(data)
}

/**
  * Genetic algorithm defines the functions that can be configured to alter the genetic evolution
  * of the process.
  *
  * @tparam G type of gene
  * @tparam C type of chromosome
  * @author Edd Spencer
  */
trait GeneticAlgorithm[G, C] {

  type Pool = Seq[Chromosome[C]]

  /**
    * Fitness function creates a score for a chromosome
    *
    * @param chromosome chromosome to score
    * @return the score
    */
  def fitness(chromosome: C): Double

  /**
    * Stop condition defines when to stop evolution
    *
    * @param pool       current pool of chromosomes
    * @param generation current generation
    * @return whether to stop
    */
  protected def stopCondition(pool: Pool, generation: Int): Boolean

  /**
    * Builds a chromosome data from a stream of genes
    *
    * @param genes gene sequence
    * @return chromosome
    */
  protected def toChromosome(genes: Stream[G]): C

  /**
    * Builds a chromosome object from a stream of genes
    *
    * @param genes gene sequence
    * @return chromosome
    */
  private[populus] def createChromosome(genes: Stream[G]): Chromosome[C] = {
    createChromosome(toChromosome(genes))
  }

  /**
    * Builds a chromosome object from chromosome data
    *
    * @param data data
    * @return chromosome
    */
  private[populus] def createChromosome(data: C): Chromosome[C] = {
    new Chromosome(data, this)
  }

  /**
    * Converts a chromosome to a stream of genes
    *
    * @param chromosome chromosome to convert
    * @return gene sequence
    */
  protected def fromChromosome(chromosome: C): Stream[G]

}

/**
  * Standard genetic algorithm that randomly selects genes from a pool
  *
  * @tparam G type of gene
  * @tparam C type of chromosome
  * @author Edd Spencer
  */
trait StandardGeneticAlgorithm[G, C] extends GeneticAlgorithm[G, C]
  with RandomGeneProducer[G]
  with GeneticSolver[G, C]
  with GeneticOperations[G, C]

/**
  * Genetic algorithm solver for chromosomes that are iterable collections of genes, uses the
  * random gene selector by default
  *
  * @tparam G type of gene
  * @author Edd Spencer
  */
trait IterableGeneticAlgorithm[G] extends GeneticAlgorithm[G, Iterable[G]]
  with RandomGeneProducer[G]
  with GeneticSolver[G, Iterable[G]]
  with GeneticOperations[G, Iterable[G]] {

  /**
    * Chromosome size as a method in case it is not static
    *
    * @return the chromosome size
    */
  protected def chromosomeSize(): Int

  /**
    * If some sticky genes are set then will always be included in every chromosome
    */
  val stickyGenes: Set[G] = Set.empty

  /**
    * Cache the size of sticky genes in case the iterable re-calculates
    */
  private lazy val numStickyGenes: Int = stickyGenes.size

  override def toChromosome(genes: Stream[G]): Iterable[G] = {
    val newGenes = genes
      .filter(!stickyGenes.contains(_))
      .take(Math.max(0, chromosomeSize() - numStickyGenes))
    val chromosome = newGenes ++ stickyGenes
    chromosome
  }

  override def fromChromosome(chromosome: Iterable[G]): Stream[G] = {
    chromosome.toStream
  }

}
