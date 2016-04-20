import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

import net.azulite.monochrome.*;

class Main extends TransferHandler implements ActionListener
{
	private JFrame frame;
	private JLabel path;
	Converter conv;

	public static void main( String[] arg )
	{
		Main main = new Main();
		main.createWindow();
	}

	public void createWindow()
	{
		this.conv = null;

		this.frame = new JFrame( "MonochromeImageConverter" );

		this.frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		this.frame.setLayout( new BorderLayout() );

		JButton infile = new JButton( "Open file" );
		infile.addActionListener( this );
		infile.setActionCommand( "open" );
		this.frame.add( BorderLayout.NORTH, infile );

		this.path = new JLabel( "none" );
		this.frame.add( BorderLayout.CENTER, this.path );
		this.path.setTransferHandler( this );

		JButton convert = new JButton( "Convert" );
		convert.addActionListener( this );
		convert.setActionCommand( "convert" );
		this.frame.add( BorderLayout.SOUTH, convert );

		//frame.setTransferHandler( this );

		this.frame.pack();
		this.frame.setVisible( true );
	}

	public void convert()
	{
		if ( this.conv == null )
		{
			this.path.setText( "Error: Please choose file." );
			return;
		}
		File output = new File( this.path.getText() + ".bmp" );
		if ( conv.convert( output ) )
		{
			this.path.setText( "Success: " + output.getPath() );
		} else
		{
			this.path.setText( "Failure: " + output.getPath() );
		}
	}

	private void open()
	{
		this.conv = new Converter();
		JFileChooser filechooser = new JFileChooser();

		int selected = filechooser.showOpenDialog( this.frame );
		if ( selected == JFileChooser.APPROVE_OPTION )
		{
			this.open( filechooser.getSelectedFile() );
		}
	}

	private void open( File input )
	{
		conv.load( input );
		this.path.setText( input.getPath() );
	}

	@Override
	public void actionPerformed( ActionEvent event )
	{
		System.out.println( event.getActionCommand() );
		if ( event.getActionCommand().equals( "open" ) )
		{
			this.open();
		} else if ( event.getActionCommand().equals( "convert" ) )
		{
			this.convert();
		}
	}

	@Override
	public boolean importData(TransferSupport support) {
		if (!canImport(support)) {
	        return false;
	    }

		// ドロップ処理
		Transferable t = support.getTransferable();
		try {
			// ファイルを受け取る
			List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

			this.open( files.get( 0 ) );

		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}

