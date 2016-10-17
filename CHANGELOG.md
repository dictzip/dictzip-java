# Change Log
All notable changes to this project will be documented in this file.

## [Unreleased]
### Add
- Swing dialog for PGP signing passphrase input.

### Changed
- Update Gradle version to 3.1

## [0.8.2] - 2016-10-16
### Fixed
- Fix a coding Style warning for DictZipInputStream
- Test fix for header comparison range.

### Changed
- Move project URL to https://www.dictzip.org/.

## [0.8.1] - 2016-4-29
### Fixed
- Gradle: github and bintray release error.
- [#21] fixed unexpected EOFException when readFully() called after seek().

## [0.8.0] - 2016-4-29
### Add
- More javadoc description. Complete for library APIs.

### Changed
- DictZipHeader.setHeaderCRC() argument become final.
- DictZipFileUtils class become final.
- CLI: drop -S -E <base64> arugment.
- CLI: support -s/-e hex(0xAAAA) and octet(0777) numbers.
- CLI: update man page according to option changes.
- Manpage description.
- CLI: start script is now 'dictzip' and 'dictzip.bat', all small caps.

### Fixed
- Generate Maven POM with a proper groupId.
- DictZipInputStream: copy buffer with offset by System.copyarray().
- CLI: Enable -t --test functionarity.
- CLI: Fix --version not working.
- CLI: Fix all coding style warnings.

## [0.7.0] - 2016-4-25
### Add
- Add test option for DictZip CLI.
- Add DictZipInputStream consructor that get a filename as argument.
- More tests.

### Changed
- Default buffer size in the DictZipInputStream was changed to 8192 byte
  from 512 byte as same as java.io.BufferedInputStream.
- Test static utility isFileBinaryEquals becomes an external library.

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
- [#3] License changed to GPL2++classpath(lib) exception and GPL3(CLI)
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

[Unreleased]: https://github.com/dictzip/dictzip-java/compare/v0.8.2...HEAD
[0.8.2]: https://github.com/dictzip/dictzip-java/compare/v0.8.1...v0.8.2
[0.8.1]: https://github.com/dictzip/dictzip-java/compare/v0.8.0...v0.8.1
[0.8.0]: https://github.com/dictzip/dictzip-java/compare/v0.7.0...v0.8.0
[0.7.0]: https://github.com/dictzip/dictzip-java/compare/v0.6.1...v0.7.0
[0.6.1]: https://github.com/dictzip/dictzip-java/compare/v0.6.0...v0.6.1
[0.6.0]: https://github.com/dictzip/dictzip-java/compare/v0.5.0...v0.6.0
[0.5.0]: https://github.com/dictzip/dictzip-java/compare/v0.0.2...v0.5.0
