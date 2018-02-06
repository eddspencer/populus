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

class TestGeneticSolver extends WordSpec with Matchers {

  "GeneticSolver" should {

    "select fittest chromosomes" in {
      val c6 = Iterable(1, 2, 3)
      val c60 = Iterable(10, 20, 30)
      val c42 = Iterable(10, 20, 12)
      val c35 = Iterable(5, 10, 20)
      val pool = Seq(c6, c60, c42, c35)

      val selected = new NumberGeneticAlgorithm().select(pool)

      selected shouldEqual Seq(c42, c35, c6)
    }

    "evolve until stop condition is met" in {
      val ga = new NumberGeneticAlgorithm() {
        override def reProduction(pool: Pool): Pool = {
          // Increase all chromosomes by one
          pool.map(_.toList :+ 1)
        }
      }

      val (pool, generations) = ga.evolution(Seq(Iterable(1)))
      generations shouldEqual 41
      pool.head.sum shouldEqual 42
    }

    "re-produce" in {
      var crossoverCalled, mutateCalled, selectCalled = 0L
      val ga = new NumberGeneticAlgorithm() {
        override def select(pool: Pool): Pool = {
          selectCalled = System.currentTimeMillis
          pool
        }

        override def crossoverPool(pool: Pool): Pool = {
          crossoverCalled = System.currentTimeMillis
          pool
        }

        override def mutatePool(pool: Pool): Pool = {
          mutateCalled = System.currentTimeMillis
          pool
        }
      }

      ga.reProduction(0 to 4 map (Iterable(_)))
      val times = List(
        crossoverCalled,
        mutateCalled,
        selectCalled
      )
      times.count(_ == 0L) shouldEqual 0
      times.sorted shouldEqual List(
        selectCalled,
        crossoverCalled,
        mutateCalled
      )
    }
  }

}
