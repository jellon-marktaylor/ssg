# Customizer

This document is for those who want to customize the app by creating a plugin.

For more information about the existing software see [developer documentation](README.md).

If your goal is to use the app as is just creating an input file, see [user documentation](../../engine/flagship/docs/user.md) instead.

## Extension Points

Whatever you add, you will need to configure DI. Guice and Spring are both supported out-of-the-box, but there's nothing
keeping one from using something else (OSGi, for instance).

### Text => Node

If you want to change how a text format is read into a node (or add your own), implement INodeParser and INodeParsers.
These traits can be found in node-parser-api. The default implementations are found in node-parser-impl. The DI modules
can be found in engines/flagship/guice or engines/flagship/spring (coming soon).

### Engines

An engine is what drives inputs into outputs. Flagship is the only planned engine at this time (although that may
change). For more info, see [Flagship Engine](../../engine/flagship/README.md).
