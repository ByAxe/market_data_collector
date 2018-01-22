package com.crypto.deep.marketdatacollector.service.api;

import com.crypto.deep.marketdatacollector.core.enums.CSV_Type;

import java.time.LocalDateTime;

public interface ICSVService {

    void writeEverythingIntoCsv(CSV_Type type, LocalDateTime dtBegin, LocalDateTime dtEnd);
}
