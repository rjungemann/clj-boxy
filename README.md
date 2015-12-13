# clj-boxy

A Clojure library designed to provide as straightforward routing as possible. It
leverages the Clout library for matching, and responds to most HTTP methods. No
extra middleware is included, but it plays well with any middleware you would
like to use.

It's just a regular Ring endpoint, so it can be combined in any way with
existing Ring endpoints and middleware. You can even put Ring endpoints inside
of a Boxy route.

## Usage

### Basics



    (ns ring.example.hello-world
      (:use ring.adapter.jetty
            ring.util.response))

    (defn app [request]
      (boxy/routes
        (boxy/GET "/" [request]
          (prn request)
          {:status 200 :headers {} :body "One"})
        (boxy/POST "/foos/:id" []
          {:status 200 :headers {} :body "Two"})
        (boxy/GET "/users/:id" {:id #"\d+"} [request params]
          (prn request params)
          {:status 200 :headers {} :body "Three"})))

    ; Confirms the routes work as expected.
    (prn (handler {:request-method :get :uri "/"}))
    (prn (handler {:request-method :get :uri "/foos/bar"}))
    (prn (handler {:request-method :get :uri "/users/10"}))
    (prn (handler {:request-method :get :uri "/users/abc"}))

    ; Shows how middleware can be easily nested.
    (defn handler [request]
      (app request))

    (run-jetty handler {:port 8080})

## License

Copyright © 2015 Roger Jungemann

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
