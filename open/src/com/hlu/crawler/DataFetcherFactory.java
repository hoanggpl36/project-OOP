package com.hlu.crawler;

public class DataFetcherFactory {

    public static IDataFetcher getFetcher(String type, String source) {
        if ("CSV".equalsIgnoreCase(type)) {
            return new CsvFetcher(source);
        } else if ("MOCK".equalsIgnoreCase(type)) {
            return new MockFetcher();
        }
        throw new IllegalArgumentException("Unknown fetcher type: " + type);
    }
}
