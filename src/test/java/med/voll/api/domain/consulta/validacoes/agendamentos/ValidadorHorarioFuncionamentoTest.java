package med.voll.api.domain.consulta.validacoes.agendamentos;

import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorHorarioFuncionamentoTest {

    @Test
    @DisplayName("Deve lançar exceção quando domingo")
    void validarCenario1() {
        // given
        var proximoDomingoAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                .atTime(10, 0);

        // then
        assertThrows(RuntimeException.class,
                () -> new ValidadorHorarioFuncionamento().validar(dadosAgendamentoConsulta(proximoDomingoAs10)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando horário for anterior à abertura")
    void validarCenario2() {
        // given
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(6, 59);

        // then
        assertThrows(RuntimeException.class,
                () -> new ValidadorHorarioFuncionamento().validar(dadosAgendamentoConsulta(proximaSegundaAs10)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando horário for igual após 18h")
    void validarCenario3() {
        // given
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(18, 0);

        // then
        assertThrows(RuntimeException.class,
                () -> new ValidadorHorarioFuncionamento().validar(dadosAgendamentoConsulta(proximaSegundaAs10)));
    }

    @Test
    @DisplayName("Não leve lançar exceção quando horário for dentro do funcionamento")
    void validarCenario4() {
        // given
        var proximaQuintaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.THURSDAY))
                .atTime(14, 0);

        // then
        assertDoesNotThrow(
                () -> new ValidadorHorarioFuncionamento().validar(dadosAgendamentoConsulta(proximaQuintaAs10)));
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