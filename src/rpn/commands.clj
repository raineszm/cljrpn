(ns rpn.commands
  (:use [rpn.stack]))

(defn stack-pop []
  (let [val (popf)]
    (if val
      (println val)
      (println "The stack is empty..."))))

(defn stack-show []
  (println "TOP> " @*main-stack* " <BOTTOM")) 

(def *cmds*
  {
   :. stack-pop
   :.s stack-show})

(defn cmd? [o]
  (contains? *cmds* (keyword o)))

(defn process-cmd [o]
  ((*cmds* (keyword o))))

