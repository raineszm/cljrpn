(ns rpn.core)

(def main-stack (ref ()))

(defn pushf
  ([f] (dosync
         (alter main-stack conj f)))
  ([f & others] (let [args (cons f others)]
                  (doseq [a args]
                    (pushf a)))))

   
(defn stacked? [n]
  (>= (count @main-stack) n))

(defn popf
  ([] (dosync
    (let [popped (first @main-stack)]
         (alter main-stack pop)
         popped)))
  ([n] (when (stacked? n)
    (loop [n n
           acc []]
      (if (= n 0)
        acc
        (recur (dec n) (conj acc (popf))))))))



(defn apply-op [op n]
  (dosync
    (if-let [args (popf n)]
      (pushf (apply op args)))))


(defn -main [& args]
  (println "Hello rpn"))
