scalaVersion := "2.13.1"

name := "contact-book"
organization := "co.com.addi"
version := "1.0"

libraryDependencies += "org.typelevel"              %% "cats-core"              % "2.0.0"
libraryDependencies += "com.github.tomakehurst"      % "wiremock"               % "1.58"
libraryDependencies += "com.typesafe.play"          %% "play-json"              % "2.8.1"
libraryDependencies += "com.typesafe.play"          %% "play-ahc-ws-standalone" % "2.1.2"
libraryDependencies += "io.monix"                   %% "monix"                  % "3.1.0"
libraryDependencies += "com.softwaremill.quicklens" %% "quicklens"              % "1.4.12"
libraryDependencies += "org.slf4j"                   % "slf4j-api"              % "1.7.5"
libraryDependencies += "org.slf4j"                   % "slf4j-simple"           % "1.7.5"

libraryDependencies += "org.specs2"                 %% "specs2-mock"            % "4.8.3"    % Test
libraryDependencies += "org.scalatest"              %% "scalatest"              % "3.2.0-M2" % Test


parallelExecution in Test := false
