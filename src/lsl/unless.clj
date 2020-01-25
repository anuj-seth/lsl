(ns lsl.unless)

;; perform the body unless the test is true
(defmacro unless
  [test & then]
  `(if-not ~test
     (do ~@then)))

(unless false
        (println "hello")
        (println "world"))

(unless (= 1 1)
        (println "is 1 = 2"))
