package viewFX;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import model.AccountException;
import model.AccountManager;
import model.TransferException;
import model.User;
import viewModel.AccountTransactionFacadeView;
import viewModel.AccountView;
import viewModel.EntryView;
import viewModel.TransactionView;
import viewModel.TransferOrTransactionView;
import viewModel.TransferOrTransactionViewVisitor;
import viewModel.TransferView;
import viewModelFX.AccountViewFX;
import viewModelFX.TransactionViewFX;
import viewModelFX.UserViewFX;
import viewModelFX.ViewModelFX;


public class ViewFX extends Stage implements AccountTransactionFacadeView{

	private static final String TXT_account = "Account";
	private static final String TXT_name = "name";

	private static final String TXT_imp_create = "create";
	private static final String TXT_imp_find = "find";

	private static final String TXT_spr_lbl = ": ";

	private static final int Credit = 2;
	private static final int Debit = 1;
	protected static final long StatusBarResetTime = 10000;
	private static final double Vertical_Divider_Pos = 0.3;
	
	private ViewModelFX viewModel;
	
	public ViewFX( int offset, User user ) {
		super( StageStyle.DECORATED);
		this.viewModel = ViewModelFX.create( UserViewFX.create(user, this));
		this.initialize( offset );
	}
	private Scene rootScene = null;
	private void initialize( int offset) {
		this.rootScene = new Scene( this.getRootPane(), 1200, 600);	
		this.setScene( this.rootScene );
		this.sizeToScene();
		this.setX( offset );
		this.setY( offset );
		this.setTitle(this.viewModel.toString());
		this.getRootPane().prefHeightProperty().bind( this.rootScene.heightProperty());
		this.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle( WindowEvent event) {
				viewModel.finaliseYourself();				
			}
		});
	}
	
	private BorderPane rootPane = null;
	private BorderPane getRootPane() {
		if( this.rootPane == null ){
			this.rootPane = new BorderPane();
			this.rootPane.setCenter( this.getMainSplitPane());			
			this.rootPane.setBottom( this.getMainStatusBar() );   		
		}
		return this.rootPane;    	
	}	
	private SplitPane mainSplitPane = null;
	private SplitPane getMainSplitPane() {
		if( this.mainSplitPane == null) {
			this.mainSplitPane = new SplitPane();
			this.mainSplitPane.setOrientation( Orientation.VERTICAL);
			this.mainSplitPane.getItems().addAll( this.getAccountsPanel(), this.getOtherAccountsAndTransactionsPanel() );	
			this.mainSplitPane.setDividerPosition( 0, 0.4 );
			this.mainSplitPane.prefHeightProperty().bind( this.heightProperty());
		}
		return this.mainSplitPane;
	}

	private BorderPane otherAccontsAndTAPanel = null;
	private BorderPane getOtherAccountsAndTransactionsPanel() {
		if( this.otherAccontsAndTAPanel == null ){
			this.otherAccontsAndTAPanel = new BorderPane();
			this.otherAccontsAndTAPanel.setCenter( this.getTransactionsSplitPane());
		}
		return this.otherAccontsAndTAPanel;
	}

	private SplitPane transactionsSplitPane = null;
	private SplitPane getTransactionsSplitPane() {
		if( this.transactionsSplitPane == null ){
			this.transactionsSplitPane = new SplitPane();
			this.transactionsSplitPane.setOrientation(Orientation.HORIZONTAL);
			this.transactionsSplitPane.setDividerPosition(0, Vertical_Divider_Pos);
			TitledPane paneLeft = new TitledPane("Other accounts", this.getOtherAccountsPane() );
			paneLeft.collapsibleProperty().set(false);
			TitledPane paneRight = new TitledPane("Transfers and transactions", this.getTransactionsAndTransfersPanel() );
			paneRight.collapsibleProperty().set(false);
			this.transactionsSplitPane.getItems().addAll( paneLeft, paneRight );
		}
		return this.transactionsSplitPane;
	}

	private BorderPane transactionsAndTransfersPanel = null;
	private BorderPane getTransactionsAndTransfersPanel() {
		if( this.transactionsAndTransfersPanel == null ){
			this.transactionsAndTransfersPanel = new BorderPane();
			this.transactionsAndTransfersPanel.setTop( this.getTransferToolBar() );
			this.transactionsAndTransfersPanel.setBottom( this.getBookToolBar() );
			this.transactionsAndTransfersPanel.setCenter( this.getTransactionsAndTransfersSplitPane());
		}
		return this.transactionsAndTransfersPanel;
	}
	
	private ToolBar bookToolBar = null;
	private ToolBar getBookToolBar() {
		if( this.bookToolBar == null ){
			this.bookToolBar = new ToolBar( this.getBookButton());			
		}
		return this.bookToolBar;
	}
	private Button bookButton = null;
	private Node getBookButton() {
		if( this.bookButton == null ){
			this.bookButton = new Button("book");
			this.bookButton.setTooltip(new Tooltip("Books the selected transfer or transaction."));
			this.bookButton.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					bookAction();
				}
			});
		}
		return this.bookButton;
	}
	
	private SplitPane transactionsAndTransfersSplitPane = null;
	private SplitPane getTransactionsAndTransfersSplitPane() {
		if( this.transactionsAndTransfersSplitPane == null){
			this.transactionsAndTransfersSplitPane = new SplitPane();
			this.transactionsAndTransfersSplitPane.setOrientation( Orientation.VERTICAL );
			this.transactionsAndTransfersSplitPane.getItems().addAll( this.getTransferOrTransactionLst(), this.getTransactionDetailsPanel());
		}
		return this.transactionsAndTransfersSplitPane;
	}
	private ListView<TransferOrTransactionView> transferOrTransactionLst = null;
	private ListView<TransferOrTransactionView> getTransferOrTransactionLst() {
		if( this.transferOrTransactionLst == null ){
			this.transferOrTransactionLst = new ListView<TransferOrTransactionView>();
			this.transferOrTransactionLst.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			this.transferOrTransactionLst.setTooltip(new Tooltip("Shows the created and pending transfers and/or transactions."));
			this.transferOrTransactionLst.setItems( this.getViewModel().getPendingTransfersAndOrTransactions());
			HBox.setHgrow(this.transferOrTransactionLst, Priority.ALWAYS);
			this.transferOrTransactionLst.getSelectionModel().getSelectedItems().addListener( new ListChangeListener<TransferOrTransactionView>(){
				@Override
				public void onChanged( javafx.collections.ListChangeListener.Change<? extends TransferOrTransactionView> c) {
					transferOrTransactionListSelectionChangedAction();					
				}
			});
		}
		return this.transferOrTransactionLst;
	}
	
	private BorderPane transactionDetailsPanel = null;
	private BorderPane getTransactionDetailsPanel() {
		if( this.transactionDetailsPanel == null ){
			this.transactionDetailsPanel = new BorderPane();
//			TitledPane pane = new TitledPane("Transaction details", getTransactionDetailLst());
//			pane.collapsibleProperty().set(false);
//			pane.minHeight(0);
//			this.transactionDetailsPanel.setCenter( pane );
			this.transactionDetailsPanel.setCenter( this.getTransactionDetailLst());
			this.hideTransactionDetails();
		}
		return this.transactionDetailsPanel;
	}
	
	private ToolBar transferToolBar = null;
	private ToolBar getTransferToolBar() {
		if( this.transferToolBar == null ){
			this.transferToolBar = new ToolBar();
			this.transferToolBar.getItems().add( new Label( "Amount(€): "));
			this.transferToolBar.getItems().add( this.getAmountInput());
			this.transferToolBar.getItems().add( new Label( " Subject: "));
			this.transferToolBar.getItems().add( this.getPurposeInput());
			this.transferToolBar.getItems().add( this.getDebitCreateButton());
			this.transferToolBar.getItems().add( this.getCreditCreateButton());
			this.transferToolBar.getItems().add( this.getCreateTransactionButton());			
		}
		return this.transferToolBar;
	}
	private Button createTransactionButton = null;
	private Button getCreateTransactionButton() {
		if (this.createTransactionButton == null) {
			this.createTransactionButton = new Button("Create transaction");
			this.createTransactionButton.setTooltip( new Tooltip("Creates a new empty transaction."));
			this.createTransactionButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					createTransactionAction();					
				}
			});
		}
		return this.createTransactionButton;
	}
	private Button creditCreateButton = null;
	private Button getCreditCreateButton() {
		if( this.creditCreateButton == null){
			this.creditCreateButton = new Button("Create credit");
			this.creditCreateButton.setTooltip(new Tooltip("Creates a new transfer (if a transaction is selected within the selected transaction)."));
			this.creditCreateButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					debitOrCreditCreateAction(Credit);					
				}
			});
		}	
		return creditCreateButton;
	}
	private Button debitCreateButton = null;
	private Button getDebitCreateButton() {
		if( this.debitCreateButton == null){
			this.debitCreateButton = new Button("Create debit");
			this.debitCreateButton.setTooltip(new Tooltip("Creates a new transfer (if a transaction is selected within the selected transaction)."));
			this.debitCreateButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					debitOrCreditCreateAction(Debit);					
				}
			});
		}
		return this.debitCreateButton;
	}
	private TextField purposeInput = null;
	private TextField getPurposeInput() {
		if( this.purposeInput == null){
			this.purposeInput = new TextField("subject");
		}
		return this.purposeInput;
	}
	private TextField amountInput = null;
	private TextField getAmountInput() {
		if( this.amountInput == null){
			this.amountInput = new TextField("1");
		}
		return this.amountInput;
	}
	private BorderPane otherAccountsPane = null;
	private BorderPane getOtherAccountsPane() {
		if( this.otherAccountsPane == null ){
			this.otherAccountsPane = new BorderPane();
			this.otherAccountsPane.setBottom( this.getOtherAccountsViewToolBar() );
			this.otherAccountsPane.setCenter( this.getOtherAccountList());
		}
		return this.otherAccountsPane;
	}
	
	private ToolBar otherAccountsViewToolBar = null;
	private ToolBar getOtherAccountsViewToolBar() {
		if( this.otherAccountsViewToolBar== null){
			this.otherAccountsViewToolBar = new ToolBar();
			this.otherAccountsViewToolBar.getItems().add(this.getClearAccountViewButton());
		}
		return this.otherAccountsViewToolBar;
	}
	
	private Button clearAccountViewButton = null;
	private Button getClearAccountViewButton() {
		if( this.clearAccountViewButton == null ){
			this.clearAccountViewButton = new Button("clear");
			this.clearAccountViewButton.setTooltip(new Tooltip("Clears the list of found accounts."));
			this.clearAccountViewButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					clearAccountViewAction();
				}
			});
		}
		return clearAccountViewButton;
	}
	
	private ListView<AccountViewFX> otherAccountLst = null;
	private ListView<AccountViewFX> getOtherAccountList() {
		if( this.otherAccountLst == null ){
			this.otherAccountLst = new ListView<AccountViewFX>();
			this.otherAccountLst.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			this.otherAccountLst.setItems( this.getViewModel().getOtherAccounts());
			this.otherAccountLst.setTooltip( new Tooltip("Shows the accounts (own and/or others) that have been found by searches."));
			this.otherAccountLst.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<AccountViewFX>() {
				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends AccountViewFX> c) {
					if (getOtherAccountList().getSelectionModel().getSelectedItem() == null && getOtherAccountList().getItems().size() == 1) 
						getOtherAccountList().getSelectionModel().select(0);
				}
			});
		}
		return this.otherAccountLst;
	}
	
	private BorderPane accountsPanel = null;
	private BorderPane getAccountsPanel() {
		if( this.accountsPanel == null ){
			this.accountsPanel = new BorderPane();
			this.accountsPanel.setTop( this.getAccountsToolBar() );
			TitledPane pane = new TitledPane("My accounts", this.getAccountsSplitPane() );
			pane.collapsibleProperty().set(false);
			this.accountsPanel.setCenter( pane );
		}
		return accountsPanel;
	}

	private SplitPane accountsSplitPane = null;
	private SplitPane getAccountsSplitPane() {
		if( this.accountsSplitPane == null ){
			this.accountsSplitPane = new SplitPane();
			this.accountsSplitPane.setOrientation( Orientation.HORIZONTAL);
			this.accountsSplitPane.getItems().addAll( this.getMyAccountsPanel(), this.getAccountDetailsPanel() );	
			this.accountsSplitPane.setDividerPosition( 0, Vertical_Divider_Pos);
			this.accountsSplitPane.prefHeightProperty().bind( this.getAccountsPanel().heightProperty());
		}
		return this.accountsSplitPane;
	}

	private BorderPane myAccountsPanel = null;
	private BorderPane getMyAccountsPanel() {
		if( this.myAccountsPanel == null ){
			this.myAccountsPanel = new BorderPane();
			this.myAccountsPanel.setCenter( this.getMyAccountList());
			this.myAccountsPanel.setBottom( this.getMyAccountsViewToolBar());
		}
		return this.myAccountsPanel;
	}
	
	private ListView<AccountViewFX> myAccountLst = null;
	private ListView<AccountViewFX> getMyAccountList() {
		if( this.myAccountLst == null ){
			this.myAccountLst = new ListView<AccountViewFX>();
			this.myAccountLst.getSelectionModel().setSelectionMode( SelectionMode.SINGLE);
			this.myAccountLst.setTooltip( new Tooltip( "Shows the user's accounts."));
			this.myAccountLst.setItems( this.getViewModel().getMyAccounts() );
			this.myAccountLst.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<AccountViewFX>() {
				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends AccountViewFX> c) {
					myAccountListSelectionChangedAction();
				}
			});
		}
		return this.myAccountLst;
	}
	private ToolBar myAccountsViewToolBar = null;
	private ToolBar getMyAccountsViewToolBar() {
		if( this.myAccountsViewToolBar== null){
			this.myAccountsViewToolBar = new ToolBar();
			this.myAccountsViewToolBar.getItems().add( this.getMyAccountNameLabel());
			this.myAccountsViewToolBar.getItems().add( this.getNewNameInputField());
			this.myAccountsViewToolBar.getItems().add(this.getRenameAccountButton());
		}
		return this.myAccountsViewToolBar;
	}

	private TextField newNameInputField = null;
	private TextField getNewNameInputField() {
		if( this.newNameInputField== null){
			this.newNameInputField = new TextField();
//TODO adjust width of input field
		}
		return newNameInputField;
	}
	private Label myAccountNameLabel = null;
	private Label getMyAccountNameLabel() {
		if( this.myAccountNameLabel == null){
			this.myAccountNameLabel = new Label("Name: ");
		}
		return this.myAccountNameLabel;
	}
	
	private Button renameButton = null;
	private Button getRenameAccountButton() {
		if( this.renameButton == null){
			this.renameButton = new Button("rename");
			this.renameButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					renameAccountAction();
				}
			});
		}
		return this.renameButton;
	}
	private BorderPane accountDetailsPane = null;
	private BorderPane getAccountDetailsPanel() {
		if( this.accountDetailsPane == null ){
			this.accountDetailsPane = new BorderPane();
			TitledPane pane = new TitledPane("Account details", this.getAccountEntryList() );
			pane.collapsibleProperty().set(false);
			this.accountDetailsPane.setCenter( pane );
		}
		return this.accountDetailsPane;
	}
	private ListView<EntryView> accountEntryLst = null;
	private ListView<EntryView> getAccountEntryList() {
		if( this.accountEntryLst == null ){
			this.accountEntryLst = new ListView<EntryView>();
			this.accountEntryLst.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			this.accountEntryLst.setTooltip(new Tooltip("Shows the entries of the selected user account."));
			this.accountEntryLst.setItems( this.getViewModel().getCurrentAccountEntries());
		}
		return this.accountEntryLst;
	}
	private ToolBar accountsToolBar = null;
	private ToolBar getAccountsToolBar() {
		if( this.accountsToolBar == null ){
			this.accountsToolBar = new ToolBar( new Label(TXT_account + " " + TXT_name+ TXT_spr_lbl) );
			this.accountsToolBar.getItems().add( this.getAccountNameInput() );
			this.accountsToolBar.getItems().add( this.getCreateAccountButton() );
			this.accountsToolBar.getItems().add( this.getFindAccountButton() );
		}
		return this.accountsToolBar;
	}

	private Button findButton = null;
	private Button getFindAccountButton() {
		if( this.findButton == null) {
			this.findButton = new Button( TXT_imp_find );
			this.findButton.setTooltip( new Tooltip("Searches for an acount with name \"account name\" and lists it under \"Other accounts\", if found."));
			this.findButton.setOnAction( new EventHandler<ActionEvent>() {
				public void handle( ActionEvent event) {
					findAccountAction();					
				}
			});
		}
		return this.findButton;
	}

	private Button createButton = null;
	private Button getCreateAccountButton() {
		if( this.createButton == null) {
			this.createButton = new Button( TXT_imp_create );
			this.createButton.setTooltip( new Tooltip("Creates an acount with name \"account name\" and lists it under \"My accounts\"."));
			this.createButton.setOnAction( new EventHandler<ActionEvent>() {
				public void handle( ActionEvent event) {
					createAccountAction();	
					getAccountNameInput().requestFocus();
				}
			});
		}
		return this.createButton;
	}

	private TextField accountNameInput = null;
	private TextField getAccountNameInput() {
		if( this.accountNameInput == null ){
			this.accountNameInput = new TextField(TXT_account);
		}
		return this.accountNameInput;
	}

	private StatusBar mainStatusBar = null;
	private StatusBar getMainStatusBar() {
		if( this.mainStatusBar == null ){
			this.mainStatusBar = new StatusBar();
		}
		return mainStatusBar;
	}
	
	/* End of graphical user interface ***********************************************************************/


	private void myAccountListSelectionChangedAction() {
		AccountViewFX selected = this.getMyAccountList().getSelectionModel().getSelectedItem();
		if( selected != null) {
			this.getViewModel().changeAccountSelection(selected);
			this.getNewNameInputField().setText(selected.getName());
		} else {
			if (this.getMyAccountList().getItems().size() == 1) {
				this.getMyAccountList().getSelectionModel().select(0);				
			} else {
				this.getNewNameInputField().clear();
			}			
		}
		
	}
	private void createAccountAction() {
		try {
			this.getViewModel().createAccount( this.getAccountNameInput().getText());
			this.getMyAccountList().getSelectionModel().clearAndSelect( this.getMyAccountList().getItems().size() - 1);
		} catch (AccountException e) {
			this.showError( e );
		}
	}
	
	private void findAccountAction() {
		try {
			this.getViewModel().findAccount( this.getAccountNameInput().getText());
			this.getOtherAccountList().getSelectionModel().select(this.getViewModel().getOtherAccounts().size()-1);
		} catch ( AccountException e) {
			this.showError( e );
		}
	}
	private TransferException transferException = null;
	private void debitOrCreditCreateAction(int creditOrDebit) {
		AccountView myAccount = this.getMyAccountList().getSelectionModel().getSelectedItem();
		if (myAccount == null) {
			this.showError("Select an account in your own account list!");
			return;
		}
		AccountView otherAccount = this.getOtherAccountList().getSelectionModel().getSelectedItem();
		if (otherAccount == null) {
			this.showError("Select an account in the list of other accounts!");
			return;
		}
		long amount; 
		try {
			amount = Long.parseLong(this.getAmountInput().getText());
		} catch (NumberFormatException nfe) {
			this.showError("The amount shall be an integer!");
			return;
		}
		if( amount <= 0 ){
			this.showError("Amount must be positive!");
			return;		
		}
		AccountView fromAccount;
		AccountView toAccount;
		if (creditOrDebit == Credit) {
			fromAccount = otherAccount;
			toAccount = myAccount;
		} else {
			fromAccount = myAccount;
			toAccount = otherAccount;
		}
		String purpose = this.getPurposeInput().getText();
		TransferOrTransactionView selectedTransferOrTransaction = this.getTransferOrTransactionLst().getSelectionModel().getSelectedItem();
		prepareTransfer(amount, fromAccount, toAccount, purpose, selectedTransferOrTransaction); 
	}

	private void prepareTransfer(long amount, AccountView fromAccount, AccountView toAccount, String purpose,
			TransferOrTransactionView selectedTransferOrTransaction) {
		this.transferException = null;
		if (selectedTransferOrTransaction != null) {
			selectedTransferOrTransaction.accept(new TransferOrTransactionViewVisitor() {
				@Override
				public void handleTransferView(TransferView transferView) {
					try {
						getViewModel().createTransfer(fromAccount, toAccount, amount, purpose);
					} catch (TransferException e) {
						transferException = e;
					}
				}
				@Override
				public void handleTransactionView(TransactionView transactionView) {
					try {
						getViewModel().createTransferInTransaction(fromAccount, toAccount, amount, purpose, transactionView);
						transferOrTransactionListSelectionChangedAction();
					} catch (TransferException e) {
						transferException = e;
					}
				}
			});
		} else {
			try {
				this.getViewModel().createTransfer(fromAccount, toAccount, amount, purpose);
			} catch (TransferException e) {
				transferException = e;
			}
		}
		if( this.transferException != null ) this.showError(this.transferException.getMessage());
	}
	
	private void transferOrTransactionListSelectionChangedAction() {
		TransferOrTransactionView selected = this.getTransferOrTransactionLst().getSelectionModel().getSelectedItem();
		if( selected != null) {
			selected.accept(new TransferOrTransactionViewVisitor(){
				@Override
				public void handleTransactionView(TransactionView transactionView) {
					getViewModel().changeTransactionSelection((TransactionViewFX) transactionView);
					showTransactionDetails(transactionView);
				}
				@Override
				public void handleTransferView(TransferView transferView) {
					hideTransactionDetails();
				}
			});
		} else {
			if ( getTransferOrTransactionLst().getItems().size() > 0) {
				getTransferOrTransactionLst().getSelectionModel().clearSelection();
			}
			this.hideTransactionDetails();
		}
	}
		
	private void hideTransactionDetails() {
		this.getTransactionDetailsPanel().setVisible( false );
		this.getTransactionsAndTransfersSplitPane().setDividerPosition( 0, 1.0);
		HBox.setHgrow(this.getTransactionsAndTransfersPanel(), Priority.ALWAYS);
	}
	
	private void showTransactionDetails(TransactionView transactionView) {
		this.getTransactionDetailsPanel().setVisible( true);
		this.getTransactionsAndTransfersSplitPane().setDividerPosition( 0, 0.6 );		
		this.getTransactionDetailLst().setItems( getViewModel().getCurrentTransactionDetails());			
	}

	private ListView<TransferOrTransactionView> transactionDetailLst = null;
	private ListView<TransferOrTransactionView> getTransactionDetailLst(){
		if( this.transactionDetailLst == null ){
			this.transactionDetailLst = new ListView<TransferOrTransactionView>( this.getViewModel().getCurrentTransactionDetails());
			this.transactionDetailLst.getSelectionModel().setSelectionMode( SelectionMode.SINGLE );
		}
		return this.transactionDetailLst;
	}

	private void showError(String message) {
		this.getMainStatusBar().showError(message);
	}

	private void showError( AccountException e) {
		this.showError(e.getMessage());
	}

	private ViewModelFX getViewModel() {
		return this.viewModel;
	}

	@Override
	public void updateEntriesOfSelectedAccount() {
		Platform.runLater(new Runnable() {		
			@Override
			public void run() {
				getAccountEntryList().setItems( getViewModel().getCurrentAccountEntries());
			}
		});
	}
	
	private void createTransactionAction() {
		this.getViewModel().createTransaction();		
	}
	protected void renameAccountAction() {
		AccountView myAccount = this.getMyAccountList().getSelectionModel().getSelectedItem();
		if( myAccount == null) {
			this.showError("Select one of your own accounts prior to renaming!");
			return;
		}
		String newName = this.getNewNameInputField().getText();
		try {
			AccountManager.getTheAccountManager().renameAccount(myAccount.getAccount(), newName);
		} catch (AccountException e) {
			this.showError(e.getMessage());;
		}
	}
	
	private void clearAccountViewAction() {
		this.getViewModel().clearOtherAccounts();
	}
	
	private void bookAction() {
		TransferOrTransactionView selected = this.getTransferOrTransactionLst().getSelectionModel().getSelectedItem();
		if( selected == null ) {
			this.showError("Select a transfer or transaction prior to booking!");
			return;
		}
		try {
			this.getViewModel().book(selected);
			this.getTransferOrTransactionLst().getSelectionModel().clearSelection();
			this.hideTransactionDetails();
		} catch (AccountException | TransferException e) {
			this.showError(e.getMessage());;
		}	
	}
}
