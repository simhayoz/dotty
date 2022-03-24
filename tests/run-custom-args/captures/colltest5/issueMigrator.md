# Issue migrator

Problem uses a lot of "local" libraries: 
- os
- ujson
- requests

Best way to do this -> change to another example using requests only.
Run with:
` scalac -Ycc -classpath requests-scala-0-7-0_2.12-0.1.0-SNAPSHOT.jar tests/run-custom-args/captures/colltest5/CollectionStrawManCC5_1.scala tests/run-custom-args/captures/colltest5/OriginalIssueMigrator.scala`