package example.sql;

public class Qualquer {
    public int numero;
    public String nome;
    public String sobrenome;

    public Qualquer(int numero, String nome, String sobrenome) {
        this.numero = numero;
        this.nome = nome;
        this.sobrenome = sobrenome;
    }

    @Override
    public String toString() {
        return "Qualquer [numero=" + numero + ", nome=" + nome + ", sobrenome=" + sobrenome + "]";
    }
}
