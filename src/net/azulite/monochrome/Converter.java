package net.azulite.monochrome;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Converter {
	private int threshold;
	private BufferedImage readImage;

	public Converter()
	{
		this.readImage = null;
		this.setThreshold( 128 );
	}

	public void setThreshold( int threshold )
	{
		this.threshold = threshold;
	}

	public boolean load( File input )
	{
		try
		{
			this.readImage = ImageIO.read( input );
		} catch ( Exception e )
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private int alphaBlend( int c, float a ){
		return (int)( 255 * ( 1 - a ) + c * a );
	}

	public boolean convert( File output )
	{
		if ( this.readImage == null ){ return false; }

		//Graphics g = this.readImage.getGraphics();
		int w = this.readImage.getWidth();
		int h = this.readImage.getHeight();
		int c, r, g, b, min, max;
		float a;

		BufferedImage write = new BufferedImage( w, h, BufferedImage.TYPE_BYTE_BINARY);
		Graphics graph = write.getGraphics();

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				c = this.readImage.getRGB( x, y );
				a = (c >>> 24) / 255.0f;
				r = this.alphaBlend( c >> 16 & 0xff, a );
				g = this.alphaBlend( c >> 8 & 0xff, a );
				b = this.alphaBlend( c & 0xff, a );
				min = Math.min( Math.min( r, g ), b );
				max = Math.max( Math.max( r, g ), b );
				if ( ( max + min ) / 2 < this.threshold )
				{
					// Black
					graph.setColor( Color.BLACK );
				} else
				{
					// White
					graph.setColor( Color.WHITE );
				}
				graph.fillRect( x, y, 1, 1 );
			}
		}

		graph.dispose();

		try
		{
			ImageIO.write( write, "bmp", output );
		} catch ( IOException e )
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
