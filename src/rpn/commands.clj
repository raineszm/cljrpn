(ns rpn.commands
  (:use [rpn.stack]
        [rpn.operators :only [*operators*]]
        [rpn.utils :only [as-vec]]))

(defn stack-pop []
  (let [val (popf)]
    (if val
      (println val)
      (println "The stack is empty..."))))

(defn stack-show []
  (println "TOP> " @*main-stack* " <BOTTOM")) 


(defn build-cmd [kwargs cmd help]
  (let [kwargs (as-vec kwargs)]
    (mapcat (fn [kw]
              [(keyword kw) {:cmd cmd :cmds kwargs :help help}]) kwargs)))

(defn construct-cmds [& specs]
  (apply hash-map
    (mapcat #(apply build-cmd %) specs)))


(def *cmds*
  (construct-cmds 
    [:. stack-pop "Pops and prints the top of the stack"]
    [:.s stack-show "Prints the whole stack"]
    [[:q :quit] #(System/exit 0) "Exits the program"]))

(defn cmd? [o]
  (contains? *cmds* (keyword o)))

(defn process-cmd [o]
  ((-> (keyword o) *cmds* :cmd)))

