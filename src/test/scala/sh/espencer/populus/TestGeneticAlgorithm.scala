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

import org.scalatest.{Matchers, WordSpec}
import sh.espencer.populus.examples.NumberGeneticAlgorithm

class TestGeneticAlgorithm extends WordSpec with Matchers {

  "IterableGeneticAlgorithm" should {
    "always include sticky genes" in {
      val ga = new NumberGeneticAlgorithm(stickyGenes = Iterable(42))
      val (pool, _) = ga.evolution()
      pool.foreach(chromosome => {
        chromosome should contain(42)
        chromosome.size shouldEqual 3
      })
    }
  }

}
