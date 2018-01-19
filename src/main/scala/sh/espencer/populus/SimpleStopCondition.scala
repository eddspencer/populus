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

/**
  * Genetic config which contains a stop condition configuration for use with SimpleStopCondition
  */
trait HasGeneticConfigWithStop extends HasGeneticConfig {
  val requiredFitness: Double
  val generationCap: Int
}

case class GeneticConfigWithStop(
  selectionCutOff: Double,
  crossoverRate: Double,
  mutationRate: Double,
  populationSize: Int,
  requiredFitness: Double,
  generationCap: Int
) extends HasGeneticConfigWithStop

/**
  * This trait defines a stop condition of a desired fitness level and a generation hard cap
  *
  * @tparam Gene       type of gene
  * @tparam Chromosome type of chromosome
  */
trait SimpleStopCondition[Gene, Chromosome] {

  this: GeneticAlgorithm[Gene, Chromosome] =>

  val config: HasGeneticConfigWithStop

  /**
    * Stop when the foods match good enough and or we have done enough generations
    *
    * @param pool       current pool
    * @param generation current number of generations
    * @return
    */
  override protected def stopCondition(pool: Pool, generation: Int): Boolean = {
    fitness(pool.head) >= config.requiredFitness || generation == config.generationCap
  }

}
