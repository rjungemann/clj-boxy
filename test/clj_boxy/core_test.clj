(ns clj-boxy.core-test
  (:require [clj-boxy.core :refer :all]
            [clojure.test :refer :all]
            [clout.core :as clout]
            [ring.mock.request :as mock]))

(defn basic-response
  "Returns a successful, plain-text response."
  [body]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body body})

(defn basic-callback
  "Returns a successful, plain-text response wrapped in a callback."
  [body]
  (fn [request] (basic-response body)))

(deftest route-match-test
  (testing "matches a route that exists in the memoized list of routes"
    (let [response-1 (basic-response "Hello, world!")
          response-2 (basic-response "Wow, neat!")
          v-1 (route :get "/" {} [] response-1)
          v-2 (route :get "/foo" {} [] response-2)
          v-3 (route :post "/foo" {} [] response-2)
          request-1 (mock/request :get "/")
          request-2 (mock/request :get "/foo")
          request-3 (mock/request :post "/foo")
          request-4 (mock/request :get "/bar")
          match-1-1 (route-match v-1 request-1)
          match-1-2 (route-match v-1 request-4)
          match-2-1 (route-match v-2 request-1)
          match-2-2 (route-match v-2 request-2)
          match-2-3 (route-match v-3 request-3)]
      (is (= {} (:params match-1-1)))
      (is (= response-1 ((:callback match-1-1) request-1)))
      (is (= nil match-2-1))
      (is (= {} (:params match-2-2)))
      (is (= response-2 ((:callback match-2-2) request-2)))
      (is (= {} (:params match-2-3)))
      (is (= response-2 ((:callback match-2-3) request-2)))
      (is (= nil match-1-2))))

  (testing "passes along the params"
    (let [response (basic-response "Hello, world!")
          v (route :get "/users/:id" {:id #"\d+"} [] response)
          request-1 (mock/request :get "/users/10")
          request-2 (mock/request :get "/users/foo")
          match-1 (route-match v request-1)
          match-2 (route-match v request-2)]
      (is (= {:id "10"} (:params match-1)))
      (is (= ((:callback match-1) request-1) response))
      (is (= nil (:params match-2)))
      (is (= nil (:callback match-2))))))

(deftest route-test
  (testing "Allows for a route to be defined with constraints")
  (testing "Allows for a route to be defined without constraints"))

(deftest routes-test
  (testing "allows for several routes to be defined"))

;; TODO: Add tests for POST, PUT, DELETE, PATCH, OPTIONS, and HEAD.
(deftest GET-test
  (testing "allows for a GET request to be made in a simple way"))
