import de.fhdw.ml.transactionFramework.administration.ObjectAdministration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import viewFX.ViewStarterFX;

public class StarterFX extends Application {
	
	public static void main(String[] args) {
		  launch( args ); 		  //fx thread 
  }

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("User Administration");
		Scene rootScene = new Scene( this.rootPane );		
		primaryStage.setScene( rootScene );
		primaryStage.sizeToScene();
		primaryStage.setX( 5 );
		primaryStage.setY( 5 );
		primaryStage.setOnCloseRequest( new EventHandler<WindowEvent>() {
			public void handle( WindowEvent event) {
				Platform.exit();
				ObjectAdministration.getCurrentAdministration().finaliseApplication();
			    System.exit(0);				
			}
		} );
		primaryStage.show();			
	}
	
	private BorderPane rootPane = null;
	public void init(){
		this.rootPane = new ViewStarterFX();
	}

}
