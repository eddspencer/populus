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
name := "populus"
organization := "sh.espencer"
version := "0.3.9"

scalaVersion := "2.11.11"

resolvers += Resolver.jcenterRepo
resolvers += "Sonatype OSS Snapshots" at
  "https://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
  // Logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  // Testing
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,

  // Benchmarks
  "com.storm-enroute" %% "scalameter" % "0.8.2"
)

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")
parallelExecution in Test := false

// Define build task to do scalastyle, compile and tests
val buildAll = taskKey[Unit]("Main build task")
buildAll := Def.sequential(
  test.in(Test),
  scalastyle.in(Compile).toTask(""),
  scalastyle.in(Test).toTask("")
).value
