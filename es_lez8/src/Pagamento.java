public class Pagamento {
    private String dataPagamento;
    private int tipoPagamento;

    public Pagamento(String dataPagamento, int tipoPagamento) {
        this.dataPagamento = dataPagamento;
        this.tipoPagamento = tipoPagamento;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public int getTipoPagamento() {
        return tipoPagamento;
    }

    public String tipoPagamentoToString(){
        String tipo = null;
        switch(this.tipoPagamento){
            case 0 -> tipo = "Bonifico";
            case 1 -> tipo = "Accredito";
            case 2 -> tipo = "Bollettino";
            case 3 -> tipo = "F24";
            case 4 -> tipo = "PagoBancomat";
            default -> {}
        }
        return tipo;
    }
}
