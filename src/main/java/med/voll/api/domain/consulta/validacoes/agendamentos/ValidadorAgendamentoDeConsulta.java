package med.voll.api.domain.consulta.validacoes.agendamentos;

import med.voll.api.domain.consulta.DadosAgendamentoConsulta;

public interface ValidadorAgendamentoDeConsulta {
    void validar(DadosAgendamentoConsulta dados);
}
