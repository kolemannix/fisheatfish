(ns fishtest.core)

(def clock-speed 1000) ; clock speed in MS
(def nearness-threshold 5)

(def abilities #{:bite})
(def aggressive-trait-map {:aggression 3 :ability :bite})
(def passive-trait-map {:aggression -3 :ability :bite})

(def sample-position [10.0 10.0])
(defrecord Fish [id position]
  Object
  (toString [this] (str
                "Fish " id ": " position)))

(def aggressive-fish (->Fish 0 [10.0 10.0]))
(def third-fish (->Fish 2 [12.0 10.0]))
(def passive-fish (->Fish 1 [90.0 90.0]))
(def all-fish (atom [aggressive-fish passive-fish third-fish]))

(println (str aggressive-fish))
(println (str passive-fish))

; TODO optimize so there's no Math/sqrt in this
(defn distance [[ax ay] [bx by]]
  (let [a (Math/abs (- ax bx))
        b (Math/abs (- ay by))]
    (Math/sqrt (+ (* a a) (* b b)))))

(defn close? [{apos :position} {bpos :position}]
  (<= (distance apos bpos) nearness-threshold))

(close? aggressive-fish passive-fish)

(defn surroundings
  "Returns a list of the surroundings of the given fish"
  [fish]
  (filter #(close? fish %) @all-fish))

(println (surroundings aggressive-fish))
(surroundings passive-fish)

(defn update-fish [fish]
  )

(defn update-tank
  "Updates the entire world, called at periodic interval"
  []
  (doall (map update-fish @all-fish)))
