# Populus

A genetic algorithm solver written in Scala. It is designed to be simple to use and modular, any
part of the algorithm can be overwritten with a new implementation of your choice. You can use the
algorithm as it is by simply implementing the following:

- genePool: Array of all possible genes
- fitness: Function that scores a chromosome, double between 0 and 1.0 (higher is fitter)
- stopCondition: Function that defines when to stop evolving the algorithm
- toChromosome: Converts genes to a chromosome
- fromChromosome: Converts chromosome to genes

## Example
There are some simple examples in the code and tests of how you can implement the genetic algorithm
solver. To create an algorithm that will match a simple string all you need is:

```
case class StringGeneticSolver(config: GeneticConfig, target: String)
  extends StandardGeneticAlgorithm[Char, String] {

  /**
    * Gene pool is array of all characters to use
    */
  override protected val genePool: Array[Char] = {
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
  override protected def toChromosome(genes: Stream[Char]): String = {
    genes.take(target.length).mkString
  }

  /**
    * To convert from a chromosome just convert it to its characters as a stream
    *
    * @param chromosome chromosome to convert
    * @return gene sequence
    */
  override protected def fromChromosome(chromosome: String): Stream[Char] = {
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
    pool.head == target || generation >= 1000
  }
}
```

This can then be used with some config, defining the evolutionary parameters of the algorithm to
match a string:

```
val target = "How many monkeys does it take to produce Shakespeare?"
val config = GeneticConfig(
  selectionCutOff = 0.8,
  crossoverRate = 0.15,
  mutationRate = 0.1,
  populationSize = 500
)
val ga = StringGeneticSolver(config, target)
val (pool, generations) = ga.evolution()
```

The resulting pool is all the chromosomes of the last generation (potention matches to tartget
string) ordered by their fitness score descending. So you should find that `pool.head == target`.