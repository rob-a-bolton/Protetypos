=====
Usage
=====

Using a leiningen REPL, call (train (wrap-seq-keywords (file-word-seq "input-file.txt")) a-hash depth)
To generate text, call (make-salad a-hash depth length), using the same hash used by (train).
