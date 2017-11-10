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

class TestGeneProducer extends WordSpec with Matchers {

  "RandomGeneProducer" should {
    "randomly produce genes from pool" in {
      val producer = new RandomGeneProducer[Int] {
        override val genePool: Array[Int] = (0 to 100).toArray
      }
      producer.geneStream.take(10000).filter(i => i > 100 || i < 0) should be(Seq.empty)
    }
  }
}
