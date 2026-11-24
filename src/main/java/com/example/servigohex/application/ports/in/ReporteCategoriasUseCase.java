package com.example.servigohex.application.ports.in;

import java.time.LocalDate;

public interface ReporteCategoriasUseCase {

    void reportarEstadoCatalogo(LocalDate fechaCorte);
}
