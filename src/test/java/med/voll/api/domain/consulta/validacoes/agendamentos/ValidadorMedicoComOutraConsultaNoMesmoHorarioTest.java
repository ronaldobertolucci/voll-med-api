package med.voll.api.domain.consulta.validacoes.agendamentos;

import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import med.voll.api.domain.endereco.DadosEndereco;
import med.voll.api.domain.medico.DadosCadastroMedico;
import med.voll.api.domain.medico.Especialidade;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.paciente.DadosCadastroPaciente;
import med.voll.api.domain.paciente.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ValidadorMedicoComOutraConsultaNoMesmoHorarioTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ConsultaRepository repository;

    @Test
    @DisplayName("Deve lançar exceção quando médico já possui consulta no mesmo horário")
    void validarCenario1() {
        // given
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10, 0);
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("Paciente", "paciente@email.com", "00011122233");
        cadastrarConsulta(medico, paciente, proximaSegundaAs10);

        assertThrows(RuntimeException.class, () ->
                new ValidadorMedicoComOutraConsultaNoMesmoHorario(repository)
                        .validar(dadosAgendamentoConsulta(medico.getId(), paciente.getId(), proximaSegundaAs10)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando antes do final da consulta")
    void validarCenario2() {
        // given
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10, 0);
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("Paciente", "paciente@email.com", "00011122233");
        cadastrarConsulta(medico, paciente, proximaSegundaAs10);

        assertThrows(RuntimeException.class, () ->
                new ValidadorMedicoComOutraConsultaNoMesmoHorario(repository)
                        .validar(dadosAgendamentoConsulta(medico.getId(), paciente.getId(), proximaSegundaAs10.plusMinutes(59))));
        assertThrows(RuntimeException.class, () ->
                new ValidadorMedicoComOutraConsultaNoMesmoHorario(repository)
                        .validar(dadosAgendamentoConsulta(medico.getId(), paciente.getId(), proximaSegundaAs10.plusMinutes(60))));
    }

    @Test
    @DisplayName("Deve lançar exceção quando consulta conflitar com início de consulta já existente")
    void validarCenario3() {
        // given
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10, 0);
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("Paciente", "paciente@email.com", "00011122233");
        cadastrarConsulta(medico, paciente, proximaSegundaAs10);

        assertThrows(RuntimeException.class, () ->
                new ValidadorMedicoComOutraConsultaNoMesmoHorario(repository)
                        .validar(dadosAgendamentoConsulta(medico.getId(), paciente.getId(), proximaSegundaAs10.minusMinutes(59))));
        assertThrows(RuntimeException.class, () ->
                new ValidadorMedicoComOutraConsultaNoMesmoHorario(repository)
                        .validar(dadosAgendamentoConsulta(medico.getId(), paciente.getId(), proximaSegundaAs10.minusMinutes(60))));
    }

    @Test
    @DisplayName("Não deve lançar exceção quando consulta não conflitar com consulta já existente")
    void validarCenario4() {
        // given
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10, 0);
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("Paciente", "paciente@email.com", "00011122233");
        cadastrarConsulta(medico, paciente, proximaSegundaAs10);

        assertDoesNotThrow(() ->
                new ValidadorMedicoComOutraConsultaNoMesmoHorario(repository)
                        .validar(dadosAgendamentoConsulta(medico.getId(), paciente.getId(), proximaSegundaAs10.minusMinutes(61))));
        assertDoesNotThrow(() ->
                new ValidadorMedicoComOutraConsultaNoMesmoHorario(repository)
                        .validar(dadosAgendamentoConsulta(medico.getId(), paciente.getId(), proximaSegundaAs10.plusMinutes(61))));
    }

    private void cadastrarConsulta(Medico medico, Paciente paciente, LocalDateTime data) {
        em.persist(new Consulta(null, medico, paciente, data, null));
    }

    private Medico cadastrarMedico(String nome, String email, String crm, Especialidade especialidade) {
        var medico = new Medico(dadosMedico(nome, email, crm, especialidade));
        em.persist(medico);
        return medico;
    }

    private Paciente cadastrarPaciente(String nome, String email, String cpf) {
        var paciente = new Paciente(dadosPaciente(nome, email, cpf));
        em.persist(paciente);
        return paciente;
    }

    private DadosCadastroMedico dadosMedico(String nome, String email, String crm, Especialidade especialidade) {
        return new DadosCadastroMedico(
                nome,
                email,
                "61999999999",
                crm,
                especialidade,
                dadosEndereco()
        );
    }

    private DadosCadastroPaciente dadosPaciente(String nome, String email, String cpf) {
        return new DadosCadastroPaciente(
                nome,
                email,
                "61999999999",
                cpf,
                dadosEndereco()
        );
    }

    private DadosEndereco dadosEndereco() {
        return new DadosEndereco(
                "rua xpto",
                "1",
                null,
                "bairro",
                "cidade xpto",
                "sp",
                "99880111"
        );
    }

    private DadosAgendamentoConsulta dadosAgendamentoConsulta(Long idMedico, Long idPaciente, LocalDateTime data) {
        return new DadosAgendamentoConsulta(
                idMedico,
                null,
                idPaciente,
                data
        );
    }

}