(ns rpn.stack)

(def *main-stack* (ref ()))

(defn stack-size []
  (count @*main-stack*))

(defn pushf
  ([f] (dosync
         (alter *main-stack* conj f)))
  ([f & others] (let [args (cons f others)]
                  (doseq [a args]
                    (pushf a)))))

(defn popf
  ([] (if-let [p (popf 1)]
        (first p)))
  ([n] (let [n (if (< n 0) (stack-size) n)
             popped (take n @*main-stack*)]
         (if (= (count popped) n)
           (dosync
             (alter *main-stack* (partial drop n))
             popped)))))

(defn apply-op [op n]
  (dosync
    (if-let [args (reverse (popf n))]
      (pushf (apply op args)))))
