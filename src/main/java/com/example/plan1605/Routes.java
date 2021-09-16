package com.example.plan1605;


public final class Routes {

    private Routes() {
        throw new AssertionError("non-instantiable class");
    }

    public static final String API_ROOT = "/api/v1";

    public static final String USERS = API_ROOT + "/users";

    public static final String EVENTS = API_ROOT + "/events";

    public static final String TOKEN = API_ROOT + "/token";

    public static String user(Long id) {
        return USERS + '/' + id;
    }

    public static String event(Long id) {
        return EVENTS + '/' + id;
    }

}

