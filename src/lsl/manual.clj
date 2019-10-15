(ns lsl.manual
  (:require [clojure.java.io :as io]))

(defmulti parse #(subs %1 0 4))

(defmethod parse "SVCL"
  [line]
  {:type :svcl
   :customer-name (subs line 4 18)
   :customer-id (subs line 19 23)
   :call-type-code (subs line 24 27)
   :date-of-call-string (subs line 28 35)})

;; (defmapping service-call "SVCL"
;;     (04 18 customer-name)
;;     (19 23 customer-id)
;;     (24 27 call-type-code)
;;     (28 35 date-of-call-string))

;; (defmapping usage "USGE"
;;     (04 08 customer-id)
;;     (09 22 customer-name)
;;     (30 30 cycle)
;;     (31 36 read-date))

(defmethod parse "USGE"
  [line]
  {:type :usge
   :customer-id (subs line 4 18)
   :customer-name (subs line 9 22)
   :cycle (subs line 30 30)
   :read-date (subs line 31 36)})

(defmethod parse :default
  [_]
  )

(comment
  (with-open [sample-log (io/reader (io/resource "sample.log"))]
    (doall
     (map parse
          (line-seq sample-log))))
  )
