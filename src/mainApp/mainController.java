package mainApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import sqlite.DBOps;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.control.SelectionMode.SINGLE;

public class mainController implements Initializable
{

    //TabPane que contém todas as abas da aplicação
    @FXML
    protected TabPane tabsMain;


    //Elementos sobre os quais se opera em Página Inicial
    @FXML
    private TextField datadia;
    @FXML
    private ListView<String> viewLocadosIni;
    private static ObservableList<String> locados = FXCollections.observableArrayList();


    //Elementos sobre os quais se opera em Locação
    @FXML
    private ListView<String> viewSociosLoc;
    private static ObservableList<String> socios = FXCollections.observableArrayList();

    //Método que lida com seleção de sócios
    public void handleListaSocios(MouseEvent mouseEvent)
    {
        String cods = viewSociosLoc.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", "");
        codSLoc.setText(cods);
        System.out.println(viewSociosLoc.getSelectionModel().getSelectedItems().toString().replaceAll("[^0-9]", ""));
    }
    //Método que lida com seleção de livros
    public void handleListaBusca(MouseEvent mouseEvent)
    {
        String[] aux = viewBusca.getSelectionModel().getSelectedItem().split(":");
        String codl = aux[0].replaceAll("[^0-9]", "");
        codLLoc.setText(codl);
        String[] aux2 = buscados.get(Integer.parseInt(codl)).split("&");
        lblEdicao.setText(aux2[0]);
        lblEditora.setText(aux2[1]);
        lblIniciais.setText(aux2[2]);
        lblCateg.setText(aux2[3]);
    }
    @FXML
    private TextField codLLoc;
    @FXML
    private TextField codSLoc;
    @FXML
    private Button btLoca;
    @FXML
    private Button btBusca;
    @FXML
    private DatePicker dataRetL;
    @FXML
    private TextField chaveBusca;
    @FXML
    private ListView<String> viewBusca;
    private static ObservableList<String> buscaFast = FXCollections.observableArrayList();
    private HashMap<Integer, String> buscados;

    @FXML
    private Label lblEdicao, lblEditora, lblIniciais, lblCateg;


    //Elementos sobre os quais se opera em Devolução
    @FXML
    private ListView<String> viewLocadosDev;
    @FXML
    private Button btDev;


    //Elementos sobre os quais se opera em Administração -> Relatório
    @FXML
    private Label noAdimp;
    @FXML
    private Label noTotal;
    @FXML
    private Label noLocados;
    @FXML
    private Label noAtrasados;
    @FXML
    private ChoiceBox<String> choiceAno;
    private static ObservableList<String> anos = FXCollections.observableArrayList();
    @FXML
    private BarChart chartLocados;
    XYChart.Series mesesLocados = new XYChart.Series();


    //Elementos sobre os quais se opera em Administração -> Incluir Livro
    @FXML
    private TextField tituloIncL, autorIncL, generoIncL, edicaoIncL, editoraIncL, nopagIncL, inicIncL;
    @FXML
    private ChoiceBox<String> categIncL;
    private static HashMap<String, String> genECodgen;
    private static ObservableList<String> generos = FXCollections.observableArrayList();
    @FXML
    private Button btIncL;
    private ObjLivro novoLivro;


    //Elementos sobre os quais se opera em Administração -> Excluir Livro
    @FXML
    private TextField codLExL;
    @FXML
    private Button btExL;


    //Elementos sobre os quais se opera em Administração -> Atualizar Livro
    @FXML
    private TextField codLAtL;
    @FXML
    private Button btMostraAtL;
    @FXML
    private TextField tituloAtL, autorAtL, generoAtL, edicaoAtL, editoraAtL, nopagAtL, inicAtL;
    @FXML
    private ChoiceBox<String> categAtL;
    @FXML
    private Button btFazAtL;

    //Elementos sobre os quais se opera em Administração -> Incluir Sócio
    @FXML
    private TextField nomeIncS, endIncS, tel1IncS, tel2IncS, emailIncS, nascIncS, contribIncS;
    @FXML
    private Button btIncS;
    private ObjSocio novoSocio;

