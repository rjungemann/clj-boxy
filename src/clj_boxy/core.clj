(ns clj-boxy.core
  (:require [clout.core :as clout]))

(defn route-match
  "Given memoized routes, a `v`, and a request, maybe return a callback."
  [v request]
  (let [method (first v)
        path (nth v 1)
        callback (last v)
        params (clout/route-matches path request)]
    (if (and params (= method (:request-method request)))
      {:callback callback :params params})))

(defn route-compile
  ""
  [method path constraints args body]
  `[~method ~(clout/route-compile path constraints)
    (fn [request#]
      (let [~args request#] ~@body))])

(defn routes
  "Allows for multiple routes to be specified."
  [& args]
  (fn [request]
    (let [result (some #(route-match % request) args)]
      (if result
        (let [callback (:callback result)
              params (:params result)]
          (callback [request params]))))))

(defmacro route
  ""
  [& arguments]
  (let [method (first arguments)
        path (nth arguments 1)
        constraints-or-args (nth arguments 2)]
    (if (map? constraints-or-args)
      (let [constraints (nth arguments 2)
            args (nth arguments 3)
            body (drop 4 arguments)]
        (route-compile method path constraints args body))
      (let [constraints {}
            args (nth arguments 2)
            body (drop 3 arguments)]
        (route-compile method path constraints args body)))))

(defmacro GET
  "Defines a GET route."
  [& arguments]
  `(route :get ~@arguments))

(defmacro POST
  "Defines a POST route."
  [& arguments]
  `(route :post ~@arguments))

(defmacro PUT
  "Defines a PUT route."
  [& arguments]
  `(route :put ~@arguments))

(defmacro DELETE
  "Defines a DELETE route."
  [& arguments]
  `(route :delete ~@arguments))

(defmacro PATCH
  "Defines a PATCH route."
  [& arguments]
  `(route :patch ~@arguments))

(defmacro OPTIONS
  "Defines a OPTIONS route."
  [& arguments]
  `(route :options ~@arguments))

(defmacro HEAD
  "Defines a HEAD route."
  [& arguments]
  `(route :head ~@arguments))
