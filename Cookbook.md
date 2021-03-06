# CONLL utilities cookbook

## OpenNLP tagger

Required steps:

1. Replace the PROP tag by PROAV.
2. Replace the PIDAT tag by PIAT.
3. Don't use coarse-grained tags.
4. Remove the features column.

Command:

    conll-replace PROP:PROAV,PIDAT:PIAT postag r9+lemmas-82043.conll | \
      conll-postag -f | conll-replace ".*:_" FEATURES > r9+lemmas-82043-onlp.conll

## Malt parser

Required steps:

1. Replace the PROP tag by PROAV.
2. Replace the PIDAT tag by PIAT.
3. Don't use coarse-grained tags.
4. Remove the features column.
5. Remove lemma column

Command:

    conll-replace PROP:PROAV,PIDAT:PIAT postag r9+lemmas-82043.conll | \
      conll-postag -f | conll-replace ".*:_" FEATURES | \
      conll-replace ".*:_" LEMMA > r9+nolemmas-82043-malt.conll

## Malt parser (morphology)

Required steps:

1. Construct data for the malt parser (see previous recipe).
2. Convert the data from CoNLL-X to TCF.
3. Upload the TCF to WebLicht and add morphology annotations.
4. Convert the resulting TCF back to CoNLL-X.

CoNLL to TCF:

    conll2tcf -g de -d tuebadz -p tuebadz r9+nolemmas-82043-malt.conll \
      r9+nolemmas-82043-malt.tcf

TCF to CoNLL:

    tcf2conll -p -d -m r9+nolemmas-morph-82043-malt.tcf \
      r9+nolemmas-morph-82043-malt.conll

## Prepare a sample for manual evaluation

Required steps:

* Take a corpus sample.
* Create a TCF file with the necessary layers

For instance, for the evaluation of a dependency parser, we'd like 100
sentences, create a TCF file with gold standard sentences, tokens, and
tags:

    conll-sample -n 100 eval.conll | conll2tcf > eval-200.tcf

Of course, if you want to be able to create the same sample every time
without keeping the intermediate CONLL file, you could use a fixed
seed:

    conll-sample -s 42 -n 100 eval.conll | conll2tcf > eval-200.tcf

## Partitioning data

For some tasks (e.g. training the Malt parser) the complete set of
sentences of a corpus may be to large. The corpus can be partitioned
such that each partition contains (nearly) the same number of sentences
and contains sentences spread throughout the corpus. For instance,
to split a corpus in five parts:

    conll-partition 5 part .conll r9+lemmas-82043.conll

The first argument specifies the number of partitions. The second argument
the prefix of each partition file and the second argument the suffix. So,
this command will create the files *part1.conll, ..., part5.conll*. Of
course, this command can also read from the standard input. E.g.:

    conll-replace PROP:PROAV,PIDAT:PIAT postag r9+lemmas-82043.conll | \
      conll-postag -f | conll-partition 5 part .conll
