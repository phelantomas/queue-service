lazy val akkaHttpVersion = "10.2.6"
lazy val akkaVersion    = "2.6.16"
lazy val circeVersion = "0.14.1"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "vandebron",
      scalaVersion    := "2.13.4"
    )),
    name := "queuing-service",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "ch.qos.logback"    % "logback-classic"           % "1.2.3",
      "eu.timepit"        %% "refined"                  % "0.9.27",

      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"                % "3.1.4"         % Test,
      "org.scalatestplus" %% "scalacheck-1-14"          % "3.2.2.0"       % Test,
      "org.scalatestplus" %% "mockito-3-12"             % "3.2.10.0"      % Test
    )
  )
