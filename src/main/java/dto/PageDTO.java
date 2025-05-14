package dto;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * Custom Data Transfer Object for pagination with only essential information
 */
public record PageDTO<T> (
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean isFirst,
    boolean isLast
) {
    /**
     * Creates a PageDTO from a Spring Page object
     */
    public static <T> PageDTO<T> from(Page<T> page) {
        return new PageDTO<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
} 