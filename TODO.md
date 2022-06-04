I've decided the perfect is too lofty a goal for the first beta release. My apologies!

# Documentation

* Convert TODO.md (this file) into a standard RoadMap
* Finish/cleanup project docs
  * Add Spring/Guice integrations
* More complete scaladocs
* Finish module docs
* Ensure all tests use standardize description convention: describe(class.method(parameters))

# Refactor

* St4NodeProcessor.template needs to be more readable
* Better debug and trace logging
* Better exception logging (especially where it's currently not logged at all in Try objects)

# Test

* 100% test coverage (mostly traits at this point)

# Scripts

* Create sbt build scripts
* Create run scripts (Windows/Linux/Mac)

# Features

* Flagship Spring implementation
* XML mixed content (eg: &lt;li value="3"&gt;something&lt;/li&gt;&lt;li&gt;something else&lt;/li&gt;)
* Integrate antlr4 into sbt build (see engines/impl/src/main/antlr4)
* Create support for Natural Language Processing into an INode
* Create support for Apache Velocity templates
* Create st4 code generation abstractions
* Implement st4 Scala code generation abstractions
* Implement st4 Java code generation abstractions
* Implement st4 C# code generation abstractions
* Generate sgg source code using a bootstrap sgg
