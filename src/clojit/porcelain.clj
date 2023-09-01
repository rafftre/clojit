(ns clojit.porcelain
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [org.eclipse.jgit.api Git]
           [org.eclipse.jgit.revwalk RevWalk]))

(defn open
  "Return a handle to interact with a repository using porcelain commands.
  repo-dir-path may be either the .git directory or that containing it."
  [repo-dir-path]
  (Git/open (io/file repo-dir-path)))

(defn get-repo
  "Return a handle to interact with the repository using plumbing commands."
  [git]
  (.getRepository git))

(defn close
  "Close and free resources."
  [git]
  (.close git))

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
  "Return a list with the local branch (head) names."
  [git]
  (let [refs (-> git (.branchList) (.call))]
    (map #(create-ref % #"refs/heads/") refs)))

(defn tag-list
  "Return a list with the tag names."
  [git]
  (let [refs (-> git (.tagList) (.call))]
    (map #(create-ref % #"refs/tags/") refs)))

(defn get-branch
  "Return a branch reference with the specified name."
  [git branch-name]
  (let [branches (branch-list git)]
    (some #(and (= (:name %) branch-name) %) branches)))

(defn- create-person
  [ident]
  {:name (.getName ident)
   :email (.getEmailAddress ident)
   :when (.toEpochMilli (.getWhenAsInstant ident))})

(defn- create-commit
  "Create a commit."
  [ref]
  (let [parents (.getParents ref)]
    {:id (.getName ref)
     :type (.getType ref)
     :parents (map #(.getName %) parents)
     :author (create-person (.getAuthorIdent ref))
     :committer (create-person (.getCommitterIdent ref))
     :date (.getCommitTime ref)
     :message (.getFullMessage ref)}))

(defn commit-list
  "Return the list of all the commits for the branch."
  [git branch]
  (let [repo (.getRepository git)
        oid (.resolve repo (:name branch))
        walk (RevWalk. repo)
        start (.parseCommit walk oid)
        _ (.markStart walk start)
        iter (iterator-seq (.iterator walk))]
    (map #(create-commit %) iter)))
