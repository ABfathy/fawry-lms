package org.fathy.fawrylms.dto.course;

import java.math.BigDecimal;

public record CourseResponse(Long id, String courseCode, String name,
                             String description, BigDecimal price, Long instructorId) {
}
