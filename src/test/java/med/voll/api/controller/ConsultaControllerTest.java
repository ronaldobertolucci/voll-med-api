package med.voll.api.controller;

import med.voll.api.domain.consulta.*;
import med.voll.api.domain.medico.Especialidade;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.paciente.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class ConsultaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<DadosAgendamentoConsulta> dadosAgendamentoConsultaJson;

    @Autowired
    private JacksonTester<DadosDetalhamentoConsulta> dadosDetalhamentoConsultaJson;

    @Autowired
    private JacksonTester<DadosCancelamentoConsulta> dadosCancelamentoConsultaJson;

    @MockBean
    private AgendaDeConsultas agendaDeConsultas;

    @MockBean
    private ConsultaRepository consultaRepository;

    @Test
    @DisplayName("Deve devolver código 400 quando informações estão inválidas")
    @WithMockUser
    void agendarCenario1() throws Exception {
        var response = mvc.perform(post("/consultas"))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deve devolver código 201 quando informações estão válidas")
    @WithMockUser
    void agendarCenario2() throws Exception {
        var data = LocalDateTime.now().plusHours(1);

        Mockito.when(agendaDeConsultas.agendar(Mockito.any())).thenReturn(retornarConsulta(
                1L, 5L, data
        ));

        var response = mvc.perform(post("/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosAgendamentoConsultaJson.write(
                                new DadosAgendamentoConsulta(1L, Especialidade.CARDIOLOGIA, 5L, data)
                        ).getJson())
                )
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    private Consulta retornarConsulta(Long idMedico, Long idPaciente, LocalDateTime data) {
        var medico = new Medico(idMedico, "nome teste", "email teste", null, null, null, null, false);
        var paciente = new Paciente(idPaciente, "nome teste", "email teste", null, "cpf", null, false);
        return new Consulta(null, medico, paciente, data, null);
    }

    @Test
    @DisplayName("Deve devolver código 400 quando informações estão inválidas")
    @WithMockUser
    void cancelarCenario1() throws Exception {
        var response = mvc.perform(delete("/consultas"))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deve devolver código 204 quando informações estão válidas")
    @WithMockUser
    void cancelarCenario2() throws Exception {
        var response = mvc.perform(delete("/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosCancelamentoConsultaJson.write(
                                new DadosCancelamentoConsulta(1L, MotivoCancelamento.PACIENTE_DESISTIU)
                        ).getJson()))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Deve devolver código 404 quando informações estão inválidas")
    @WithMockUser
    void detalharCenario1() throws Exception {
        var response = mvc.perform(get("/consultas/{id}", ""))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Deve devolver código 200 quando informações estão válidas")
    @WithMockUser
    void detalharCenario2() throws Exception {
        Mockito.when(consultaRepository.getReferenceById(1L)).thenReturn(
                retornarConsulta(1L, 5L, LocalDateTime.now().plusHours(1)));

        var response = mvc.perform(get("/consultas/{id}", 1L))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

}