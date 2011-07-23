(ns rpn.commands
  (:use [rpn.stack]
        [rpn.operators :only [*operators*]]
        [clojure.string :only [replace-first]]))

(defn stack-pop []
  (let [val (popf)]
    (if val
      (println val)
      (println "The stack is empty..."))))

(defn stack-show []
  (println "TOP> " @*main-stack* " <BOTTOM")) 

(declare *cmds*)

(defn- prep-keys [k]
  (map #(replace-first (str %) ":" "") k))

(defn help []
  (do
    (print "Basic operators: ")
    (apply println (prep-keys (keys @*operators*)))
    (println)
    (print "Basic commands: ")
    (apply println (prep-keys (keys *cmds*)))))

(defn quit []
  (System/exit 0))

(def *cmds*
  {
   :. stack-pop
   :.s stack-show
   :? help
   :quit quit})

(defn cmd? [o]
  (contains? *cmds* (keyword o)))

(defn process-cmd [o]
  ((*cmds* (keyword o))))

