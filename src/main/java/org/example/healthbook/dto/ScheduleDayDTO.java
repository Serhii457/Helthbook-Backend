package org.example.healthbook.dto;

import lombok.Data;
import java.util.List;

@Data
public class ScheduleDayDTO {
    private String day;
    private List<String> times;
}
