(ns cljrpn.stack)

(def 
  ^{:doc "The main stack for the calculator"}
  ^:dynamic
  *main-stack* 
  (ref ()))

(defn stack-size []
  "Returns the number of elements on the stack"
  (count @*main-stack*))

(defn pushf
  "Adds the supplied elemnts to the top of the stack"
  ([f] (dosync
         (alter *main-stack* conj f)))
  ([f & others] (let [args (cons f others)]
                  (doseq [a args]
                    (pushf a)))))

(defn popf
  "Fetches n arguments from the top of the stack. Popping them one at a time.
  n defaults to 1"
  ([] (if-let [p (popf 1)]
        (first p)))
  ([n] (let [n (if (neg? n) (stack-size) n)
             popped (take n @*main-stack*)]
         (if (= (count popped) n)
           (dosync
             (alter *main-stack* (partial drop n))
             popped)))))

(defn clear-stack []
  "Empties the stack"
  (dosync (ref-set *main-stack* '())))





(defn apply-op [op n]
  "Apply the function op to the top n members of the stack."
  (dosync
    (if-let [args (popf n)]
      (pushf (apply op (reverse args))))))
