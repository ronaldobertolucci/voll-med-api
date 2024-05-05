package med.voll.api.domain.medico;

import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.endereco.DadosEndereco;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MedicoRepositoryTest {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Deveria devolver Optional.empty quando único médico não está disponível na data")
    void findByEspecialidadeComDataLivreCenario1() {
        // given
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10, 0);
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("Paciente", "paciente@email.com", "00011122233");
        cadastrarConsulta(medico, paciente, proximaSegundaAs10);

        // when
        var medicoLivre = medicoRepository.findByEspecialidadeComDataLivre(Especialidade.CARDIOLOGIA, proximaSegundaAs10);

        // then
        assertEquals( Optional.empty(), medicoLivre);
    }

    @Test
    @DisplayName("Deveria devolver médico quando ele estiver disponível na data")
    void findByEspecialidadeComDataLivreCenario2() {
        // given
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10, 0);
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);

        // when
        var medicoLivre = medicoRepository.findByEspecialidadeComDataLivre(Especialidade.CARDIOLOGIA, proximaSegundaAs10);

        // then
        assertEquals(medico, medicoLivre.orElseThrow());
    }

    @Test
    @DisplayName("Deveria devolver Optional.empty quando médico não está ativo")
    void findAtivoByIdCenario1() {
        // given
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);
        medico.excluir();

        // when
        var medicoLivre = medicoRepository.findAtivoById(medico.getId());

        // then
        assertEquals( Optional.empty(), medicoLivre);
    }

    @Test
    @DisplayName("Deveria devolver Optional.empty quando médico não existe")
    void findAtivoByIdCenario2() {
        // given Nenhum médico cadastrado

        // when
        var medicoLivre = medicoRepository.findAtivoById(1L);

        // then
        assertEquals( Optional.empty(), medicoLivre);
    }

    @Test
    @DisplayName("Deveria devolver médico quando médico existe e está ativo")
    void findAtivoByIdCenario3() {
        // given
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);

        // when
        var medicoLivre = medicoRepository.findAtivoById(medico.getId());

        // then
        assertEquals(medico, medicoLivre.orElseThrow());
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
}