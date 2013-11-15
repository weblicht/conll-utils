# CONLL utilities cookbook

## Prepare data for training the OpenNLP tagger or Malt

Required steps:

* Replace the PROP tag by PROAV.
* Replace the PIDAT tag by PIAT.
* Don't use coarse-grained tags.

Command:
  conll-replace PROP:PROAV,PIDAT:PIAT postag r9+lemmas-82043.conll | \
    conll-postag -f > r9+lemmas-82043-onlp-malt.conll


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

