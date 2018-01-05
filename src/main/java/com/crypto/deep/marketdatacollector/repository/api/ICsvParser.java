package com.crypto.deep.marketdatacollector.repository.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ICsvParser<T> {

    List<T> readFile(String fileName) throws IOException;

    void writeFile(String fileName);
}
