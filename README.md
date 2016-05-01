# CoNLL-X Utilities

## Introduction

This is a set of utilities to modify files in the CoNLL-X tabular files. The
focus of this package is interoperability with TCF (Text Corpus Format).
However, the majority of the utilities are also useful outside TCF. The
package contains the following programs:

* conll2tcf: convert a CoNLL-X file to TCF.
* conll-postag: replace course-grained tags by fine-grained and vise versa.
* conll-replace: replace certain values in annotation layers.

The expandmorph, merge, partition, and sample utilities can now be found in the [conllx-utils](https://github.com/danieldk/conllx-utils) package.

## Download

Downloads are available on the [release page](https://github.com/weblicht/conll-utils/releases)
and require Java 7.

## Usage

Executing one the commands gives usage information. For some examples see
the cookbook (*Cookbook.md*).

## Todo

A lot, including:

* Partitioning is currently interleaving. Also support chunked partitioning.
* Test with problematic inputs.
* Merge specific columns from two CoNLL files.

## License

This software is under the General Public License version 3 (enclosed in
LICENSE.gpl-3.0.txt).

