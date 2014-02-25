(ns fisheatfish.tank
  (:require [fisheatfish.behaviors.bluefish :as bluefish]))

(def ^{:private true} refresh-interval 1000) ; 1/10 second
(def id-counter (atom 0))
(def cycle-counter (atom 0))
(def max-cycles 5)
(reset! id-counter 0)
(defn- next-id []
  (swap! id-counter inc))

(defn- peek-id [] @id-counter)

(defn- random-position [{[x-max y-max] :size}]
  [(* x-max (.nextDouble (java.util.Random.)))
   (* y-max (.nextDouble (java.util.Random.)))])

(defn- random-aggression [] (.nextDouble (java.util.Random.)))

(defrecord Fish [id aggression position type]
  Object
  (toString [this] (str "Fish " id ": " position)))

(defrecord Tank [size fish-list])


(defn basic-behave "Main update function for a fish"
  [fish tank]
  (let [[old-x old-y] (:position fish)
        new-fish (assoc fish :position [(double (+ old-x 2.0)) (double (+ old-y 2.0))])]
    new-fish))

(def bluefish-behave bluefish/behave)

(defn behavior-lookup [{type :type}]
  (case type
    :basic basic-behave
    :bluefish bluefish-behave))

(defn- add-basic-fish [tank]
  (let [fish (->Fish (next-id) 5 (random-position tank) :basic)] 
    (assoc tank :fish-list (conj (:fish-list tank) fish))))

(defn add-bluefish [tank]
  (let [fish (->Fish (next-id) 5 (random-position tank) :bluefish)]
    (assoc tank :fish-list (conj (:fish-list tank) fish))))

(defn create-tank 
  "Creates a fish tank agent"
  [size]
  (->Tank size (list)))

(defn test-tank "Returns a pre-populated tank for testing"
  [size]
  (-> (create-tank size)
      (add-basic-fish)
      (add-basic-fish)
      (add-basic-fish)
      (add-bluefish)))

(defn- do-cycle
  "Brings the tank to its next state"
  [tank]
  (swap! cycle-counter inc)
  (let [fish-behave (fn [fish] ((behavior-lookup fish) fish tank))
        new-fish-list (doall (map fish-behave (:fish-list tank)))]
    (assoc tank :fish-list new-fish-list)))

(defn- simulation-loop [t callback-fn]
  (loop [tank t]
    (if (> @cycle-counter max-cycles)
      (println "exiting after" max-cycles "cycles")
      (do
        (Thread/sleep refresh-interval)
        (println "doing cycle " @cycle-counter)
        (recur (-> tank
                   do-cycle
                   callback-fn)))))
  )

(defn reset-all []
  (reset! id-counter 0))

(defn begin [t callback]
  "Starts the simulation. Callback is the desired refresh function. Callback should
   expect to receive a FishTank as an argument"
  (do
    (reset! id-counter 0)
    (reset! cycle-counter 0)
    (simulation-loop t callback)))
