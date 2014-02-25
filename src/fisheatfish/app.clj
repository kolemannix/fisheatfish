(ns fisheatfish.app
  (:gen-class)
  (:require [seesaw.core :refer :all]
            [seesaw.graphics :as g]
            [fisheatfish.resource :as resource]
            [fisheatfish.tank :as tank])
  (:import [java.awt AlphaComposite])
  )

(def ^{:private true} golden-ratio 1.61803398875)
(defn- nice-size [desired-height] [(* desired-height golden-ratio) desired-height])

(defn- default-width [] (first (nice-size 300)))
(defn- default-height [] (second (nice-size 300)))

(def blue-fish (resource/scale-image (resource/load-image "blue.png") 0.5))
(def gold-fish (resource/scale-image (resource/load-image "gold.png") 0.5))

(def small-square (g/rect 10 10 10))
(def big-circle (g/ellipse 10 30 100))
(def red-on-black (g/style :foreground :white :background :green))

(def tank-image (atom (g/buffered-image (default-width) (default-height))))

(defn do-paint [c g]
  (.drawImage g @tank-image 0 0 nil)
  )

(defn- image-for-fish [fish]
  (case (:type fish)
    :basic blue-fish
    :bluefish gold-fish))

(defn- draw-fish
  "Draw the given fish to the given graphics context"
  [{[x y] :position :as fish} g]
  (.drawImage g (image-for-fish fish) (int x) (int y) nil))

(defn- clear-rect [g x y w h]
  (doto g 
    (.setComposite (AlphaComposite/getInstance AlphaComposite/CLEAR))
    (.fillRect x y w h)
    (.setComposite (AlphaComposite/getInstance AlphaComposite/SRC_OVER)))
  )

(def tt {:position [1 2]})

(def c (canvas :id :can
               :background :blue
               :paint do-paint))

(def f (frame :title "fishes"
              :size [(default-width) :by (default-height)]
              :content c
              :resizable? false))

(defn- draw-tank
  "Draws the given fish tank to the main buffer, explicitly calls repaint"
  [{[w h] :size :as tank}]
  (let [tank-gfx (.createGraphics @tank-image)
        draw-fn (fn [fish] (draw-fish fish tank-gfx))]
    (do
      (clear-rect tank-gfx 0 0 w h)
      (doall (map draw-fn (:fish-list tank)))
      (repaint! f)
      ;; finally, return the tank
      tank)))

(defn -main [& args]
  (native!)
  (show! f)
  (let [tank (tank/test-tank [(default-width) (default-height)])]
    (tank/begin tank draw-tank))
  )
;; (tank/kill)
(-main [])
