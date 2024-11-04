package com.huang.crawler;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InMemoryCookieJar implements CookieJar {
    private final List<Cookie> cookieStore = new ArrayList<>();

    @Override
    public void saveFromResponse(@NotNull HttpUrl url, List<Cookie> cookies) {
        cookieStore.addAll(cookies);
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
        List<Cookie> validCookies = new ArrayList<>();
        for (Cookie cookie : cookieStore) {
            if (cookie.matches(url)) {
                validCookies.add(cookie);
            }
        }
        return validCookies;
    }
    public void clear() {
        cookieStore.clear();
    }
}
