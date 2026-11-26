package com.curso.jogo2026.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DocumentoFiscalTest {
    @Test void validaCpfComDigitosCorretos() { assertTrue(DocumentoFiscal.cpfValido("52998224725")); }
    @Test void rejeitaCpfComDigitoIncorreto() { assertFalse(DocumentoFiscal.cpfValido("52998224724")); }
    @Test void validaCnpjComDigitosCorretos() { assertTrue(DocumentoFiscal.cnpjValido("11222333000181")); }
    @Test void rejeitaCnpjComDigitoIncorreto() { assertFalse(DocumentoFiscal.cnpjValido("11222333000182")); }
    @Test void rejeitaCpfComDigitosRepetidos() { assertFalse(DocumentoFiscal.cpfValido("11111111111")); }
    @Test void rejeitaCnpjComDigitosRepetidos() { assertFalse(DocumentoFiscal.cnpjValido("11111111111111")); }
}
