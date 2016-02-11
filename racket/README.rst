=====
Usage
=====

Load up a Racket repl and ,enter markov.rkt
Use a method which changes the current input port to read from a source of input data,
then call (train a-hash depth). *a-hash* must be an existing hash, and it will be mutated.
Next call (generate a-hash depth num-words) to generate num-words of words from a-hash.
