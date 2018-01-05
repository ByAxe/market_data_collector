package com.crypto.deep.marketdatacollector.repository.api;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public abstract class ACsvParser<T> implements ICsvParser<T> {

    protected final Class tClass;
    protected final ObjectMapper mapper;

    public ACsvParser(Class tClass, ObjectMapper mapper) {
        this.tClass = tClass;
        this.mapper = mapper;
    }

    @Override
    public List<T> readFile(String fileName) throws IOException {
        CsvSchema schema = CsvSchema.emptySchema().withoutHeader();

        ObjectReader oReader = mapper.readerFor(tClass).with(schema);

        try (Reader reader = new FileReader(fileName)) {
            MappingIterator<T> mi = oReader.readValues(reader);
            return mi.readAll();
        }
    }

}
