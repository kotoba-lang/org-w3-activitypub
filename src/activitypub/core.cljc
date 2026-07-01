(ns activitypub.core
  "EDN constructors for ActivityPub resources.")

(def context-key (keyword "@context"))
(def as-context "https://www.w3.org/ns/activitystreams")
(def public "https://www.w3.org/ns/activitystreams#Public")

(defn actor
  [{:keys [id type inbox outbox followers following liked preferred-username name summary public-key]
    :or {type "Person"}}]
  (cond-> {context-key as-context
           :id id
           :type type
           :inbox inbox
           :outbox outbox}
    followers (assoc :followers followers)
    following (assoc :following following)
    liked (assoc :liked liked)
    preferred-username (assoc :preferredUsername preferred-username)
    name (assoc :name name)
    summary (assoc :summary summary)
    public-key (assoc :publicKey public-key)))

(defn collection
  [{:keys [id type total-items items first last current next prev]
    :or {type "Collection"}}]
  (cond-> {context-key as-context
           :id id
           :type type}
    total-items (assoc :totalItems total-items)
    items (assoc :items (vec items))
    first (assoc :first first)
    last (assoc :last last)
    current (assoc :current current)
    next (assoc :next next)
    prev (assoc :prev prev)))

(defn ordered-collection [opts]
  (collection (assoc opts :type "OrderedCollection")))

(defn ordered-page [opts]
  (collection (assoc opts :type "OrderedCollectionPage")))

(defn endpoints [base]
  {:inbox (str base "/inbox")
   :outbox (str base "/outbox")
   :followers (str base "/followers")
   :following (str base "/following")
   :liked (str base "/liked")})

(defn activity-request
  [{:keys [method url actor object headers body]}]
  {:method method
   :url url
   :actor actor
   :object object
   :headers (or headers {})
   :body body})

(defn errors [x]
  (cond-> []
    (not (map? x)) (conj {:error :activitypub/document-must-be-map})
    (and (map? x) (nil? (:id x))) (conj {:error :activitypub/missing-id})
    (and (map? x) (nil? (:type x))) (conj {:error :activitypub/missing-type})))

(defn validate [x]
  (let [es (errors x)]
    {:valid? (empty? es) :errors es}))
