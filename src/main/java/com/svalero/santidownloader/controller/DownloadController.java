package com.svalero.santidownloader.controller;

import com.svalero.santidownloader.task.DownloadTask;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

public class DownloadController implements Initializable {

    public TextField tfUrl;
    public Label lbStatus;
    public ProgressBar pbProgress;
    private String urlText;
    private DownloadTask downloadTask;
    private ExecutorService executor;
    private File file;
    private File defaultFile;

    private static final Logger logger = LogManager.getLogger(DownloadController.class);

    public DownloadController(String urlText, File defaultFile, ExecutorService exec) {
        logger.info("Creado: " + urlText);
        this.urlText = urlText;
        this.defaultFile = defaultFile;
        this.executor = exec;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfUrl.setText(urlText);
    }

    @FXML
    public void start(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser(); // Ventana donde eliger un archivo de tu disco duro
            fileChooser.setInitialDirectory(defaultFile);
            file = fileChooser.showSaveDialog(tfUrl.getScene().getWindow());
            if (file == null)
                return;

            downloadTask = new DownloadTask(urlText, file);

            // Actualiza la barra de progreso
            pbProgress.progressProperty().unbind();
            pbProgress.progressProperty().bind(downloadTask.progressProperty());

            // Está pendiente de la barra de progreso y cuando termina lanza un alert informativo
            downloadTask.stateProperty().addListener((observableValue, oldState, newState) -> {
                System.out.println(observableValue.toString());
                if (newState == Worker.State.SUCCEEDED) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("La descarga ha terminado");
                    alert.show();
                }
                if (newState == Worker.State.CANCELLED) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("La descarga se ha cancelado");
                    alert.show();
                }
            });

            downloadTask.messageProperty().addListener((observableValue, oldValue, newValue) -> lbStatus.setText(newValue));
            executor.execute(downloadTask);

        } catch (MalformedURLException murle) {
            murle.printStackTrace();
            logger.error("URL incorrecta", murle.fillInStackTrace());
        }
    }

    @FXML
    public void stop(ActionEvent event) {
        logger.trace("Descarga " + urlText + " detenida");
        stop();
    }

    public void stop() {
        if (downloadTask != null) {
            downloadTask.cancel();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("¿Quieres borrar el archivo?");
            alert.showAndWait();
            ButtonType result = alert.getResult();
            if (result == ButtonType.OK) {
                if (file != null) {
                    file.delete();
                }
                logger.info("Descarga " + urlText + " eliminada");
            }
        }
    }

    public void close() {
        if (downloadTask != null) {
            downloadTask.cancel();
        }
    }

    /*
    public void delete(Tab tab) {
        if (file != null) {
            logger.info("Eliminado: " + urlText);
            file.delete();
        }
        //AppController appController = new AppController();
        //appController.tpDownloads.getTabs().remove(tab);

        //tabsMap.remove(getTabByText(tab.getText()));
    }
    */

    public String getUrlText() {
        return urlText;
    }
}
