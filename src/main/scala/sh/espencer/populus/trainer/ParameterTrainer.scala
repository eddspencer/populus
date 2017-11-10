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
package sh.espencer.populus.trainer

import com.typesafe.scalalogging.LazyLogging
import org.scalameter.picklers.noPickler._
import org.scalameter.{Bench, Gen, _}
import sh.espencer.populus.{GeneticAlgorithm, GeneticConfig, GeneticSolver}

import scala.collection.mutable
import scala.util.Random

/**
  * Definition of all config bounds used to generate a configuration, rates and cutoff are
  * described as percentages and population size is an exact number
  *
  * @author Edd Spencer
  */
case class ConfigBounds(
  selectionCutOff: Range,
  crossoverRate: Range,
  mutationRate: Range,
  populationSize: Range
)

/**
  * Parameter trainer for genetic algorithm, runs the algorithm with random configurations any
  * outputs the success and time of runs. This can be used to find the optimal parameters for a
  * given algorithm
  *
  * @author Edd Spencer
  */
trait ParameterTrainer[Gene, Chromosome] extends Bench.LocalTime with LazyLogging {

  /**
    * Create a new genetic solver with a given config
    *
    * @param config configuration
    * @return new solver instance
    */
  protected def createSolver(config: GeneticConfig): GeneticSolver[Gene, Chromosome]
    with GeneticAlgorithm[Gene, Chromosome]

  /**
    * Defines a successful evolution of the algorithm
    *
    * @param pool population
    * @return successful or not
    */
  protected def success(pool: Seq[Chromosome]): Boolean

  /**
    * Defined the bound for configuration parameters, a random number between these bounds will
    * be chosen. Defaults are given to have a relatively high selection rate but a relatively low
    * crossover and mutation rate.
    */
  protected val configBounds: ConfigBounds = ConfigBounds(70 to 100, 0 to 40, 0 to 40, 500 to 1000)

  /**
    * Define a list of randomly generated configs to use in algorithm
    */
  private val configs: List[GeneticConfig] = (0 to 10).map(_ => {
    def fromRange(r: Range) = r.start + (r.end - r.start) * Random.nextDouble

    def fromPercentageRange(r: Range) = fromRange(r) / 100.0

    GeneticConfig(
      selectionCutOff = fromPercentageRange(configBounds.selectionCutOff),
      crossoverRate = fromPercentageRange(configBounds.crossoverRate),
      mutationRate = fromPercentageRange(configBounds.mutationRate),
      populationSize = fromRange(configBounds.populationSize).toInt
    )
  }).toList

  private val genConfigs = Gen.enumeration("configs")(configs: _*)

  override def defaultConfig: Context = Context(
    Key.exec.maxWarmupRuns -> 5,
    Key.exec.benchRuns -> 5,
    Key.exec.independentSamples -> 1
  )

  val mapFails = mutable.Map(configs.map((_, 0)): _*)

  performance of "Genetic Algorithm" in {
    measure method "evolution" in {
      using(genConfigs) in {
        config => {
          val solver = createSolver(config)
          val (pool, generation) = solver.evolution()
          if (pool.isEmpty || !success(pool)) {
            mapFails(config) += 1
            logger.error(s"Using config: $config - no solution found count ${mapFails(config)}")
          } else {
            logger.info(s"Using config: $config - finished after $generation generations")
          }
        }
      }
    }
  }

}
