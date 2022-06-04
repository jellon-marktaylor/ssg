# Overview

Provides abstractions and implementations for input/output. Similar to how an HTTP server will want to metadata on the
type of content it is receiving, some of these abstractions allow for a hint which the implementation may use to provide
custom functionality.

* api: abstractions which an ssg engine will need to implement
* spi: abstractions with default implementations in impl module
* impl: implementations of abstractions in spi modules
* test: reusable lib (yes, you might want your module to depend on this) for testing with io and all unit-tests for io
  modules

How each abstraction or implementation should work is documented in detail in the test module unit-tests.

# Use-Cases

Obviously, the Flagship engine contains a variety of classes which access or create resources. One use for using a hint
is an IOutputStreamResources which returns a custom OutputStream implementation for java files. It writes the source
code in-memory. When the OutputStream is flushed, it parses out the class name, creates the file in the requested
directory, and writes the code there. This allows users to dynamically save very many java files to a directory without
specifying a file name for each.

Providing a hint allows for all kinds of possibilities being created. One could even save a JSON object in a hint that
the implementation parses out. If you don't want to bother with hints, then don't. Power to developers! :)
