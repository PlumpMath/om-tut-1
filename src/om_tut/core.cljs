(ns om-tut.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :as async
             :refer [>! <! put! chan alts!]]
            [goog.events :as events]
            [goog.dom.classes :as classes])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import [goog.events EventType]))


(enable-console-print!)

(def app-state (atom ["Lion" "Zebra" "Buffalo" "Antelope"]))

(defn stripe [text bgc]
  (let [st #js {:backgroundColor bgc}]
    (dom/li #js {:style st} text)))

(defn by-id
  "Short-hand for document.getElementById(id)"
  [id]
  (.getElementById js/document id))


(defn events->chan
  "Given a target DOM element and event type return a channel of
  observed events. Can supply the channel to receive events as third
  optional argument."
  ([el event-type] (events->chan el event-type (chan)))
  ([el event-type c]
   (events/listen el event-type
     (fn [e] (put! c e)))
   c))

(defn show!
  "Given a CSS id and a message string append a child paragraph element
  with the given message string."
  [id msg]
  (let [el (.getElementById js/document id)
        p  (.createElement js/document "p")]
    (set! (.-innerHTML p) msg)
    (.appendChild el p)))


(om/root
 (fn [animals owner]
   (om/component
    (apply dom/ul nil
           (map stripe animals (cycle ["red" "gray"])))))
  app-state
  {:target (by-id "app")})

(defn add-animal []
  (let
    [clicks (events->chan (by-id "btn-add-animal") EventType.CLICK)
     show!  (partial show! "message")]
    (go
     (show! "Waiting for a click ...")
      (<! clicks)
      (show! "Clicked!")
      (let
        [newanimal (by-id "animal-name")]
        (swap! app-state conj (.-value newanimal))))))

(add-animal)


; task: use loop and recur to add animals on every click
; exit the loop when user clicks done!




;(swap! app-state assoc :list ["Lion" "Antelope"])
;(swap! app-state assoc :list ["Lion" "Tiger"])

;;*************************************

;(def app-state (atom {:list ["Lion" "Zebra" "Buffalo" "Antelope"]}))

;(om/root
;  (fn [app owner]
;    (om/component
;      (apply dom/ul #js {:className "animals"}
;        (map (fn [text] (dom/li nil text)) (:list app)))))
;  app-state
;  {:target (. js/document (getElementById "app"))})
