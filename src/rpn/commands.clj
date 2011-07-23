(ns rpn.commands
  (:use [rpn.stack]
        [rpn.operators :only [*operators*]]))

(defn stack-pop []
  (let [val (popf)]
    (if val
      (println val)
      (println "The stack is empty..."))))

(defn stack-show []
  (println "TOP> " @*main-stack* " <BOTTOM")) 


(defn quit []
  (System/exit 0))

(def *cmds*
  {
   :. stack-pop
   :.s stack-show
   :quit quit})

(defn cmd? [o]
  (contains? *cmds* (keyword o)))

(defn process-cmd [o]
  ((*cmds* (keyword o))))

