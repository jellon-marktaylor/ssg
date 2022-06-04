# Flagship Engine

This document is for those who want to make modifications specifically to the flagship engine.

Flagship allows one to customize how a node from the is paired with one or more templates. It might be helpful to some
to know the inspiration was taken from Gherkin/Cucumber feature files. Even though the structure is completely
different, both "glue" a text file to executable code.

## Flagship Engine Processor (instruction)

An instruction is something like "define", "loop", "scope", "st4", "velocity" etc in an instruction file that pairs
input with templates. To add your own, implement INodeProcessor and INodeProcessors in engines/flagship/api. Each

INodeProcessor will implement the method `path: String`. The path will just be the name of a node. So if your path is
`"custom"` it would be picked up by `"scope": { "custom": {} }` or `<scope><custom>` in the instruction file.

INodeProcessor will implement the method `process(state: INodeMap, engine: IFlagshipEngine): INodeMap`. The state passed
in will contain a key for different nodes. By default, `input` will be defined as the node defined by the program model.
`instructions` will be the node defined by instructions. Normally, a processor will return an empty INodeMap. An
exception to this rule would be the `define` processor which returns an INodeMap containing only the values defined in
the instructions.
