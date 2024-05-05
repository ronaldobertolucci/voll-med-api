package med.voll.api.domain.consulta.validacoes.agendamentos;

import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorHorarioAntecedenciaAgendamentoTest {

    @Test
    @DisplayName("Deve lançar exceção quando horário de agendamento for próximo")
    void validarCenario1() {
        // given
        var dataProxima29Minutos = LocalDateTime.now().plusMinutes(29);
        var dataProxima30Minutos = LocalDateTime.now().plusMinutes(30); // mesmo com 30, vai disparar, pois até a execução será menor do que 30

        // then
        assertThrows(RuntimeException.class,
                () -> new ValidadorHorarioAntecedenciaAgendamento().validar(dadosAgendamentoConsulta(dataProxima29Minutos)));
        assertThrows(RuntimeException.class,
                () -> new ValidadorHorarioAntecedenciaAgendamento().validar(dadosAgendamentoConsulta(dataProxima30Minutos)));
    }

    @Test
    @DisplayName("Não deve lançar exceção quando horário de agendamento for maior que 30 minutos")
    void validarCenario2() {
        // given
        var dataProxima = LocalDateTime.now().plusMinutes(31);

        // then
        assertDoesNotThrow(() -> new ValidadorHorarioAntecedenciaAgendamento().validar(dadosAgendamentoConsulta(dataProxima)));
    }

    private DadosAgendamentoConsulta dadosAgendamentoConsulta(LocalDateTime data) {
        return new DadosAgendamentoConsulta(
                1L,
                null,
                1L,
                data
        );
    }

}