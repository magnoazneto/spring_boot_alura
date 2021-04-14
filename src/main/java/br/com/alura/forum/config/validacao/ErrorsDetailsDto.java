package br.com.alura.forum.config.validacao;

public class ErrorsDetailsDto {

    private final String field;
    private final String error;

    public ErrorsDetailsDto(String field, String error) {
        this.field = field;
        this.error = error;
    }

    public String getField() {
        return field;
    }

    public String getError() {
        return error;
    }
}
