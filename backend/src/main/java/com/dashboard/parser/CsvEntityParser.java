package com.dashboard.parser;

public interface CsvEntityParser<T> {
    T parse(String[] row);
}