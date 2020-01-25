(ns lsl.with)

(defmacro with-timing-no-exception-handling
  [& body]
  `(let [start-time# (System/currentTimeMillis)]
     (println "start:" start-time#)
     ~@body
     (let [end-time# (System/currentTimeMillis)]
       (println "end:" end-time#)
       (println (- end-time# start-time#)))))

(with-timing-no-exception-handling
  (+ 1 2)
  (/ 1 0))

;; first cut
;; print the start time, end time and the duration
;; it takes to do something
(defmacro with-timing
  [& body]
  `(let [start-time# (System/currentTimeMillis)]
     (try
       (println "start:" start-time#)
       ~@body
       (finally
         (let [end-time# (System/currentTimeMillis)]
           (println "end:" end-time#)
           (println (- end-time# start-time#)))))))

(with-timing
  (+ 1 2)
  (* 1 2))

;; milliseconds is too small
(defmacro with-timing-nano
  [& body]
  `(let [start-time# (System/nanoTime)]
     (try
       (println "start:" start-time#)
       ~@body
       (finally
         (let [end-time# (System/nanoTime)]
           (println "end:" end-time#)
           (println (/ (- end-time# start-time#)
                       1000000.0)))))))

(with-timing-nano
  (+ 1 2)
  (* 1 2))

;; what i really want is to just print one line
;; and tag each line with a keyword that i can
;; search in my logs

(defmacro with-timing-tagged
  [tag & body]
  `(let [start-time# (System/nanoTime)]
     (try
       ~@body
       (finally
         (let [end-time# (System/nanoTime)]
           (println ~tag
                    "duration-in-ms:"
                    (/ (- end-time# start-time#)
                       1000000.0)))))))

(with-timing-tagged :doing-something-useful
  (+ 1 2)
  (/ 1 0))
