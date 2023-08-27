(ns clojit.core
  (:use [clojit.porcelain]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

; -------------------- scratch pad

(def repo-dir "../path/to/repo")

(def repo (open repo-dir))
(def branches (branch-list repo))
(def tags (tag-list repo))
