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

import scala.util.Random

/**
  * Gene producer creates genes for creation of chromosomes
  *
  * @tparam Gene type of a gene
  * @author Edd Spencer
  */
trait GeneProducer[Gene] {

  /**
    * Stream of genes
    *
    * @return stream to produce genes
    */
  protected def geneStream: Stream[Gene]

}

/**
  * Randomly selects genes from a pool of all possible genes
  *
  * @tparam Gene type of a gene
  * @author Edd Spencer
  */
trait RandomGeneProducer[Gene] extends GeneProducer[Gene] {

  protected val genePool: Array[Gene]

  override protected[populus] def geneStream: Stream[Gene] = {
    genePool(Random.nextInt(genePool.length)) #:: geneStream
  }
}