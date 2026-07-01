(ns activitypub.core-test
  (:require [activitypub.core :as ap]
            [clojure.test :refer [deftest is]]))

(deftest builds-actor
  (let [base "https://example.test/users/alice"
        actor (ap/actor (merge {:id base :preferred-username "alice"}
                               (ap/endpoints base)))]
    (is (= "Person" (:type actor)))
    (is (= (str base "/inbox") (:inbox actor)))
    (is (:valid? (ap/validate actor)))))

(deftest builds-ordered-collection
  (let [c (ap/ordered-collection {:id "https://example.test/outbox"
                                  :total-items 1
                                  :items [{:id "urn:activity:1"}]})]
    (is (= "OrderedCollection" (:type c)))
    (is (= 1 (:totalItems c)))))
