(ns lsl.macro
  (:require [clojure.string :as string]))

(defmulti parse #(subs %1 0 4))


;; i cannot directly use in-arg# in defmethod
;; since auto-gensyms are not part of syntax unquote
(defmacro defmapping
  [mapping-name mapping-type & body]
  (let [tp (keyword (string/lower-case
                     mapping-type))
        in-arg (gensym)]
    `(defmethod parse ~mapping-type
       [~in-arg]
       ~(merge {:type `~tp}
               (into {}
                     (for [[s# e# k#] body]
                       [(keyword k#) `(subs ~in-arg ~s# ~e#)]))))))

(macroexpand-1
 '(defmapping service-call "SVCL"
    (04 18 customer-name)
    (19 23 customer-id)
    (24 27 call-type-code)
    (28 35 date-of-call-string)))

(defmethod parse "SVCL"
  [line]
  {:type :svcl
   :customer-name (subs line 4 18)
   :customer-id (subs line 19 23)
   :call-type-code (subs line 24 27)
   :date-of-call-string (subs line 28 35)})

(defmapping service-call "SVCL"
    (04 18 customer-name)
    (19 23 customer-id)
    (24 27 call-type-code)
    (28 35 date-of-call-string))

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
