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
- Apache commons IO library(Apache2)


## Copyrights and License

DictZip library for Java and command line utility.

Copyright (C) 2001-2004 Ho Ngoc Duc
Copyright (C) 2016 Hiroshi Miura

Some part of this program code are come from a part of abandoned jdictd on java
by JDictd project.

DictZip command line utility is distributed under the terms of GNU General
Public License Version 3 or (at your option) any later version.

DictZip library is distributed under the terms of the GNU General Public License
Version 2 or (at your option) any later version.
