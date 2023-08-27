(ns clojit.plumbing
  (:require [clojure.java.io :as io])
  (:import [org.eclipse.jgit.storage.file FileRepositoryBuilder]))

(defn open
  "Return a handle to interact with a repository using plumbing commands.
  repo-dir-path may be either the .git directory or that containing it."
  [repo-dir-path]
  (-> (FileRepositoryBuilder.)
      (.setGitDir (io/file repo-dir-path))
      (.readEnvironment)
      (.findGitDir)
      (.build)))
