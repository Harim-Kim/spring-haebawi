package haebawi.board.domain.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public class ResponseCommon<D> {
    private final int resultCode;
    private final String message;
    private final D data;
}
