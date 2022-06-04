# Overview

Make sure you know what a [Node](../README.md) is. This package defines INodeParser and INodeParsers.

* INodeParser: conditionally converts a resource to an INode
* INodeParsers: aggregates INodeParser implementations in one class

Current INodeParser implementations: YAML (supports .yaml or .yml ext), JSON (.json), or XML (.xml). For fine grain
details on implementations, see the unit-tests and test resources.

[JSON](test/src/test/resources/jellon/ssg/node/parser/impl/node.json)
[YAML](test/src/test/resources/jellon/ssg/node/parser/impl/node.yaml)
[XML](test/src/test/resources/jellon/ssg/node/parser/impl/node.xml)
XML has different features than JSON and YAML:

* no support for anonymous list nested in another list. In other words, you can't parse a ListNode containing a
  ListNode.
* supports Nodes that have a value and attributes
