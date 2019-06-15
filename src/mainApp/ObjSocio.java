package mainApp;

public class ObjSocio
{
    String nome;
    String ender;
    Integer tel1;
    Integer tel2;
    String email;
    String nasc;
    String desde;
    Integer adimp;
    Integer facil;
    Integer categs;
    Integer valor;

    public ObjSocio (String s1, String s2, Integer i3, Integer i4, String s5, String s6, String s7, Integer i8,
                     Integer i9, Integer i10, Integer i11)
    {
        nome = s1;
        ender = s2;
        tel1 = i3;
        tel2 = i4;
        email = s5;
        nasc = s6;
        desde = s7;
        adimp = i8;
        facil = i9;
        categs = i10;
        valor = i11;
    }

    public String getNome() {  return nome; }

    public String getEnder() { return ender;}

    public Integer getTel1() { return tel1; }

    public Integer getTel2() { return tel2; }

    public String getEmail() { return email; }

    public String getNasc() { return nasc; }

    public String getDesde() { return desde; }

    public Integer getAdimp() { return adimp; }

    public Integer getFacil() { return facil; }

    public Integer getCategs() { return categs; }

    public Integer getValor() { return valor; }
}
