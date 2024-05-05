package med.voll.api.controller;

import med.voll.api.domain.endereco.DadosEndereco;
import med.voll.api.domain.endereco.Endereco;
import med.voll.api.domain.medico.*;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class MedicoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<DadosCadastroMedico> dadosCadastroMedicoJson;

    @Autowired
    private JacksonTester<DadosDetalhamentoMedico> dadosDetalhamentoMedicoJson;

    @MockBean
    private MedicoRepository medicoRepository;

    @Test
    @DisplayName("Deve devolver código 400 quando informações estão inválidas")
    @WithMockUser
    void cadastrarCenario1() throws Exception {
        var response = mvc.perform(post("/medicos"))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deve devolver código 201 quando informações estão válidas")
    @WithMockUser
    void cadastrarCenario2() throws Exception {
        Mockito.when(medicoRepository.save(Mockito.any())).thenReturn(new Medico(retornarDadosCadastro()));

        var response = mvc.perform(post("/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosCadastroMedicoJson.write(
                                retornarDadosCadastro()
                        ).getJson())
                )
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Deve devolver código 200 quando informações estão válidas")
    @WithMockUser
    void atualizarCenario1() throws Exception {
        Mockito.when(medicoRepository.getReferenceById(1L)).thenReturn(
                new Medico(retornarDadosCadastro()));

        var response = mvc.perform(put("/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosDetalhamentoMedicoJson.write(
                                retornarDadosDetalhamento()
                        ).getJson())
                )
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deve devolver código 200")
    @WithMockUser
    void listarCenario1() throws Exception {
        Mockito.when(medicoRepository.findAllByAtivoTrue(Mockito.any())).thenReturn(Page.empty());
        var response = mvc.perform(get("/medicos"))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deve devolver código 200 quando informações estão válidas")
    @WithMockUser
    void detalharCenario1() throws Exception {
        Mockito.when(medicoRepository.getReferenceById(1L)).thenReturn(
                new Medico(retornarDadosCadastro()));

        var response = mvc.perform(get("/medicos/{id}", 1L))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deve devolver código 404 quando informações estão inválidas")
    @WithMockUser
    void detalharCenario2() throws Exception {
        var response = mvc.perform(get("/medicos/{id}", ""))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Deve devolver código 204 quando informações estão válidas")
    @WithMockUser
    void excluirCenario1() throws Exception {
        Mockito.when(medicoRepository.getReferenceById(1L)).thenReturn(
                new Medico(retornarDadosCadastro()));

        var response = mvc.perform(delete("/medicos/{id}", 1L))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Deve devolver código 404 quando informações estão inválidas")
    @WithMockUser
    void excluirCenario2() throws Exception {
        var response = mvc.perform(delete("/medicos/{id}", ""))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private DadosCadastroMedico retornarDadosCadastro() {
        return new DadosCadastroMedico(
                "medico",
                "email@email.com",
                "11999994444",
                "123456",
                Especialidade.CARDIOLOGIA,
                new DadosEndereco(
                        "rua teste",
                        "1",
                        null,
                        "bairro",
                        "cidade",
                        "sp",
                        "99999000"));
    }

    private DadosDetalhamentoMedico retornarDadosDetalhamento() {
        return new DadosDetalhamentoMedico(
                1L,
                "medico",
                "email@email.com",
                "11999994444",
                "123456",
                Especialidade.CARDIOLOGIA,
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