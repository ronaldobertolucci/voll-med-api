package med.voll.api.domain.paciente;

import med.voll.api.domain.endereco.DadosEndereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PacienteRepositoryTest {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Deveria devolver Optional.empty quando paciente não está ativo")
    void findAtivoByIdCenario1() {
        // given
        var paciente = cadastrarPaciente("Pacienete", "paciente@voll.med", "12345678910");
        paciente.excluir();

        // when
        var pacienteLivre = pacienteRepository.findAtivoById(paciente.getId());

        // then
        assertEquals( Optional.empty(), pacienteLivre);
    }

    @Test
    @DisplayName("Deveria devolver Optional.empty quando paciente não existe")
    void findAtivoByIdCenario2() {
        // given - Não existe paciente

        // when
        var pacienteLivre = pacienteRepository.findAtivoById(1L);

        // then
        assertEquals( Optional.empty(), pacienteLivre);
    }

    @Test
    @DisplayName("Deveria devolver paciente quando paciente existe e está ativo")
    void findAtivoByIdCenario3() {
        // given
        var paciente = cadastrarPaciente("Pacienete", "paciente@voll.med", "12345678910");

        // when
        var pacienteLivre = pacienteRepository.findAtivoById(paciente.getId());

        // then
        assertEquals(paciente, pacienteLivre.orElseThrow());
    }

    private Paciente cadastrarPaciente(String nome, String email, String cpf) {
        var paciente = new Paciente(dadosPaciente(nome, email, cpf));
        em.persist(paciente);
        return paciente;
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