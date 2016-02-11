(defproject markov "0.1.0-SNAPSHOT"
  :description "Markov generator prototype"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :main ^:skip-aot markov.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