    //Elementos sobre os quais se opera em Administração -> Atualizar Sócio
    @FXML
    private TextField buscaNomeAtS, codSAtS, nomeSAtS, endAtS, tel1AtS, tel2AtS, emailAtS, nascAtS, desdeAtS, contrAtS;
    @FXML
    private ChoiceBox<String> adimpAtS, facilAtS, categAtS;
    private ObservableList<String> simNao = FXCollections.observableArrayList();
    private ObservableList<String> categs = FXCollections.observableArrayList();
    @FXML
    private Button btBuscaAtS, btMostraAtS, btFazAtS;
    private ObjSocio dummySocio;


//TODO//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//TODO//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//TODO//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//TODO//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //Inicialização dos campos da Tab Inicial
        datadia.setText(Main.getDate());
        try
        {
            DBOps.pegaLocados();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        viewLocadosIni.setItems(locados);

        //Inicialização dos campos da Tab Locação
        try
        {
            DBOps.pegaSocios();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        viewSociosLoc.setItems(socios.sorted());
        viewSociosLoc.getSelectionModel().setSelectionMode(SINGLE);
        //System.out.printf("Text field: %b%n", codSLoc); Fica no código por raiva mesmo
        btLoca.setOnAction(event ->
                actionLoca());
        viewBusca.setItems(buscaFast);
        viewBusca.getSelectionModel().setSelectionMode(SINGLE);
        btBusca.setOnAction(event ->
        {
            System.out.println("Busca Rápida");
            limpaRapida();
            try
            {
                buscados = DBOps.buscaRapida(chaveBusca.getText());
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        });
        dataRetL.setOnAction(event ->
                System.out.println(Main.formatter.format(dataRetL.getValue())));

        //Inicialização dos campos da Tab Devolução
        viewLocadosDev.setItems(locados);
        btDev.setOnAction(event ->
        {
            try
            {
                actionDevolve();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        });

        //Inicialização dos campos da Tab Administração
        //Aba Relatório:
        // # Adimplentes
        try
        {
            setRelatorioSocios();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        //# Livros
        try
        {
            setLivrosAcervo();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        //# Locados
        try
        {
            setLivrosLocados();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        //# Atrasados
        try
        {
            setLivrosAtrasados();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        //Tabela estatística
        try
        {
            anos = DBOps.getAnos();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        choiceAno.setItems(anos);
        choiceAno.getSelectionModel().selectLast();
        choiceAno.setOnAction(event ->
        {
            try
            {
                Integer[] stats = DBOps.pegaStats(choiceAno.getSelectionModel().getSelectedItem());
                fillChart(stats);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            mesesLocados.setName(choiceAno.getSelectionModel().getSelectedItem());
        });
        Integer[] stats = new Integer[0];
        try
        {
            stats = DBOps.pegaStats(Integer.toString(Main.dataDeHoje.getYear()));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        fillChart(stats);
        chartLocados.getData().add(mesesLocados);
        chartLocados.setAnimated(false);

        //Aba Incluir Livro
        try
        {
            genECodgen = DBOps.pegaGeneros();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        edicaoIncL.setText("0");
        nopagIncL.setText("0");
        generos.addAll(genECodgen.keySet());
        categIncL.setItems(generos);
        categIncL.setOnAction(event -> generoIncL.setText(
                genECodgen.get(categIncL.getSelectionModel().getSelectedItem())));
        btIncL.setOnAction(event ->
        {
            if (checkIncLFields())
            {
                novoLivro = actionIncluiLivro();
                try
                {
                    int codNovoLivro = DBOps.IncluiLivro(novoLivro);
                    alertaNovoLivro(codNovoLivro);
                    setLivrosAcervo();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }

        });

        //Aba Excluir Livro
        btExL.setOnAction(event ->
        {
            actionExcluiLivro();
            try
            {
                setLivrosAcervo();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        });

        //Aba Atualiza Livro
        categAtL.setItems(generos);
        categAtL.setOnAction(event -> generoAtL.setText(
                genECodgen.get(categAtL.getSelectionModel().getSelectedItem())));
        btMostraAtL.setOnAction(event -> actionAtualizaLivro());
        btFazAtL.setOnAction(event ->
        {
            if(checkAtLFields())
            {
                ObjLivro aAtualizar = GetLivroAtualizar();
                try
                {
                    DBOps.AtualizaLivro(aAtualizar, codLAtL.getText());
                    alertaAtualizado();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }

        });

        //Aba Incluir Sócio
        tel1IncS.setText("0");
        tel2IncS.setText("0");
        contribIncS.setText("0");
        btIncS.setOnAction(event ->
        {
            if (checkIncSFields())
            {
                novoSocio = actionIncluiSocio();
                try
                {
                    int codNovoSocio = DBOps.IncluiSocio(novoSocio);
                    alertaNovoSocio(codNovoSocio);
                    setRelatorioSocios();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        });

        //Aba Atualizar Sócio
        simNao.addAll("Sim", "Não");
        adimpAtS.setItems(simNao);
        facilAtS.setItems(simNao);
        categs.addAll("Colaborador", "Efetivo");
        categAtS.setItems(categs);
        btBuscaAtS.setOnAction(event ->
        {
            try
            {
                System.out.println(buscaNomeAtS.getText());
                String acha = DBOps.buscaPorNome(buscaNomeAtS.getText());
                mostraResulBusca(acha);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        });
        btMostraAtS.setOnAction(event ->
        {
            try
            {
                dummySocio = DBOps.MostraSocio(codSAtS.getText());
                SetSocioAtualizar(dummySocio);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        });
        btFazAtS.setOnAction(event ->
        {
            try
            {
                DBOps.AtualizaSocio(GetSocioAtualizar(), codSAtS.getText());
                alertaAtualizado();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        });

    }

//TODO//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//TODO//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//TODO//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//TODO//////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //Método para adicionar registros à lista de Locados
    public static void addToObListLocados(String string)
    {
        if (!locados.contains(string))
        {
            locados.add(string);
        }
    }

    //Método para adicionar registros à lista de Sócios
    public static void addtoObListSocios(String string)
    {
        if(!socios.contains(string))
        {
            socios.add(string);
        }
    }

    //Método para limpar lista de Busca Rápida
    private static void limpaRapida()
    {
        buscaFast.clear();
    }

    //Método para adicionar registros à lista de Busca Rápida
    public static void addtoObListRapida(String string)
    {
        buscaFast.add(string);
    }

    //Método para Locar um livro, deixando mais limpo a "initialize"
    private void actionLoca()
    {
        System.out.println("Locar Livro");
        if(codLLoc.getText().trim().isEmpty() || codSLoc.getText().trim().isEmpty() ||
                dataRetL.getValue().toString().trim().isEmpty())
        {
            Alert incompleto = new Alert(Alert.AlertType.ERROR);
            incompleto.setTitle("Informações Insuficientes");
            incompleto.setHeaderText(null);
            incompleto.setContentText("Os campos não estão corretamente preenchidos. Por favor se certifique de " +
                    "que o Código do Livro, Código do Sócio e Data de Devuloção estejam preenchidos.");
            incompleto.showAndWait();
        }
        String codlivro = codLLoc.getText().trim();
        String codsocio = codSLoc.getText().trim();
        String datartrn = Main.formatter.format(dataRetL.getValue());
        try
        {
            if(!DBOps.locadoOuN(codlivro))
            {
                Alert livroLocado = new Alert(Alert.AlertType.ERROR);
                livroLocado.setTitle("Livro Locado!");
                livroLocado.setHeaderText(null);
                livroLocado.setContentText("Este livro está Locado no momento.");
                livroLocado.showAndWait();
            }
            else
            {
                DBOps.locaLivro(codlivro, codsocio, datartrn);
                DBOps.pegaLocados();
                Alert yay = new Alert(Alert.AlertType.INFORMATION);
                yay.setTitle("Locação Efetuada");
                yay.setHeaderText("Data para devolução: " + Main.formatter.format(dataRetL.getValue()));
                yay.setContentText(null);
                yay.showAndWait();
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //Método para devolver um Livro, deixando mais limpa a "initialize"
    private void actionDevolve() throws SQLException
    {
        double multa = 0;
        long atraso = 0;

        if (viewLocadosDev.getSelectionModel().getSelectedItem() !=  null)
        {
            String dataRet = null;
            String codDevolve = viewLocadosDev.getSelectionModel().getSelectedItem();
            //String copiaDevolve = codDevolve;
            String[] tratamento = codDevolve.trim().split(";");
            codDevolve = tratamento[0].replaceAll("[^0-9.]", "");
            try
            {
                dataRet = DBOps.verifLivro(codDevolve);
                System.out.println(dataRet);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            assert dataRet != null;
            LocalDate dtRet = LocalDate.parse(dataRet, Main.formatter);
            atraso = ChronoUnit.DAYS.between(dtRet, Main.dataDeHoje);
            System.out.println(atraso);
            if (atraso > 0)
            {
                multa = atraso * 0.5;
                Alert temMulta = new Alert(Alert.AlertType.WARNING);
                temMulta.setTitle("Devolução com atraso!");
                temMulta.setHeaderText("Multa por atraso de R$ " + multa);
                temMulta.setContentText("Livro atrasado. Só aceite devolução após comprovação " +
                        "do pagamento da multa.");
                temMulta.showAndWait();
            }
            else
            {
                Alert noPrazo = new Alert(Alert.AlertType.INFORMATION);
                noPrazo.setTitle("Devolução no Prazo");
                noPrazo.setHeaderText(null);
                noPrazo.setContentText("Livro entregue dentro do prazo. Prossiga com a operação.");
                noPrazo.showAndWait();
            }
            Alert devolve = new Alert(Alert.AlertType.CONFIRMATION);
            devolve.setTitle("Finalizar operação");
            devolve.setHeaderText(null);
            devolve.setContentText("Prosseguir com a Devolução?");
            Optional<ButtonType> result = devolve.showAndWait();
            ButtonType button = result.orElse(ButtonType.CANCEL);
            if (button == ButtonType.OK)
            {
                try
                {
                    DBOps.devolveLivro(codDevolve, multa);
                    limpaLocados();
                    DBOps.pegaLocados();

                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        setLivrosLocados();
        setLivrosAtrasados();
    }

    //Método para limpar os locados depois da Devolução
    private void limpaLocados()
    {
        locados.clear();
    }

    //Método para mostrar corretamente o número de sócios adimplentes
    private void setRelatorioSocios() throws SQLException
    {
        String noSocios;

        noSocios = DBOps.contaSocio();
        noAdimp.setText(noSocios);
    }

    //Método para mostrar corretamente o número de livros no acervo
    private void setLivrosAcervo() throws SQLException
    {
        String noLivros;

        noLivros = DBOps.contaLivros();
        noTotal.setText(noLivros);
    }

    //Método para mostrar corretamente o número de livros locados
    private void setLivrosLocados() throws SQLException
    {
        String noLoc;

        noLoc = DBOps.contaLocados();
        noLocados.setText(noLoc);
    }

    //Método para mostrar corretamente o número de livros atrasados
    private void setLivrosAtrasados() throws SQLException
    {
        String noAtr;

        noAtr = DBOps.contaAtrasados();
        noAtrasados.setText(noAtr);
    }

    //Método para prencher a tabela de estatísticas
    private void fillChart(Integer[] dados)
    {
        String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        mesesLocados.getData().clear();
        for (int i = 0; i < dados.length; i++)
        {
            mesesLocados.getData().add(new XYChart.Data(meses[i], dados[i]));
            System.out.println(i);
        }
    }

    //Método para garantir que campos obrigatórios estejam preenchidos para inclusão de livro
    private boolean checkIncLFields()
    {
        if (tituloIncL.getText().isEmpty() || autorIncL.getText().isEmpty() || generoIncL.getText().isEmpty() ||
                inicIncL.getText().isEmpty())
        {
            Alert incompleto = new Alert(Alert.AlertType.ERROR);
            incompleto.setTitle("Campos Incompletos!");
            incompleto.setHeaderText(null);
            incompleto.setContentText("Certifique-se de que os campos \"Titulo\", \"Autor\", \"Categoria\" e " +
                    "\"Iniciais\" estejam preenchidos para a inclusão do livro. Caso contrário, " +
                    "podem ocorrer erros na operação. Preencha o máximo de campos que conseguir.");
            incompleto.showAndWait();
            return false;
        }
        else return true;
    }

    //Método para iniciar a inclusão de livros
    private ObjLivro actionIncluiLivro()
    {
        ObjLivro newLivro = new ObjLivro(tituloIncL.getText(), autorIncL.getText(), generoIncL.getText(),
                Integer.parseInt(edicaoIncL.getText()), editoraIncL.getText(), Integer.parseInt(nopagIncL.getText()),
                inicIncL.getText(), categIncL.getValue());
        return newLivro;
    }

    //Método para confirmar inclusão de livro
    private void alertaNovoLivro(int codlivro)
    {
        Alert novoLivro = new Alert(Alert.AlertType.INFORMATION);
        novoLivro.setTitle("livro Incluído no Acervo!");
        novoLivro.setHeaderText(String.format("Código do novo livro: %d", codlivro));
        novoLivro. setContentText("Livro foi incluído com sucesso!");
        novoLivro.showAndWait();
    }

    //Método geral para informar usuário de campos vazios importantes
    private void campoVazio() {
        Alert campoVazio = new Alert(Alert.AlertType.ERROR);
        campoVazio.setTitle("Campo Vazio");
        campoVazio.setHeaderText(null);
        campoVazio.setContentText("Por favor insira o código do livro a ser excluído.");
        campoVazio.showAndWait();
    }

    //Método para iniciar a exclusão de livros
    private void actionExcluiLivro()
    {
        if (codLExL.getText().isEmpty())
        {
            campoVazio();
        }
        else
        {
            try
            {
                if (DBOps.existeLivro(codLExL.getText()))
                {
                    Alert certeza = new Alert(Alert.AlertType.CONFIRMATION);
                    certeza.setTitle("Confirme a operação");
                    certeza.setHeaderText("Você quer prosseguir com a exclusão do livro?");
                    certeza.setContentText("A exclusão pode ser revertida por meio de atualização do " +
                            "registro posteriormente.");
                    Optional<ButtonType> result = certeza.showAndWait();
                    ButtonType button = result.orElse(ButtonType.CANCEL);
                    if (button == ButtonType.OK)
                    {
                        DBOps.ExcluiLivro(codLExL.getText());
                        Alert livroEx = new Alert(Alert.AlertType.INFORMATION);
                        livroEx.setTitle("Livro Excluído");
                        livroEx.setHeaderText(null);
                        livroEx.setContentText("Operação realizada com sucesso.");
                        livroEx.showAndWait();
                    }
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    //Método para mostrar livro para a atualização
    private void SetLivroAtualizar(ObjLivro obj)
    {
        tituloAtL.setText(obj.getTitulo());
        autorAtL.setText(obj.getAutor());
        generoAtL.setText(obj.getCodgen());
        edicaoAtL.setText(obj.getEdicao().toString());
        editoraAtL.setText(obj.getEditora());
        nopagAtL.setText(obj.getNoPag().toString());
        inicAtL.setText(obj.getIniciais());
        categAtL.setValue(obj.getCateg());
    }

    //Método para pegar dados para atualização
    private ObjLivro GetLivroAtualizar()
    {
        ObjLivro aAtualizar = null;
        aAtualizar = new ObjLivro(tituloAtL.getText(), autorAtL.getText(), generoAtL.getText(),
                Integer.parseInt(edicaoAtL.getText()), editoraAtL.getText(), Integer.parseInt(nopagAtL.getText()),
                inicAtL.getText(), categAtL.getValue());
        return aAtualizar;
    }

    //Método para iniciar a atualizaçào de livros
    private void actionAtualizaLivro()
    {
        if (codLAtL.getText().isEmpty())
        {
            campoVazio();
        }
        try
        {
            ObjLivro mostraLivro = DBOps.MostraLivro(codLAtL.getText());
            if (mostraLivro != null)
                SetLivroAtualizar(mostraLivro);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //Método para garantir que campos obrigatórios estejam preenchidos para a atualização
    private boolean checkAtLFields()
    {
        if (tituloAtL.getText().isEmpty() || autorAtL.getText().isEmpty() || generoAtL.getText().isEmpty() ||
                inicAtL.getText().isEmpty())
        {
            Alert incompleto = new Alert(Alert.AlertType.ERROR);
            incompleto.setTitle("Campos Incompletos!");
            incompleto.setHeaderText(null);
            incompleto.setContentText("Certifique-se de que os campos \"Titulo\", \"Autor\", \"Categoria\" e " +
                    "\"Iniciais\" estejam preenchidos para a atualização do livro. Caso contrário, " +
                    "podem ocorrer erros na operação. Preencha o máximo de campos que conseguir.");
            incompleto.showAndWait();
            return false;
        }
        else return true;
    }

    //Método para informar que * foi atualizado com sucesso
    private void alertaAtualizado()
    {
        Alert foi = new Alert(Alert.AlertType.INFORMATION);
        foi.setTitle("Registro Atualizado");
        foi.setHeaderText(null);
        foi.setContentText("Operação realizada com sucesso.");
        foi.showAndWait();
    }

    //Método para iniciar a inclusão de sócios
    private ObjSocio actionIncluiSocio()
    {
        ObjSocio newSocio = new ObjSocio(nomeIncS.getText(), endIncS.getText(), Integer.parseInt(tel2IncS.getText()),
                Integer.parseInt(tel2IncS.getText()), emailIncS.getText(), nascIncS.getText(), "", 0, 0,
                0, Integer.parseInt(contribIncS.getText()));
        System.out.println(newSocio);
        return newSocio;
    }

    //Método para garantir que campos obrigatórios estejam preenchidos para inclusão de sócio
    private boolean checkIncSFields()
    {
        if (nomeIncS.getText().isEmpty() || endIncS.getText().isEmpty() || tel1IncS.getText().isEmpty() ||
                contribIncS.getText().isEmpty())
        {
            Alert incompleto = new Alert(Alert.AlertType.ERROR);
            incompleto.setTitle("Campos Incompletos!");
            incompleto.setHeaderText(null);
            incompleto.setContentText("Certifique-se de que os campos \"Nome do Sócio\", \"Endereço\", " +
                    "\"Tel. 1 (Obrigatório)\" e \"Contribuição\" estejam preenchidos para a atualização do livro. " +
                    "Caso contrário, podem ocorrer erros na operação. Preencha o máximo de campos que conseguir.");
            incompleto.showAndWait();
            return false;
        }
        else return true;
    }

    //Método para informar que sócio fo incluido com sucesso
    private void alertaNovoSocio(int codsocio)
    {
        Alert novoSocio = new Alert(Alert.AlertType.INFORMATION);
        novoSocio.setTitle("Sócio Incluído nos Registros!");
        novoSocio.setHeaderText(String.format("Código do novo Sócio: %d", codsocio));
        novoSocio. setContentText("Sócio foi incluído com sucesso!");
        novoSocio.showAndWait();
    }

    //Método para mostrar resultado da busca por nome de sócio
    private void mostraResulBusca(String string)
    {
        if (string != null)
        {
            Alert mostra = new Alert(Alert.AlertType.INFORMATION);
            mostra.setTitle("Resultado da Busca por Nome");
            mostra.setHeaderText("Sócios encontrados:");
            mostra.setContentText(string);
            mostra.showAndWait();
        }

    }

    //Método para mostrar resultado da busca do sócio para atualização
    private void SetSocioAtualizar(ObjSocio obj)
    {
        nomeSAtS.setText(obj.getNome());
        endAtS.setText(obj.getEnder());
        tel1AtS.setText(obj.getTel1().toString());
        tel2AtS.setText(obj.getTel2().toString());
        emailAtS.setText(obj.getEmail());
        nascAtS.setText(obj.getNasc());
        desdeAtS.setText(obj.getDesde());
        int adimp = obj.getAdimp();
        SwitchAtS(adimp, adimpAtS);
        int facil = obj.getFacil();
        SwitchAtS(facil, facilAtS);
        int categs = obj.getCategs();
        switch(categs)
        {
            case 1: categAtS.setValue("Colaborador");
            case 2: categAtS.setValue("Efetivo");
            default: break;
        }
        contrAtS.setText(obj.getValor().toString());
    }

    //Método auxiliar para o SetSocioAtualizar
    private void SwitchAtS(int i, ChoiceBox<String> box) {
        switch(i)
        {
            case 0: box.setValue("Não"); break;
            case 1: box.setValue("Sim"); break;
            default: break;
        }
    }

    //Método para criar ObjSocio para a atualização no BD
    private ObjSocio GetSocioAtualizar()
    {
        ObjSocio aAtualizar;
        aAtualizar = new ObjSocio(nomeSAtS.getText(), endAtS.getText(), Integer.parseInt(tel1AtS.getText()),
                Integer.parseInt(tel2AtS.getText()), emailAtS.getText(), nascAtS.getText(), desdeAtS.getText(),
                0, 0, 0, Integer.parseInt(contrAtS.getText()));
        switch (adimpAtS.getValue())
        {
            case "Não": aAtualizar.adimp = 0; break;
            case "Sim": aAtualizar.adimp = 1; break;
            default: break;
        }
        switch (facilAtS.getValue())
        {
            case "Não": aAtualizar.facil = 0; break;
            case "Sim": aAtualizar.facil = 1; break;
            default: break;
        }
        switch (categAtS.getValue())
        {
            case "Colaborador": aAtualizar.categs = 1; break;
            case "Efetivo": aAtualizar.categs = 2; break;
            default: break;
        }
        return aAtualizar;
    }
}
