(ns dtdt.core
  (:import (java.util TimerTask Timer Date)))

(defn- create-task
  "Internal function, extends abstract TimerTask class"
  ([f] (create-task f nil))
  ([f ex-handler] (let [f-ex (if ex-handler
                               (fn [] (try (f) (catch Exception e (ex-handler e))))
                               f)]
                    (proxy
                      [TimerTask]
                      []
                      (run [] (f-ex))))))

(defn create-timer
  "Creates a new timer (and a corresponding thread) to run tasks"
  []
  (Timer.))

(def default-ex-handler (fn [^Exception e]
                          (println (str "Caught exception in dt-dt: " (.getMessage e)))))

(defn every
  "Create a new task that will execute f every `every-ms` milliseconds.
   first execution will happen immediately unless `initial-delay` or `initial-date` are provided.
  Returns the created task"
  [every-ms f timer & [{:keys [initial-delay initial-date ex-handler]
                        :or   {initial-delay 0
                               initial-date  nil
                               ex-handler    default-ex-handler}}]]

  (let [task (create-task f ex-handler)]
    (if initial-date
      (. ^Timer timer scheduleAtFixedRate ^TimerTask task ^Date initial-date ^long every-ms)
      (. ^Timer timer scheduleAtFixedRate ^TimerTask task ^long initial-delay ^long every-ms))
    task))

(defn in
  "execute f once in `delay-ms` milliseconds"
  [delay-ms f timer & [{:keys [ex-handler]
                        :or   {ex-handler default-ex-handler}}]]
  (let [task (create-task f ex-handler)]
    (. ^Timer timer schedule ^TimerTask task ^long delay-ms)
    task))

(defn last-execution-time
  "Returns last execution time of a task in epoch time"
  [^TimerTask task]
  (. task scheduledExecutionTime))

(defn cancel
  "Cancels a task or a timer"
  [t]
  (condp instance? t
    TimerTask (. ^TimerTask t cancel)
    Timer (. ^Timer t cancel)))
