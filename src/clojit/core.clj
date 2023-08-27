(ns clojit.core
  (:require [clojure.string :as str])
  (:use [clojit.porcelain]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
