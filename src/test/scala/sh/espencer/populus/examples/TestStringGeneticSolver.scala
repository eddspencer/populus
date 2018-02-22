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

import org.scalatest.{Matchers, WordSpec}
import sh.espencer.populus.GeneticConfig

class TestStringGeneticSolver extends WordSpec with Matchers {

  "StringGeneticSolver" should {
    "solve correctly" in {
      val target = "Build this"
      val config = GeneticConfig(
        selectionCutOff = 0.8,
        crossoverRate = 0.15,
        mutationRate = 0.1,
        populationSize = 500
      )
      val ga = StringGeneticSolver(config, target)
      val (pool, generations) = ga.evolution()
      generations should be < 1000
      pool.head.data should be(target)
    }
  }
}
