# Flagship Engine User Docs

This document is for those who want to use the app as is by supplying an input file.

If your goal is to customize the app, see [customizer documentation](developer.md) instead.

If your goal is to modify the source code for the SSG project itself, see [maintainer documentation](../../../docs/developer/README.md) and [flagship](../README.md) instead.

## Executing The App

At this point, the easiest way to execute is to use IntelliJ and install the scala plugin. It will be more user friendly in the future.

## Inputs

The flagship engine takes 2 required inputs and one optional.
1. project model (must be available as either a resource or file relative to directory being executed from)
2. instructions (must be available as either a resource or file relative to directory being executed from)
3. root output directory (is not required to exist yet)

### Input 1) Project Model

The project model must be parsable into a [node](../../../node/README.md). By default, this includes JSON, YAML (aka YML),
and XML. The project model may contain any values you want, but advanced features such as XML entities may not be
supported. It should work for all simple use-cases.

Whatever your model looks like, your instruction file will probably need to know the structure of your project model.

### Input 2) Instructions

The instructions file must be parsable into a node, just like the project model.

Each item in your file must conform to a processor defined in code. The details of what is supported out-of-the-box are
included in a later section.

### Input 3) Root Output Directory (optional)

If supplied, all outputs will be nested in the provided directory. The directory should ideally be relative to the
directory the application is being executed from. If it doesn't exist, it will be created. It defaults to the directory
the application is being executed from.

## Instruction Processors

By default, flagship currently supports 5 instructions: scope, define, loop, unzip, and st4. Future support for velocity
will be added.

### Examples

These examples get executed in the engine/flagship/test module

#### Example 1: only input (JSON)

```json
{
  "scope": {
    "define": {
      "internalStuff": {
        "root": ".",
        "path": "/my/custom/path"
      },
      "myCustomDefs": {
        "count": 1,
        "foo": "bar"
      }
    },
    "st4": {
      "using": "myCustomDefs",
      "template": "<count> = <foo>",
      "output": "<internalStuff.root>/<internalStuff.path>/output.txt"
    }
  }
}
```

If the program is executed, it creates file `./my/custom/path/output.txt` and it will contain the text `1 = bar`

#### Example 2: Program model (YAML / XML)

This time, we'll do something a little more useful. We're going to create a pom.xml file. The files for this example may
be found under app/example2 in the repository.

project.yaml
```yaml
dependencies:
  - version: 1.7.25
    groupId: org.slf4j
    artifacts: [ slf4j-api, slf4j-log4j12 ]
  - version: 1.26
    groupId: org.yaml
    artifacts: snakeyaml
  - version: 4.3.25.RELEASE
    groupId: org.springframework
    artifacts: [ spring-beans, spring-web, spring-webmvc ]
```

Alternatively, you may use any element name for your root node. Note the different styles you may use.
project.xml
```xml
<root>
    <groupId>jellon.example</groupId>
    <artifactId>example2</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <dependencies>
        <slf4j groupId="org.slf4j" version="1.7.25">
            <artifacts>slf4j-api</artifacts>
            <artifacts>slf4j-log4j12</artifacts>
        </slf4j>
        <jdbi groupId="org.yaml" version="4.3.25.RELEASE" artifacts="snakeyaml"/>
        <spring-beans>
            <groupId>org.springframework</groupId>
            <version>4.3.25.RELEASE</version>
            <artifacts>spring-beans</artifacts>
            <artifacts>spring-web</artifacts>
            <artifacts>spring-webmvc</artifacts>
        </spring-beans>
    </dependencies>
</root>
```

Example instructions:
```json
{
  "scope": {
    "st4": {
      "stgroup": "mvn.stg",
      "template": "deps",
      "using": "input",
      "output": "auto-pom.xml"
    }
  }
}
```

mvn.stg
```stgroup
delimiters "`", "`"

version(groupId, version) ::= "<version.`groupId`>`version`</version.`groupId`>"

dependency(groupId, artifactId, version) ::= <<
<dependency>
    <groupId>`groupId`</groupId>
    <artifactId>`artifactId`</artifactId>
    `if(version)`<version>${version.`groupId`}</version>`endif`
</dependency>
>>

dependency0(dep) ::= "`dependency(dep.groupId, dep.artifactId, dep.version)`"

dependencyManagement(dep) ::= "`dep.artifacts:{artifactId | `dependency(dep.groupId, artifactId, true)`}; separator={`\n`}`"

dependencies(dep) ::= "`dep.artifacts:{artifactId | `dependency(dep.groupId, artifactId, false)`}; separator={`\n`}`"

deps(dependencies) ::= <<
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>`groupId`</groupId>
    <artifactId>`artifactId`</artifactId>
    <version>`version`</version>
    <packaging>pom</packaging>

    <properties>
        `dependencies:{dep |`version(dep.groupId, dep.version)`}; separator={`\n`}`
    </properties>

    <dependencyManagement>
        <dependencies>
            `dependencies:{dep |`dependencyManagement(dep)`}; separator={`\n``\n`}`
        </dependencies>
    </dependencyManagement>

    <dependencies>
        `dependencies:{dep |`dependencies(dep)`}; separator={`\n``\n`}`
    </dependencies>
</project>

>>
```
