# Do this Do that
A clojure periodic task dispatcher

## Why?
We love the simplicity of `at-at` but its **really</b>** old, depends on clojure 1.3.0
and has reflection errors.<br>
Some newer libraries don't quite fit - they require java interop for executing a periodic task.  
<br>
This library is intended to be light, simple to use and execute clojure functions idiomatically.

## Usage
Create a timer - each timer contains a single thread to run tasks
```clojure
(def t (create-timer))
```
Next, assign a task to that timer and it will start running periodically
```clojure
(def some-task (every 5000 #(println "Hello there..") t))
=> #'dtdt.core/some-task
Hello there..
Hello there..
...
```

You can assign as many tasks as you want to a timer, make sure each execution is short otherwise the Timer's thread will be hogged.<br><br>
Tasks can be cancelled
```clojure
Hello there..
Hello there..
(cancel some-task)
=> true
```
Timers can also be cancelled using the same cancel function. Canceling a timer will stop all of its running tasks.<br><br>

### Exceptions
By default when an exception from a task is thrown it will be printed
```clojure
(def bad-task (every 5000 #(throw (Exception. "Something is broken")) t))
Caught exception in dtdt: Something is broken
=> #'dtdt.core/bad-task1
Caught exception in dtdt: Something is broken
Caught exception in dtdt: Something is broken
```

You can pass an exception handler to alter the default behaviour (logging for example)
```clojure
(let [task #(throw (Exception. "Something broke again"))
      ex-handler (fn [e] (timbre/error (.getMessage e)))] 
  (every 2000 task t :ex-handler ex-handler))
```
## License

Copyright © 2020 FIXME
