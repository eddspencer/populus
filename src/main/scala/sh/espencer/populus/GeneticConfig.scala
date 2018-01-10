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
  * Configuration for genetic algorithm
  *
  * @author Edd Spencer
  */
trait HasGeneticConfig {

  /**
    * Cut off percentage of sorted population pool to select each evolution (between 0 and 1)
    */
  val selectionCutOff: Double

  /**
    * Crossover rate, larger number means more crossover (between 0 and 1)
    */
  val crossoverRate: Double

  /**
    * Mutation rate, large number means more mutation (between 0 and 1)
    */
  val mutationRate: Double

  /**
    * Size to make randomly generated population
    */
  val populationSize: Int
}

case class GeneticConfig(
  selectionCutOff: Double,
  crossoverRate: Double,
  mutationRate: Double,
  populationSize: Int
) extends HasGeneticConfig
