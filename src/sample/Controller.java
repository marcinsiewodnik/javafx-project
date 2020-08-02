package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import sample.model.Album;
import sample.model.Artist;
import sample.model.Datasource;

// architecture of the application

public class Controller {

    @FXML
    private TableView artistTable;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void listArtists(){

        Task<ObservableList<Artist>> task = new Task<ObservableList<Artist>>() {

            // This class will be put in a new thread

            @Override
            public ObservableList<Artist> call() {

                return FXCollections.observableArrayList(Datasource.getInstance().queryArtists(Datasource.ORDER_BY_ASC));
            }
        };

        artistTable.itemsProperty().bind(task.valueProperty());

        progressBar.progressProperty().bind(task.progressProperty());

        progressBar.setVisible(true);

//        It will be executed later

        task.setOnSucceeded(e -> progressBar.setVisible(false));
        task.setOnFailed(e -> progressBar.setVisible(false));

        new Thread(task).start();
    }

    @FXML
    public void listAblumsForArtist(){

        final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();

        if(artist == null){

            System.out.println("NO ARTIST SELECTED");
            return;
        }

        Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() throws Exception {

                return FXCollections.observableArrayList(Datasource.getInstance().queryAlbumsForArtist(artist.getId()));
            }
        };

        artistTable.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
    }

    @FXML
    public void updateArtist(){

        // final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();

        // We're simulating this in order to avoid having to create a dialog that accepts a new name

        final Artist artist = (Artist) artistTable.getItems().get(2);

        Task<Boolean> task = new Task<>() {

            @Override
            protected Boolean call() throws Exception {

                return Datasource.getInstance().updateArtistName(artist.getId(), "New Value");
            }
        };

        new Thread(task).start();

        task.setOnSucceeded(e -> {

            if(task.valueProperty().get()){

                artist.setName("New Value");

                // The refresh() method forces the table view to redraw its visible area -> we have to refresh because of the bug in jdk

                artistTable.refresh();

            }
        });
    }
}

