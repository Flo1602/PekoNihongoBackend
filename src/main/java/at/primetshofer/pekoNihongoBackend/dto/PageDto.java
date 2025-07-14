package at.primetshofer.pekoNihongoBackend.dto;

import java.util.List;

public record PageDto<T>(List<T> content, int pageCount) {
}
