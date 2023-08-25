(ns clojitlein.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [org.eclipse.jgit.api Git]
           [org.eclipse.jgit.lib Repository]
           [org.eclipse.jgit.storage.file FileRepositoryBuilder]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

; https://www.braveclojure.com/java/
; https://www.vogella.com/tutorials/JGit/article.html
; https://git-scm.com/book/en/v2/Appendix-B%3A-Embedding-Git-in-your-Applications-JGit
; https://javadoc.io/doc/org.eclipse.jgit/org.eclipse.jgit/latest/org.eclipse.jgit/org/eclipse/jgit/lib/Repository.html

(def repo-dir "..\\samplerepo")

; -------------------- Git functions (via porcelain commands)

(defn create-ref
  [ref regex]
  (let [leaf (.getLeaf ref)
        name (str/replace-first (.getName leaf) regex "")
        object (-> leaf (.getObjectId) (.getName))]
    {:name name :object object}))

(defn branch-list
  [git]
  (let [refs (-> git (.branchList) (.call))]
    (map #(create-ref % #"refs/heads/") refs)))

(defn tag-list
  [git]
  (let [refs (-> git (.tagList) (.call))]
    (map #(create-ref % #"refs/tags/") refs)))

; -------------------- Values & Tests

; porcelain commands
(def git
  (Git/open (io/file repo-dir)))
(def api-repo
  (.getRepository git))
(def branches (branch-list git))
(def tags (tag-list git))

; plumbing commands
(def lib-repo
  (-> (FileRepositoryBuilder.)
      (.setGitDir (io/file repo-dir))
      (.readEnvironment)
      (.findGitDir)
      (.build)))
