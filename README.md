# dictzip-java
DictZip, GZip random access compression format(.dz), access library for Java

## Usage

Extract archive in directory. If you want to use just library, `dictzip-lib.jar`
is an only file for you.
CLI is also made for example of API usage and will be a good reference.
Also there is a javadoc of DictZip at https://miurahr.github.io/dictzip-java

## Build

DictZip for java uses Gradle for build system. You can build library and CLI
by typing command:

```
$ gradle build
```

You will find archive files at

```
dictzip-cli/build/distributions/
```

## Contribution

As usual of other projects hosted on GitHub, DictZip for java also welcome
forking source and send modification as a Pull Request.
It is recommended to post an issue before sending a patch.


## Dependencies

DictZip library does not depend on any project without Java standard libraries.

DictZip CLI utility depends on some libraries.

### Runtime depenency

- java-getopts library(GPLv2+)
- Apache commons codec library(Apache2)

### Test dependency

- TestNG framework(Apache2)

## Copyrights and License

DictZip library for Java and command line utility.

Copyright (C) 2001-2004 Ho Ngoc Duc

Copyright (C) 2016 Hiroshi Miura

Some part of this program are come from a part of jdictd 1.5 on java.

DictZip command line utility is distributed under the terms of GNU General
Public License Version 3 or (at your option) any later version.

DictZip library is distributed under the terms of the GNU General Public License
Version 2 or (at your option) any later version with the following clarification
and special exception as same as GNU classpath.

Linking this library statically or dynamically with other modules is making
a combined work based on this library. Thus, the terms and conditions of
the GNU General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you permission
to link this library with independent modules to produce an executable, regardless
of the license terms of these independent modules, and to copy and distribute
the resulting executable under terms of your choice, provided that you also meet,
for each linked independent module, the terms and conditions of the license of
that module. An independent module is a module which is not derived from or based on
this library. If you modify this library, you may extend this exception to
your version of the library, but you are not obligated to do so. If you do not wish
to do so, delete this exception statement from your version.

As such, it can be used to run, create and distribute a large class of applications
and applets. When this library is used unmodified as the core class library
for a virtual machine, compiler for the java languge, or for a program written
in the java programming language it does not affect the licensing for distributing
those programs directly.
