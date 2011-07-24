(ns rpn.modifiers
  (:use [rpn.operators :only [*operators* operator?]]
        [rpn.commands :only [*cmds* cmd? build-cmd]]
        [rpn.utils]
        [clojure.string :only [replace-first]]))


(declare *modifiers*)

(defn- prep-keys [k]
  (map #(replace-first (str %) ":" "") k))

(defn print-help [table sym]
  (let [op ((keyword sym) table)
        aliases (:cmds op)
        effect (:help op)]
    (apply println "Aliases: "(prep-keys aliases))
    (println effect))
  (println))

(declare modifier?)

(defn help
  ([]
   (do
     (print "Operators: ")
     (apply println (prep-keys (keys *operators*)))
     (println)
     (print "Commands: ")
     (apply println (prep-keys (keys *cmds*)))
     (println)
     (print "Modifiers: ")
     (apply println (prep-keys (keys *modifiers*)))))
  ([sym]
   (cond
     (operator? sym) (print-help *operators* sym)
     (cmd? sym) (print-help *cmds* sym)
     (modifier? sym) (print-help *modifiers* sym))))

(def *last-mod* (atom nil))

(def *modifiers*
  (construct build-cmd
             [[:? :h :help] help "When called with an argument displays help information about that command. Otherwise, displays a command list."]))

(defn modifier? [m]
  (contains? *modifiers* (keyword m)))

(defn process-mod [m]
  (reset! *last-mod* (-> (keyword m) *modifiers* :cmd)))

(defn trigger-mod
  ([]
   (when @*last-mod*
     (@*last-mod*)
     (reset! *last-mod* nil)))
  ([sym]
   (@*last-mod* sym)
   (reset! *last-mod* nil)))
