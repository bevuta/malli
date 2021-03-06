(ns malli.clj-kondo-test
  (:require [clojure.test :refer [deftest is]]
            [malli.clj-kondo :as clj-kondo]
            [malli.core :as m]
            [malli.util :as mu]))

(def Schema
  (m/schema
    [:map {:registry {::id string?
                      ::price double?}}
     ::id
     [::price {:optional true}]
     [:name string?]
     [:description [:maybe string?]]
     [:tags {:optional true} [:set qualified-keyword?]]
     [::y {:optional true} boolean?]
     [:select-keys [:maybe [:select-keys [:map [:x int?] [:y int?]] [:x]]]]
     [:nested [:merge
               [:map [:id ::id]]
               [:map [:price ::price]]]]
     [:z [:vector [:map-of int? int?]]]]
    {:registry (merge (m/default-schemas) (mu/schemas))}))

(deftest clj-kondo-integration-test
  (is (= {:op :keys,
          :opt {::price :double, :tags :set, ::y :boolean},
          :req {::id :string,
                :name :string,
                :description :nilable/string,
                :select-keys {:op :keys, :req {:x :int}},
                :nested {:op :keys, :req {:id :string, :price :double}},
                :z :vector}}
         (clj-kondo/transform Schema))))
