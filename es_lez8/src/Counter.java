public class Counter implements Runnable{
private Conto conto;
private int[] occorrenze;

    public Counter(Conto conto, int[] occorrenze) {
        this.conto = conto;
        this.occorrenze = occorrenze;
    }

    public void run(){
        for(Pagamento p : this.conto.getPagamenti())
            this.occorrenze[p.getTipoPagamento()]++;
    }
}
