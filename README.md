# Welcome to Simple Software Generator

The vision for this software is to both allow non-developers to create software without learning to code and reduce the amount of time it takes software developers to write reliable software.

## Navigation

If your goal is to customize the app by creating a plugin, see [customizer documentation](docs/developer/customizer.md).

If your goal is to use the app as is just creating an input file, see [user documentation](engine/flagship/docs/user.md).

If your goal is to modify the source code for the SSG project itself, see [maintainer documentation](docs/developer/README.md) and [flagship](engine/flagship/README.md).

## How does it work?

At the time of this writing (2022-05-14), the only application is called the flagship app. For now, it's the only planned application, but it was built with the idea in mind that others may follow. Below is a description of flagship.

Let's start with a glossary you can refer back to.
* Antlr grammar - Antlr is a ubiquitous tool that takes g4 grammar files and writes Java code that can parse that grammar into tokens. See [https://www.antlr.org/](https://www.antlr.org/).
* Natural Language File - text file (*.txt) with spoken language (e.g. English) which must match the Antlr grammar.
* Program Model - usually in a human-readable text format such as JSON, YAML, or XML.
* Templates - template files in whatever templating technology is supported in the Instructions File. Version 0.1.0 only supports [StringTemplate4](https://github.com/antlr/stringtemplate4/blob/master/doc/index.md), but the roadmap contains plans for [Apach Velocity](https://velocity.apache.org/).
* Instructions File - a JSON, YAML, or XML file that defines how and what to bind the Program Model with Templates.

1. Convert Natural Language to a Program Model (optional)
Application Inputs: Natural Language File
Static Inputs: Antlr Grammar
Outputs: Program Model
Software: Must be aware of the Antlr Grammar being used to know how to convert tokens into a program model 

2. Convert a Program Model to Code
Application Inputs: Program Model (must match any structure expected by instructions)
Static Inputs: Instructions File, Templates (any listed in instructions must be available at runtime)
Outputs: Software Project (end result)
Software: Program parses the Program Model and Instructions File into 2 respective nodes. The instructions are then interpreted in such a way that data from the instructions and model are bound to templates given in the instructions and files are created in directories given in the instructions. It is typical that instructions must be aware of the structure of the program model as well as what templates are available. It is also typical that the type of program being written as output will be determined by the templates picked in the instructions and the files and contents will be determined by the model.

## What's coming?

[TODO](TODO.md) Because I decided perfect isn't a good goal for the first release
