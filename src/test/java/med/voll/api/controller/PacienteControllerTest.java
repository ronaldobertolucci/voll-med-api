package med.voll.api.controller;

import med.voll.api.domain.endereco.DadosEndereco;
import med.voll.api.domain.endereco.Endereco;
import med.voll.api.domain.paciente.DadosCadastroPaciente;
import med.voll.api.domain.paciente.DadosDetalhamentoPaciente;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.domain.paciente.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class PacienteControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<DadosCadastroPaciente> dadosCadastroPacienteJson;

    @Autowired
    private JacksonTester<DadosDetalhamentoPaciente> dadosDetalhamentoPacienteJson;

    @MockBean
    private PacienteRepository pacienteRepository;

    @Test
    @DisplayName("Deve devolver código 400 quando informações estão inválidas")
    @WithMockUser
    void cadastrarCenario1() throws Exception {
        var response = mvc.perform(post("/pacientes"))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deve devolver código 201 quando informações estão válidas")
    @WithMockUser
    void cadastrarCenario2() throws Exception {
        Mockito.when(pacienteRepository.save(Mockito.any())).thenReturn(new Paciente(retornarDadosCadastro()));

        var response = mvc.perform(post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosCadastroPacienteJson.write(
                                retornarDadosCadastro()
                        ).getJson())
                )
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Deve devolver código 200")
    @WithMockUser
    void listarCenario1() throws Exception {
        Mockito.when(pacienteRepository.findAllByAtivoTrue(Mockito.any())).thenReturn(Page.empty());
        var response = mvc.perform(get("/pacientes"))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deve devolver código 200 quando informações estão válidas")
    @WithMockUser
    void atualizarCenario1() throws Exception {
        Mockito.when(pacienteRepository.getReferenceById(1L)).thenReturn(
                new Paciente(retornarDadosCadastro()));

        var response = mvc.perform(put("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosDetalhamentoPacienteJson.write(
                                retornarDadosDetalhamento()
                        ).getJson())
                )
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deve devolver código 200 quando informações estão válidas")
    @WithMockUser
    void detalharCenario1() throws Exception {
        Mockito.when(pacienteRepository.getReferenceById(1L)).thenReturn(
                new Paciente(retornarDadosCadastro()));

        var response = mvc.perform(get("/pacientes/{id}", 1L))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deve devolver código 404 quando informações estão inválidas")
    @WithMockUser
    void detalharCenario2() throws Exception {
        var response = mvc.perform(get("/pacientes/{id}", ""))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Deve devolver código 204 quando informações estão válidas")
    @WithMockUser
    void excluirCenario1() throws Exception {
        Mockito.when(pacienteRepository.getReferenceById(1L)).thenReturn(
                new Paciente(retornarDadosCadastro()));

        var response = mvc.perform(delete("/pacientes/{id}", 1L))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Deve devolver código 404 quando informações estão inválidas")
    @WithMockUser
    void excluirCenario2() throws Exception {
        var response = mvc.perform(delete("/pacientes/{id}", ""))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private DadosCadastroPaciente retornarDadosCadastro() {
        return new DadosCadastroPaciente(
                "paciente",
                "email@email.com",
                "11999994444",
                "12345678910",
                new DadosEndereco(
                        "rua teste",
                        "1",
                        null,
                        "bairro",
                        "cidade",
                        "sp",
                        "99999000"));
    }

    private DadosDetalhamentoPaciente retornarDadosDetalhamento() {
        return new DadosDetalhamentoPaciente(
                1L,
                "paciente",
                "email@email.com",
                "11999994444",
                "12345678910",
                new Endereco(
                        "rua teste",
                        "bairro",
                        "99999888",
                        "1",
                        null,
                        "sp",
                        "cidade"));
    }
}