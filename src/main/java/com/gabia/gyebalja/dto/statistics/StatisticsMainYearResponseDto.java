package com.gabia.gyebalja.dto.statistics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@NoArgsConstructor
@ToString
@Getter
public class StatisticsMainYearResponseDto {
    private ArrayList<Long> years;
    private ArrayList<Long> totalEducationTime;
    private ArrayList<Long> totalEducationCount;

    public StatisticsMainYearResponseDto(ArrayList<Long> years, ArrayList<Long> totalEducationTime, ArrayList<Long> totalEducationCount){
        this.years = years;
        this.totalEducationTime = totalEducationTime;
        this.totalEducationCount = totalEducationCount;
    }
}