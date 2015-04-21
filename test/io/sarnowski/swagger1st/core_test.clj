(ns io.sarnowski.swagger1st.core-test
  (:require [clojure.test :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [io.sarnowski.swagger1st.core :as s1st]
            [ring.util.response :refer :all]
            [clojure.pprint :refer [pprint]]
            [ring.util.response :as r]))

;; test helpers

(defn- request-logging [next-handler]
  (if (System/getenv "PRINT")
    (fn [request]
      (pprint request)
      (let [response (next-handler request)]
        (print response)
        response))
    (fn [request]
      (next-handler request))))

(defn create-app [file & {:keys [security] :or {security {}}}]
  (-> (s1st/context :yaml-cp (str "io/sarnowski/swagger1st/" file))
      (s1st/discoverer)
      (s1st/ring wrap-params)
      (s1st/mapper)
      (s1st/parser)
      (s1st/ring request-logging)
      (s1st/validator)
      (s1st/protector security)
      (s1st/executor)))

(defn noop
  "No operation helper."
  [request]
  (r/response "noop"))
