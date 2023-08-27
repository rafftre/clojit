(ns clojit.porcelain
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [org.eclipse.jgit.api Git]))

(defn open
  "Return a handle to interact with a repository using porcelain commands.
  repo-dir-path may be either the .git directory or that containing it."
  [repo-dir-path]
  (Git/open (io/file repo-dir-path)))

(defn- create-ref
  "Create a reference, i.e. a pair of a name and an object id.
  The name is a stripped down string with the prefix removed using the provided
  regex. Symbolic links and peeled tags are resolved to target object id."
  [ref regex]
  (let [leaf (.getLeaf ref)
        name (str/replace-first (.getName leaf) regex "")
        object (-> leaf (.getObjectId) (.getName))]
    {:name name :object object}))

(defn branch-list
  "Return a list with the local branch/head names."
  [repo]
  (let [refs (-> repo (.branchList) (.call))]
    (map #(create-ref % #"refs/heads/") refs)))

(defn tag-list
  "Return a list with the tag names."
  [repo]
  (let [refs (-> repo (.tagList) (.call))]
    (map #(create-ref % #"refs/tags/") refs)))
