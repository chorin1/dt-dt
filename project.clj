(defproject dtdt "0.2.0"
  :description "Do this Do that - A clojure function scheduler"
  :url "https://github.com/chorin1/dt-dt"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v20.html"}
  :profiles {:dev       {:dependencies [[org.clojure/clojure "1.10.1"]
                                        [clj-kondo "RELEASE"]]
                         :repl-options {:init-ns dtdt.core}
                         :aliases {"clj-kondo" ["run" "-m" "clj-kondo.main"]
                                   "lint" ["run" "-m" "clj-kondo.main" "--lint" "src" "test"]}
                         :global-vars    {*warn-on-reflection* true}}})
