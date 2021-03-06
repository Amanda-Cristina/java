/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controles;

import bancoDados.Bandeira;
import bancoDados.Cartao;
import bancoDados.FormasPagamento;
import classesDao.CartaoDao;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import jpaControles.exceptions.IllegalOrphanException;
import jpaControles.exceptions.NonexistentEntityException;

/**
 * FXML Controller class
 *
 * @author marcos
 */
public class TelaCartaoController implements Initializable {

    @FXML
    private TextField nmrCrt;
   
    @FXML
    private TextField nmCrt;

    @FXML
    private DatePicker dtVldCrt;

    @FXML
    private TextField cnsCrt;
    
    @FXML
    private ToggleButton btCnsltr;
     
    @FXML
    private ToggleButton btNv;
    
    @FXML
    private ComboBox<Bandeira> bdrCrt;

    Bandeira[] listBandeira;
    @FXML
    private Label msgCrt;
    @FXML
    private Label msgCrtSuccess;
    @FXML
    private AnchorPane scCartao;
   

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listBandeira = Bandeira.values();
        bdrCrt.setItems( FXCollections.observableArrayList(listBandeira));
        msgCrt.setStyle("");
    }    

    @FXML
    private void VisualizarTudo(ActionEvent event) {
    }

    @FXML
    private void consultar(ActionEvent event) {
        resetMessage();
        try{
            Cartao cartao = (Cartao) new CartaoDao().consultar(cnsCrt.getText());
            
            nmrCrt.setText((String.valueOf(cartao.getIdCartao())));
            nmCrt.setText(String.valueOf(cartao.getNome()));
            Date data = cartao.getDataValidade();
            dtVldCrt.setValue(data.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
            //bdrCrt.getSelectionModel().select(Bandeira.valueOf(cartao.getBandeira()).ordinal());
            bdrCrt.getSelectionModel().select(Bandeira.valueOf(cartao.getBandeira().toUpperCase()));
            scCartao.setVisible(true);
        }catch (NonexistentEntityException ex) {
             System.out.println(ex.getMessage());
             msgCrt.setText("C??digo n??o encontrado na base " + nmrCrt.getText());
        }
         catch (NumberFormatException e) {
            msgCrt.setText("C??digo deve ser um n??mero inteiro " + nmrCrt.getText());
        }    
    }

    @FXML
    private void inserir(ActionEvent event) {
        scCartao.setVisible(true);
    }

    @FXML
    private void gravar(ActionEvent event)throws AlphaNumException {
        resetMessage();
        try {
            Cartao cartao = new Cartao();

            // Bandeira
            String bandeira = bdrCrt.getValue().getDescricao();
            cartao.setBandeira(bandeira);

            // Validade
            LocalDate dataLocal = dtVldCrt.getValue();
            Date dataValidade = Date.from(dataLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
            cartao.setDataValidade(dataValidade);

            // Nome
            cartao.setNome(nmCrt.getText());

            // N??mero
            String idCartao = nmrCrt.getText();
            boolean idCartaoIsNumeric =  idCartao.matches("[+-]?\\d*(\\.\\d+)?");
            
            if(!idCartaoIsNumeric){
                throw new AlphaNumException(idCartao);
            }
           
            
         
            cartao.setIdCartao(idCartao);


            if (btNv.isSelected()) {
                try {
                    new CartaoDao().inserir(cartao);
                     msgCrtSuccess.setText("Inser????o conclu??da com sucesso!!");
                } catch (Exception ex) {
                     msgCrt.setText("Erro na inser????o do cart??o: " + ex.getMessage());
                }
            } else {
                try {
                    new CartaoDao().editar(cartao);
                } catch (Exception ex) {
                    msgCrt.setText("Erro na altera????o do cart??o: " + ex.getMessage());
                }
            }
        }catch (AlphaNumException ex){
                    msgCrt.setText(ex.getMessage());
     
        } catch (Exception  ex) {            
            System.out.println("Formato de entrada incorreto: " + ex.getMessage());
        }
    }

    @FXML
    private void excluir(ActionEvent event) throws IllegalOrphanException {
        resetMessage();
         try {
            new CartaoDao().excluir(nmrCrt.getText());
             msgCrtSuccess.setText("Exclus??o com sucesso");
        } catch (NonexistentEntityException ex) {
             msgCrt.setText("Erro na exclus??o: " + ex.getMessage());
        } catch (IllegalOrphanException ex) {
             msgCrt.setText("Erro na exclus??o: " + ex.getMessage());
        }
    }

    @FXML
    private void sair(ActionEvent event) {
        resetMessage();
         Platform.exit();
    }

    @FXML
    private void setDateCard(ActionEvent event) {
        
    }

    @FXML
    private void setBandeira(ActionEvent event) {
        bdrCrt.getValue();       
    }
    
    private void resetMessage(){
        msgCrt.setText("");
        msgCrtSuccess.setText("");
    }
    
    
}