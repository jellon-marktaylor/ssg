# Overview

A node is an abstraction of any structured data. Nodes provide a way to represent multiple formats (YAML, JSON, XML,
etc) with arbitrary schemas. XML has features neither JSON nor YAML supports and vice versa. Nodes have all the
important features of all 3 formats and then some. See [Node Parsing](parser/README.md) for more information, especially
if you plan to add support for an unsupported text format.

Generally speaking, a node will either hold a value (ValueNode), list of nodes (ListNode), or map of nodes (MapNode).
All the provided implementations are immutable, making them thread-safe and allows for pure functions. Most of the
functionality of an INode comes from INode.NodeExt which provides all kinds of ways to get data or build a new node from
an existing one.

If INode, INodeList, and INodeMap remind you of org.w3c.dom.Node, it's because they are based on them.
