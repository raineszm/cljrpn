(ns rpn.commands
  (:use [rpn.stack]
        [rpn.operators :only [*operators*]])
  (:use [rpn.utils]))

(defn stack-pop []
  (let [val (popf)]
    (if val
      (println val)
      (println "The stack is empty..."))))

(defn stack-show []
  (let [size (stack-size)]
    (println "STACK:")
    (println "(TOP)")
    (loop [i 0]
      (when-not (= i size)
        (println i ": " (nth @*main-stack* i))
        (recur (inc i))))))

(defn dup []
  (if-let [bot (peek @*main-stack*)]
    (pushf bot)))

(defn swap []
  (when (>= (stack-size) 2)
    (apply pushf (popf 2))))

(defn build-cmd [kwargs cmd help]
  (let [kwargs (as-vec kwargs)]
    (mapcat (fn [kw]
              [(keyword kw) {:cmd cmd :cmds kwargs :help help}]) kwargs)))

(def *cmds*
  (construct build-cmd
    [:. stack-pop "Pops and prints the top of the stack"]
    [:.s stack-show "Prints the whole stack"]
    [:dup dup (effect "a" "a a")]
    [:swap swap (effect "a b" "b a")]
    [[:q :quit :.q] #(System/exit 0) "Exits the program"]))

(defn cmd? [o]
  (contains? *cmds* (keyword o)))

(defn process-cmd [o]
  ((-> (keyword o) *cmds* :cmd)))

