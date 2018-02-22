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

import sh.espencer.populus.trainer.ParameterTrainer
import sh.espencer.populus.{Chromosome, GeneticConfig, StandardGeneticAlgorithm}

/**
  * Simple string genetic algorithm that mutates a string until it creates the target string
  *
  * @author Edd Spencer
  */
case class StringGeneticSolver(config: GeneticConfig, target: String)
  extends StandardGeneticAlgorithm[Char, String] {

  /**
    * Gene pool is array of all characters to use
    */
  override val genePool: Array[Char] = {
    Array(' ', '.', ',', '?', '!') ++: ('a' to 'z').toArray ++: ('A' to 'Z').toArray
  }

  /**
    * Define fitness by how many characters are a match, using exact index
    *
    * @param chromosome chromosome to score
    * @return the score
    */
  override def fitness(chromosome: String): Double = {
    val matching = target.toCharArray.zip(chromosome.toCharArray).count(p => p._1.equals(p._2))
    matching.toDouble / target.length
  }

  /**
    * To make a chromosome we create a String of the correct length from the random genes
    *
    * @param genes gene sequence
    * @return chromosome
    */
  override def toChromosome(genes: Stream[Char]): String = {
    genes.take(target.length).mkString
  }

  /**
    * To convert from a chromosome just convert it to its characters as a stream
    *
    * @param chromosome chromosome to convert
    * @return gene sequence
    */
  override def fromChromosome(chromosome: String): Stream[Char] = {
    chromosome.toStream
  }

  /**
    * Stop when we have matched the target string or the generation limit has been reached
    *
    * @param pool       current pool of chromosomes
    * @param generation current generation
    * @return whether to stop
    */
  override def stopCondition(pool: Pool, generation: Int): Boolean = {
    pool.head.data == target || generation >= 1000
  }
}

/**
  * Parameter trainer for string genetic algorithm
  *
  * @author Edd Spencer
  */
object StringParameterTrainer extends ParameterTrainer[Char, String] {

  private val target = "Training parameters"

  override protected def createSolver(config: GeneticConfig) = StringGeneticSolver(config, target)

  override protected def success(pool: Seq[Chromosome[String]]): Boolean = pool.head.data == target
}
