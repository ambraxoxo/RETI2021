import java.util.List;

public class Conto {
    private String nomeCliente;
    private List<Pagamento> pagamenti;

    public Conto(String nomeCliente, List<Pagamento> pagamenti) {
        this.nomeCliente = nomeCliente;
        this.pagamenti = pagamenti;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public List<Pagamento> getPagamenti() {
        return pagamenti;
    }
}
