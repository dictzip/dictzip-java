# Change Log
All notable changes to this project will be documented in this file.

## [Unreleased]
### Add
- Add test option for DictZip CLI.
- Add DictZipInputStream consructor that get a filename as argument.

### Changed
- README: there is no test dependency for commons-io.
- Default buffer size in the DictZipInputStream was changed to 4096 byte.

### Fixed
- Check return value of InputStream.skip() method in the utility method
  for test.

## [0.6.1] - 2016-4-12
### Fixed
- CLI: test compile error because DictZipFileUtils is package private,
  but cli test depends on it. We add a utility method on test class.

## [0.6.0] - 2016-4-10
### Add
- gradle.properties.template
  You need to copy to gradle.properties and edit its configuration.
- [#8] Support -# --fast --best option in CLI.
- DictZipHeader.getExtraFlag() method.

### Changed
- We have now dictzip-lib and dictzip-cli subprojects.
- Target jars become dictzip-lib.jar and dictzip-cli.jar
- [#2] Now this is a Gradle project.
- Reorganize file structures on Maven rules.
- Command script by Gradle.
- Delete external libraries. Now gradle will download it.
- README: add build procedure, contribution and copyright.
- Coding style checks by checkstyle 6.16.1
- DictZip header information API through DictZipInputStream.

### Fixed
- [#7] Fix DictZipInputStream.seek().Previous all releases are broken.
- [#5] Fix coding styles and potential problematic codes.

## [0.5.0] - 2016-03-21
### Added
- [#1] Command line tool subproject.
- CHANGELOG.md file.

### Changed
- DictZipHeader interface.
- DictZipOutputStream constructor interface.

### Fixed
- Broken output features in previous releases.

## 0.0.2 - 2016-03-06
### Added
- Readme document.

## 0.0.1 - 2016-02-28
### Added
- Start project.

[Unreleased]: https://github.com/miurahr/dictzip-java/compare/v0.5.0...HEAD
[0.6.0]: https://github.com/miurahr/dictzip-java/compare/v0.5.0...v0.6.0
[0.5.0]: https://github.com/miurahr/dictzip-java/compare/v0.0.2...v0.5.0
