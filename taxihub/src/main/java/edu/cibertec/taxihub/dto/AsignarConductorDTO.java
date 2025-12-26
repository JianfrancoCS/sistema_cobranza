package edu.cibertec.taxihub.dto;

import jakarta.validation.constraints.NotNull;

public record AsignarConductorDTO(
    @NotNull Long autoId,
    @NotNull String empleadoId
) {
}