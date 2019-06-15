package sqlite;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mainApp.Main;
import mainApp.ObjLivro;
import mainApp.ObjSocio;
import mainApp.mainController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;

public class DBOps extends SQLiteJDBC
{

    //Método que faz o login do usuário no sistema, necessário para a tabela Atividades e verificação do sistema
    public static boolean Login(String user, String pass) throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM user WHERE coduser ='" + user + "' AND senha = '" + pass + "';");
            if (rs.next())
            {
                String nome = rs.getString("nome");
                mainApp.Main.setNomeuser(nome);
                return true;
            }
            else
            {
                return false;
            }
        }
        finally
        {
            rs.close();
            stmt.close();
        }
    }

    //Método que Busca no Banco de Dados os Livros que estão locados para uma lista observável
    public static void pegaLocados() throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM locados;");
            while (rs.next())
            {
                int codlivro = rs.getInt("codlivro");
                int codsocio = rs.getInt("codsocio");
                String dtret = rs.getString("dtret");
                String fullrs = "Código do Livro: " + codlivro + "; Código do Sócio: " + codsocio +
                        "; Data de Retorno: " + dtret;
                mainController.addToObListLocados(fullrs);
            }
        }
        finally
        {
            stmt.close();
            rs.close();
        }
    }

    //Método que Busca no Banco de Dados os Sócios da SEHBV para uma lista observável
    public static void pegaSocios() throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM socio WHERE adimp=1;");
            while (rs.next())
            {
                int codsocio = rs.getInt("codsocio");
                String nome = rs.getString("nome");
                String fullsocio = nome + " - Sócio #" + codsocio;
                mainController.addtoObListSocios(fullsocio);
            }
        }
        finally
        {
            stmt.close();
            rs.close();

        }
    }

    //Método que Busca Rápida dos Livros para a aba Locação
    public static HashMap<Integer, String> buscaRapida(String searchKey) throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        HashMap<Integer, String> result = new HashMap<>();

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM livros WHERE titulo LIKE '%" + searchKey +
                    "%' AND codgen !='00.00.00';");
            while (rs.next())
            {
                int codlivro = rs.getInt("codlivro");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                Integer edicao = rs.getInt("edicao");
                String editora = rs.getString("editora");
                String iniciais = rs.getString("iniciais");
                String categ = rs.getString("categ");
                String combo1 = "Livro #" + codlivro + ": " + titulo + "; Autor: " + autor;
                String combo2 = edicao + "&" + editora + "&" + iniciais + "&" + categ;
                mainController.addtoObListRapida(combo1);
                result.put(codlivro, combo2);
            }
        }
        finally
        {
            stmt.close();
            rs.close();
        }
        return result;
    }

    //Método para determinar se livro está locado na tentativa de locação
    public static Boolean locadoOuN(String codl) throws SQLException
    {
        int count = 0;
        boolean result = true;
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM locados WHERE codlivro='" + codl + "';");
            while (rs.next())
            {
                result = false;
                count++;
            }
            if (count < 1) result = true;
        }
        finally
        {
            stmt.close();
            rs.close();
        }
        return result;
    }

    //Método para efetuar Locação de Livro para a aba Locação
    public static void locaLivro(String livro, String socio, String data) throws SQLException
    {
        Statement stmt = null;

        try
        {
            stmt = c.createStatement();
            String query0 = "INSERT INTO locados VALUES (" + livro + ", " + socio + ", '" + data + "');";
            System.out.println(query0);
            stmt.executeUpdate(query0);
            c.commit();
            String query = "INSERT INTO atividade (ativdata, op, usuario, codlivro, codsocio, dataretorno) " +
                    "VALUES ('" + Main.getDate() + "', 'Locação', '" + Main.getNomeuser() + "', " + livro + ", " + socio +
                    ", '" + data + "');";
            System.out.println(query);
            stmt.executeUpdate(query);
            c.commit();

            stmt.close();
            c.close();
        }
        finally
        {
            connect();
        }
    }

    //Método para verificar atraso do livro para a aba Devolução
    public static String verifLivro(String codl) throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        String data_dev;

        try
        {
            System.out.println(codl);
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT dtret FROM locados WHERE codlivro=" + codl + ";");
            data_dev = rs.getString("dtret");

            stmt.close();
            rs.close();

        }
        finally
        {

        }
        return data_dev;
    }

    //Método para efetuar Devolução de Livro para a aba Devolução
    public static void devolveLivro(String codl, double captado) throws SQLException
    {
        Statement stmt = null;

        try
        {
            stmt = c.createStatement();
            stmt.executeUpdate("DELETE FROM locados WHERE codlivro=" + codl + ";");
            c.commit();
            String query = "INSERT INTO atividade (ativdata, op, usuario, codlivro, captado) VALUES ('" + Main.getDate() +
                    "', 'Devolução', '" + Main.getNomeuser() + "', " + codl + ", " + captado + ");";
            stmt.executeUpdate(query);
            c.commit();

            stmt.close();
            c.close();
        }
        finally
        {
            connect();
        }
    }

    //Método que conta os sócios adimplentes para a aba Relatório em Administração
    public static String contaSocio() throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        int total;
        String result;

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT COUNT (*) AS total FROM socio WHERE adimp=1;");
            rs.next();
            total = rs.getInt("total");
            result = Integer.toString(total);
            System.out.println(result);
        }
        finally
        {
            stmt.close();
            rs.close();
        }
        return result;
    }

    //Método que conta os livros do acervo para a aba Relatório em Administração
    public static String contaLivros() throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        int total;
        String result;

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT COUNT (*) AS total FROM livros WHERE codgen !='00.00.00';");
            rs.next();
            total = rs.getInt("total");
            result = Integer.toString(total);
            System.out.println(result);
        }
        finally
        {
            stmt.close();
            rs.close();
        }
        return result;
    }

    //Método que conta o número de livros locados para a aba Relatório em Administração
    public static String contaLocados() throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        int total;
        String result;

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT COUNT (*) AS total FROM locados;");
            rs.next();
            total = rs.getInt("total");
            result = Integer.toString(total);
            System.out.println(result);
        }
        finally
        {
            stmt.close();
            rs.close();
        }
        return result;
    }

    //Método que conta o número de livros atrasados para a aba Relatório em Administração
    public static String contaAtrasados() throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        int count = 0;
        String result;

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM locados;");
            while(rs.next())
            {
                String dtret = rs.getString("dtret");
                LocalDate dataR = LocalDate.parse(dtret, Main.formatter);
                LocalDate hj = Main.dataDeHoje;
                if(dataR.isBefore(hj))
                {
                    count++;
                }
            }
            result = Integer.toString(count);
        }
        finally
        {
            stmt.close();
            rs.close();
        }
        return result;
    }

    //Método para pegar os anos registrados para a aba Relatório em Administração
    public static ObservableList<String> getAnos() throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<String> list = FXCollections.observableArrayList();

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT ativdata FROM atividade;");
            while (rs.next())
            {
                String data = rs.getString("ativdata");
                String anoData = data.substring(data.length() - 4);
                if(!list.contains(anoData))
                {
                    list.add(anoData);
                }
            }
        }
        finally
        {
            stmt.close();
            rs.close();
        }

        return list;
    }

    //Método que recebe estatísticas dos anos para a aba Relatório em Administração
    public static Integer[] pegaStats(String ano) throws SQLException
    {
        Integer[] stats = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0};
        String[] resultStats = new String[12];
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            stmt = c.createStatement();
            String query = "SELECT * FROM atividade WHERE op='Locação' AND ativdata LIKE '%" + ano + "';";
            rs = stmt.executeQuery(query);
            //System.out.println(query);
            while (rs.next())
            {
                String ativdata = rs.getString("ativdata");
                ativdata = ativdata.substring(3,5);
                System.out.println(ativdata);
                switch (ativdata)
                {
                    case "01": stats[0]++; break;
                    case "02": stats[1]++; break;
                    case "03": stats[2]++; break;
                    case "04": stats[3]++; break;
                    case "05": stats[4]++; break;
                    case "06": stats[5]++; break;
                    case "07": stats[6]++; break;
                    case "08": stats[7]++; break;
                    case "09": stats[8]++; break;
                    case "10": stats[9]++; break;
                    case "11": stats[10]++; break;
                    case "12": stats[11]++; break;
                    default:
                        System.out.println("Erro");
                        break;
                }
            }
        }
        finally
        {
            rs.close();
            stmt.close();
        }

        for (int i = 0; i < stats.length; i++)
        {
            resultStats[i] = Integer.toString(stats[i]);
        }

        return stats;
    }

    //Método que recupera os gêneros de livros para a aba Incluir Livro em Administração
    public static HashMap<String, String> pegaGeneros() throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        HashMap<String, String> result = new HashMap<>();

        try
        {
            stmt = c.createStatement();
            rs =stmt.executeQuery("SELECT * FROM genero;");
            while (rs.next())
            {
                String codgen = rs.getString("codgen");
                String genero = rs.getString("genero");
                result.put(genero, codgen);
            }
            rs.close();
        }
        finally
        {
            stmt.close();
        }
        return result;
    }

    //Método para inclusão de livros no Acervo
    public static int IncluiLivro(ObjLivro obj) throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            stmt = c.createStatement();
            stmt.executeUpdate("INSERT INTO livros (titulo, autor, codgen, edicao, editora, nopag, iniciais, categ)" +
                    " VALUES ('" + obj.getTitulo() + "', '" + obj.getAutor() + "', '" + obj.getCodgen() + "', " +
                    obj.getEdicao() + ", '" + obj.getEditora() + "', " + obj.getNoPag() + ", '" + obj.getIniciais() +
                    "', '" + obj.getCateg() + "');");
            c.commit();

            rs = stmt.getGeneratedKeys();
            if (rs.next())
            {
                return rs.getInt(1);
            }
        }
        finally
        {
            rs.close();
            stmt.close();
            c.close();
            connect();
        }
        return 0;
    }

    //Método para conferência da existência de livro
    public static boolean existeLivro(String codl) throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        Boolean b = false;

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM livros WHERE codlivro =" + codl + ";");
            while (rs.next())
            {
                b = true;
            }
            rs.close();
        }
        finally
        {
            stmt.close();
        }
        return b;
    }

    //Método para exlcusão de livros do acervo - em verdade atualização do resgistro para nulificação, caso o livro
    //reapareça ele pode ser atualizado de volta para o mesmo código
    public static void ExcluiLivro(String codigo) throws SQLException
    {
        Statement stmt = null;

        try
        {
            stmt = c.createStatement();
            stmt.executeUpdate("UPDATE livros SET titulo = '00.00.00', autor = '00.00.00', codgen = '00.00.00'," +
                    "edicao = 0, editora = '00.00.00', nopag = 0, iniciais = '00.00.00', categ = 'EXCLUIDO' WHERE " +
                    "codlivro =" + codigo + ";");
            c.commit();

            stmt.close();
            c.close();
        }
        finally
        {
            connect();
        }
    }

    //Método para buscar livro para atualização
    public static ObjLivro MostraLivro(String codl) throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        ObjLivro atualLivro = null;

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM livros WHERE codlivro=" + codl + ";");
            while (rs.next())
            {
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                String codgen = rs.getString("codgen");
                Integer edicao = rs.getInt("edicao");
                String editora = rs.getString("editora");
                Integer nopag = rs.getInt("nopag");
                String iniciais = rs.getString("iniciais");
                String categ = rs.getString("categ");
                atualLivro = new ObjLivro(titulo, autor, codgen, edicao, editora, nopag, iniciais, categ);
            }
            rs.close();
        }
        finally
        {
            stmt.close();
        }
        return atualLivro;
    }

    //Método para efetuar a atualização de um livro
    public static void AtualizaLivro(ObjLivro obj, String codigo) throws SQLException
    {
        Statement stmt = null;

        try
        {
            stmt = c.createStatement();
            stmt.executeUpdate("UPDATE livros SET titulo = '" + obj.getTitulo() + "', autor = '"
                    + obj.getAutor() + "', codgen = '" + obj.getCodgen() + "', edicao = " + obj.getEdicao() +
                    ", editora = '" + obj.getEditora() + "', nopag = " + obj.getNoPag() + ", iniciais = '" +
                    obj.getIniciais() + "', categ = '" + obj.getCateg() + "' WHERE codlivro = " + codigo + ";");
            c.commit();

            stmt.close();
            c.close();
        }
        finally
        {
            connect();
        }
    }

    //Método para inclusão de Sócios nos Registros
    public static int IncluiSocio (ObjSocio obj) throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            stmt = c.createStatement();
            stmt.executeUpdate("INSERT INTO socio (nome, ender, tel1, tel2, email, nasc, desde, adimp, facil, " +
                    "categs, valor) VALUES ('" + obj.getNome() + "', '" + obj.getEnder() + "', " + obj.getTel1() + ", "
                    + obj.getTel2() + ", '" + obj.getEmail() + "', '" + obj.getNasc() + "', '" + Main.getDate() +
                    "', 1, 0, 1, " + obj.getValor() + ");");
            c.commit();

            rs = stmt.getGeneratedKeys();

            if (rs.next())
            {
                //System.out.println(rs.getInt(1));
                return rs.getInt(1);
            }
            rs.close();
        }
        finally
        {

            stmt.close();
            c.close();
            connect();
        }
        return 0;
    }

    //Método para busca de sócio por nome
    public static String buscaPorNome(String chave) throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        String result = "";

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT codsocio, nome FROM socio WHERE nome LIKE '%" + chave + "%';");
            while (rs.next())
            {
                int codsocio = rs.getInt("codsocio");
                String nome = rs.getString("nome");
                result += "Sócio #" + codsocio + ": " + nome + "\n";
            }

            rs.close();
        }
        finally
        {
            stmt.close();
        }
        return result;
    }

    //Método para mostrar sócio para atualização
    public static ObjSocio MostraSocio(String cods) throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        ObjSocio result = null;

        try
        {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM socio WHERE codsocio=" + cods + ";");
            if (rs.next())
            {
                result = new ObjSocio(rs.getString("nome"), rs.getString("ender"),
                        rs.getInt("tel1"), rs.getInt("tel2"), rs.getString("email"),
                        rs.getString("nasc"), rs.getString("desde"),
                        rs.getInt("adimp"), rs.getInt("facil"), rs.getInt("categs"),
                        rs.getInt("valor"));
            }
            rs.close();
        }
        finally
        {
            stmt.close();
        }
        return result;
    }

    //Método para atualizar o registro de sócio
    public static void AtualizaSocio(ObjSocio obj, String codsocio) throws SQLException
    {
        Statement stmt = null;

        try
        {
            stmt = c.createStatement();
            stmt.executeUpdate("UPDATE socio SET nome = '" + obj.getNome() + "', ender = '" + obj.getEnder() +
                    "', tel1 = " + obj.getTel1() + ", tel2 = " + obj.getTel2() + ", email = '" + obj.getEmail() +
                    "', nasc = '" + obj.getNasc() + "', desde = '" + obj.getDesde() + "', adimp = " + obj.getAdimp() +
                    ", facil = " + obj.getFacil() + ", categs = " + obj.getCategs() + ", valor = " + obj.getValor() +
                    " WHERE codsocio = " + codsocio + ";");
            c.commit();

            stmt.close();
            c.close();
        }
        finally
        {
            connect();
        }
    }

}
