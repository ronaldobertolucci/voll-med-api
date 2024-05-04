package med.voll.api.domain.consulta.validacoes.agendamentos;

import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class ValidadorHorarioFuncionamento implements ValidadorAgendamentoDeConsulta {

    public void validar(DadosAgendamentoConsulta dados) {
        var dataConsulta = dados.data();
        boolean ehDomingo = dataConsulta.getDayOfWeek().equals(DayOfWeek.SUNDAY);
        boolean antesDaAberturaDaClinica = dataConsulta.getHour() < 7;
        boolean depoisDoFechamentoDaClinica = dataConsulta.getHour() >= 18;

        if (ehDomingo || antesDaAberturaDaClinica || depoisDoFechamentoDaClinica) {
            throw new RuntimeException("Consulta fora do horário de funcionamento da clínica");
        }
    }

}
