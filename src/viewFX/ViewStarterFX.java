package viewFX;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import model.User;
import model.UserException;
import model.UserManager;
import model.UserNotFoundException;


public class ViewStarterFX extends BorderPane {

	private static final String TXT_user = "user";
	private static final String TXT_name = "name";
	private static final String TXT_action_find = "find";
	private static final String TXT_action_add = "add";
	
	private static final String TXT_spr_list = ":";
	
	private static final String UserPrefix = TXT_user;
	private int userNumber = 1;
	protected static final int OffSetIncrement = 30;
	private int offset = 160; 
	
	public ViewStarterFX() {
		super();
		this.setCenter( this.getUserAdminArea());
		this.setBottom( this.getStatusLabel());
		BorderPane.setMargin( this.getCenter(), new Insets(0,8,3,8));
	}

	private StatusBar statusLabel = null;
	private StatusBar getStatusLabel(){
		if( this.statusLabel == null ){
			this.statusLabel = new StatusBar();
		}
		return this.statusLabel;
	}

	private HBox userAdminArea = null;
	private HBox getUserAdminArea() {
		if( this.userAdminArea == null ) {
			this.userAdminArea = new HBox(10);
			this.userAdminArea.setAlignment(Pos.BASELINE_LEFT);
			this.userAdminArea.setPadding( new Insets( 20,10,10,10 ));
			this.userAdminArea.getChildren().add( this.getNameLabel());
			this.userAdminArea.getChildren().add( this.getNameInput());
			this.userAdminArea.getChildren().add( this.getFindButton());
			this.userAdminArea.getChildren().add( this.getAddButton());			
			HBox.setHgrow(this.getNameInput(), Priority.ALWAYS);
			this.getFindButton().setDefaultButton(true);
		}
		return this.userAdminArea;
	}
	
	private Button addButton = null;
	private Node getAddButton() {
		if (this.addButton == null) {
			this.addButton = new Button( TXT_action_add );
			this.addButton.setTooltip( new Tooltip("Adds an user with name \"user name\", if user does not exist already."));
			this.addButton.setOnAction( new EventHandler<ActionEvent>() {
				public void handle( ActionEvent event) {
					addUserAction( getNameInput().getText());					
				}
			});
		}
		return addButton;
	}

	private String getNextUserName() {
		return UserPrefix + userNumber++;
	}

	private Button findButton = null;
	private Button getFindButton(){
		if (this.findButton == null) {
			this.findButton = new Button( TXT_action_find );
			this.findButton.setTooltip( new Tooltip("Searches for an user with name \"user name\"."));
			this.findButton.setOnAction( new EventHandler<ActionEvent>() {
				public void handle( ActionEvent event) {
					findUserAction( getNameInput().getText());					
				}
			});
		}
		return this.findButton;
	}
	private TextField nameField = null;
	public TextField getNameInput() {
		if( this.nameField == null ){
			this.nameField = new TextField( UserPrefix + userNumber++ );
		}
		return this.nameField;
	}
	private Label nameLabel = null;
	protected Label getNameLabel() {
		if( this.nameLabel == null ){
			this.nameLabel = new Label(TXT_user + " " + TXT_name  + TXT_spr_list + " ");
		}
		return nameLabel;
	}
	private User findUserAction(String name) {
		User result = null;
		try {
			result = UserManager.getTheUserManager().find( name );			
			ViewFX userWindow = new ViewFX( this.offset, result);
			this.offset = this.offset + OffSetIncrement;
			userWindow.sizeToScene();
			userWindow.show();
			
		} catch (UserNotFoundException e) {
			this.showError( e );
		}	
		return result;
	}
	private void addUserAction(String name) {
		try {
			User user = UserManager.getTheUserManager().create(name);
			ViewFX userWindow = new ViewFX( this.offset, user);
			this.offset = this.offset + OffSetIncrement;
			userWindow.sizeToScene();
			userWindow.show();
			this.getNameInput().setText( this.getNextUserName());
		} catch( UserException e){
			this.showError( e );
		}		
	}

	private void showError(UserException e) {
		this.getStatusLabel().showError(e.getMessage());		
	}

}
