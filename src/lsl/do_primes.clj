(ns lsl.do-primes)

;; this is like the doseq macro
;; but simpler

(defn prime?
  [n]
  (cond
    (= n 0) false
    (= n 1) false
    (= n 2) true
    :else (not-any? #(zero? (mod n %))
                    (range 2 n))))

(defn next-prime
  "Return the next prime greater or equal to n"
  [n]
  (first (filter prime?
                 (iterate inc n))))

(defn primes-between
  [start end]
  (filter prime?
          (range start (inc end))))

(defmacro do-primes
  [[sym start end] & body]
  `(for [~sym (primes-between ~start ~end)]
     (do 
       ~@body)))

(macroexpand-1 '(do-primes [p 0 9]
                           (println "prime is:" p)
                           (+ p 2)))

(do-primes [p 0 9]
           (println "prime is:" p)
           (+ p 2))

(comment

  (= 25
     (count
      (filter prime?
              (range 1 101))))

  (= 2 (next-prime-2 1))

  (= 2 (next-prime-2 2))

  (= 23 (next-prime-2 20))

  (primes-between 0 9)

  )
