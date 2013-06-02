package example.sql;

public class Qualquer {
    public int numero;
    public String nome;

    public Qualquer(int numero, String nome) {
        this.numero = numero;
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Qualquer [numero=" + numero + ", nome=" + nome + "]";
    }
}
