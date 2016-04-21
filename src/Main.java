import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.azulite.monochrome.*;

class Main extends DropTargetAdapter implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JLabel path;
	private JSlider slider;
	private JTextField threshold;
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

		// Top
		JButton infile = new JButton( "Open file" );
		infile.addActionListener( this );
		infile.setActionCommand( "open" );
		this.frame.add( BorderLayout.NORTH, infile );

		// Center
		JPanel panel = new JPanel();
		panel.setLayout( new BorderLayout() );
		this.frame.add( BorderLayout.CENTER, panel );

		this.path = new JLabel( "Drop image file." );
		this.path.setHorizontalAlignment( JLabel.CENTER );
		panel.add( BorderLayout.NORTH, this.path );

		// Slider
		JPanel spanel = new JPanel();
		spanel.setLayout( new BorderLayout() );
		panel.add( BorderLayout.SOUTH, spanel );

		this.slider = new JSlider( 0, 255 );
		this.slider.addChangeListener( this );
		spanel.add( BorderLayout.CENTER, this.slider );

		JButton sub = new JButton( "<" );
		sub.addActionListener( this );
		sub.setActionCommand( "<" );
		spanel.add( BorderLayout.WEST, sub );

		JButton add = new JButton( ">" );
		add.addActionListener( this );
		add.setActionCommand( ">" );
		spanel.add( BorderLayout.EAST, add );

		// Config
		JPanel config = new JPanel();
		panel.add( BorderLayout.CENTER, config );
		config.setLayout( new GridLayout( 1, 2 ) );

		config.add( new JLabel( "Threshold" ) );
		this.threshold = new JTextField();
		config.add( this.threshold );
		this.threshold.setEditable( false );
		this.updateThreshold( slider.getValue() );

		// Bottom
		JButton convert = new JButton( "Convert" );
		convert.addActionListener( this );
		convert.setActionCommand( "convert" );
		this.frame.add( BorderLayout.SOUTH, convert );

		this.frame.setDropTarget( new DropTarget( panel, this ) );

		this.frame.pack();
		this.frame.setVisible( true );
	}

	public void convert()
	{
		if ( this.conv == null )
		{
			this.path.setText( "Failure: Please choose file." );
			return;
		}
		File output = new File( this.path.getText() + ".bmp" );
		if ( conv.convert( output ) )
		{
			this.path.setText( "Success: " + output.getPath() );
		} else
		{
			this.path.setText( "Failure: " + this.conv.getError() + "|" + output.getPath() );
		}
	}

	private void open()
	{
		JFileChooser filechooser = new JFileChooser();

		int selected = filechooser.showOpenDialog( this.frame );
		if ( selected == JFileChooser.APPROVE_OPTION )
		{
			this.open( filechooser.getSelectedFile() );
		}
	}

	private void open( File input )
	{
		if ( this.conv == null )
		{
			this.conv = new Converter();
			this.conv.setThreshold( this.slider.getValue() );
		}
		if ( this.conv.load( input ) )
		{
			this.path.setText( "Failure: Cannot load image." );
		}
		this.path.setText( input.getPath() );
	}

	private void updateThreshold( int newval )
	{

		if ( newval < 0 || 255 < newval ){ return; }
		this.slider.setValue( newval );
		this.threshold.setText( String.valueOf( this.slider.getValue() ) );
		if ( this.conv == null ){ return; }
		this.conv.setThreshold( this.slider.getValue() );
	}

	@Override
	public void actionPerformed( ActionEvent event )
	{
		System.out.println( event.getActionCommand() );
		String cmd = event.getActionCommand();
		if ( cmd.equals( "open" ) )
		{
			this.open();
		} else if ( cmd.equals( "convert" ) )
		{
			this.convert();
		} else if( cmd.equals( "<" ) )
		{
			this.updateThreshold( this.slider.getValue() - 1 );
		} else if( cmd.equals( ">" ) )
		{
			this.updateThreshold( this.slider.getValue() + 1 );
		}
	}

	@Override
	public void drop( DropTargetDropEvent event )
	{
		try
		{
			Transferable t = event.getTransferable();
			if ( t.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) )
			{
				event.acceptDrop( DnDConstants.ACTION_COPY_OR_MOVE );
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>)(t.getTransferData(DataFlavor.javaFileListFlavor));
				this.open( files.get( 0 ) );
			}
		} catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void stateChanged( ChangeEvent event )
	{
		this.updateThreshold( this.slider.getValue() );
	}
}

