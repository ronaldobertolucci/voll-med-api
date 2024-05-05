package med.voll.api.domain.consulta.validacoes.cancelamentos;

import med.voll.api.domain.consulta.*;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ValidadorHorarioAntecedenciaCancelamentoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ConsultaRepository repository;

    @Test
    @DisplayName("Deve lançar exceção quando antecedência for menor do que 24h")
    void validarCenario1() {
        // given
        var amanha = LocalDateTime.now().plusDays(1L); // lança exceção, pois até a assertiva é menor que 24h
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("Paciente", "paciente@email.com", "00011122233");
        var consultaAmanha = cadastrarConsulta(medico, paciente, amanha);

        assertThrows(RuntimeException.class, () ->
                new ValidadorHorarioAntecedenciaCancelamento(repository)
                        .validar(dadosCancelamentoConsulta(consultaAmanha.getId())));
    }

    @Test
    @DisplayName("Não deve lançar exceção quando antecedência for maior do que 24h")
    void validarCenario2() {
        // given
        var depoisDeAmanha = LocalDateTime.now().plusDays(2L);
        var amanhaMais1Minuto = LocalDateTime.now().plusDays(1L).plusMinutes(1);
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("Paciente", "paciente@email.com", "00011122233");
        var consultaAmanha1H = cadastrarConsulta(medico, paciente, amanhaMais1Minuto);
        var consultaDepoisDeAmanha = cadastrarConsulta(medico, paciente, depoisDeAmanha);

        assertDoesNotThrow(() ->
                new ValidadorHorarioAntecedenciaCancelamento(repository)
                        .validar(dadosCancelamentoConsulta(consultaAmanha1H.getId())));
        assertDoesNotThrow(() ->
                new ValidadorHorarioAntecedenciaCancelamento(repository)
                        .validar(dadosCancelamentoConsulta(consultaDepoisDeAmanha.getId())));
    }

    private Consulta cadastrarConsulta(Medico medico, Paciente paciente, LocalDateTime data) {
        return em.persist(new Consulta(null, medico, paciente, data, null));
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

    private DadosCancelamentoConsulta dadosCancelamentoConsulta(Long idConsulta) {
        return new DadosCancelamentoConsulta(
                idConsulta,
                MotivoCancelamento.PACIENTE_DESISTIU
        );
    }
}