(ns lsl.macro
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))

(defmulti parse #(subs %1 0 4))

(defmacro defmapping
  [mapping-name mapping-type & body]
  (let [tp (keyword (string/lower-case
                     mapping-type))]
    `(defmethod ~'parse ~mapping-type
       [~'in-arg]
       ~(merge {:type mapping-type
                :name (keyword mapping-name)}
               (into {}
                     (for [[s# e# k#] body]
                       [(keyword k#) `(subs ~'in-arg
                                            ~s#
                                            ~e#)]))))))

(defmapping header "#123")

(defmapping service-call "SVCL"
  (4 18 customer-name)
  (19 23 customer-id)
  (24 27 call-type-code)
  (28 35 date-of-call-string))

(defmapping usage "USGE"
  (4 8 customer-id)
  (9 22 customer-name)
  (30 30 cycle)
  (31 36 read-date))

(defmethod parse :default
  [& args]
  (throw (ex-info "No matching parser found"
                  {:args args})))

(comment
  (with-open [sample-log (io/reader (io/resource "sample.log"))]
    (doall
     (map parse
          (line-seq sample-log))))
  )
