package mainApp;

public class ObjLivro
{
    String titulo;
    String autor;
    String codgen;
    Integer edicao;
    String editora;
    Integer noPag;
    String iniciais;
    String categ;

    public ObjLivro(String s1, String s2, String s3, Integer i4, String s5, Integer i6, String s7, String s8)
    {
        titulo = s1;
        autor = s2;
        codgen = s3;
        edicao = i4;
        editora = s5;
        noPag = i6;
        iniciais = s7;
        categ = s8;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getCodgen() {
        return codgen;
    }

    public Integer getEdicao() {
        return edicao;
    }

    public String getEditora() {
        return editora;
    }

    public Integer getNoPag() {
        return noPag;
    }

    public String getIniciais() {
        return iniciais;
    }

    public String getCateg() {
        return categ;
    }
}
