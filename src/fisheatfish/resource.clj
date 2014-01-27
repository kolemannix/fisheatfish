(ns fisheatfish.resource
  (:import [java.awt.image BufferedImage]
           [javax.imageio ImageIO]
           [java.io File]))

(defn ^ClassLoader context-class-loader []
  (.getContextClassLoader (Thread/currentThread)))

(defn ^java.awt.image.BufferedImage load-image [resource-name]
  (javax.imageio.ImageIO/read (.getResource (context-class-loader) resource-name)))

(defn scale-image [img factor]
  (let [x (* (.getWidth img) factor)
        y (* (.getHeight img) factor)
        new (BufferedImage. x y BufferedImage/TYPE_INT_ARGB)
        gfx (.createGraphics new)
        _ (.drawImage gfx img 0 0 x y nil)
        __ (.dispose gfx)]
    new))
