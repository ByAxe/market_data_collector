package com.crypto.deep.marketdatacollector.repository.api;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

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

        CsvMapper csvMapper = new CsvMapper();

        try (Reader reader = new FileReader(fileName)) {
            MappingIterator<Map<?, ?>> mappingIterator = csvMapper.readerFor(Map.class)
                    .with(schema).readValues(reader);
            List<Map<?, ?>> list = mappingIterator.readAll();
            return null;
        }
    }

}
