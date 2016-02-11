(ns markov.core
  (:gen-class)
  (:require [clojure.java.io :as jio]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn wrap-seq-keywords
  "Wraps a sequence with :start and :end keywords."
  [s]
  (concat [:start] s [:end]))

(defn file-word-seq
  "Gets a sequence of words from a file."
  [filename]
  (let [l (line-seq (jio/reader (jio/file filename)))]
    (flatten (map (fn [line] (seq (.split (.toLowerCase line) " "))) l))))
  

(defn nil-take
  "Returns first n items from list, padding with nils
  if necessary."
  [n col]
  (take n (concat col (repeat nil))))

(defn get-weight
  "Gets the weight of a word using its value
   or the sum of its children's values."
  [value]
  (if (number? (val value))
    (val value)
    (reduce + (map get-weight (val value)))))

(defn get-potential-words
  "Gets the weighted list of words from a word hash."
  [word-hash words]
  (let [root-children (get-in word-hash words)]
    (zipmap (keys root-children) (map get-weight root-children))))

;; See:
;; https://github.com/sjl/roul/blob/master/src/roul/random.clj 2016/02
;; http://stackoverflow.com/questions/14464011/idiomatic-clojure-for-picking-between-random-weighted-choices 2016/02
(defn generate-word
  "Generates a word for the chain."
  [word-hash words]
  (let [potential-words (get-potential-words word-hash words)
        potential-count (reduce + (vals potential-words))
        threshold (rand-int potential-count)]
    (if (= 0 potential-count)
      nil
      (loop [num threshold
             [[word weight] & rest] (seq potential-words)]
        (if (>= weight num)
          word
          (recur (- num weight) rest))))))
           

(defn train
  "Trains on a sequence of words, returning a hash of the weighted data."
  [word-seq word-hash depth]
  (loop [word-hash word-hash
         word-seq word-seq]
    (let [words (nil-take depth word-seq)
          word-count (get-in word-hash words 0)]
      (if (= (first words) :end)
        word-hash
        (recur
         (assoc-in word-hash words (inc word-count))
         (drop 1 word-seq))))))

(defn make-salad
  "Creates a delicious fresh word salad."
  [word-hash chain-depth salad-length]
  (drop 1 (reverse
           (loop [words [:start]
                  words-left salad-length]
             (if (= words-left 0)
               words
               (let [next-word (generate-word word-hash (reverse (take chain-depth words)))]
                 (if (nil? next-word)
                   words
                   (recur (cons next-word words) (dec words-left)))))))))
