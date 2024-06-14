package ortus.boxlang.modules.image;

import java.awt.BasicStroke;

public class StrokeBuilder {

	private float	width		= 1;
	private float	miterLimit	= 1;
	private int		endCaps		= 1;
	private int		lineJoins	= 1;
	private float[]	dashArray	= new float[] { 1 };
	private float	dashPhase	= 0;

	public StrokeBuilder width( float width ) {
		this.width = width;
		return this;
	}

	public StrokeBuilder miterLimit( float miterLimit ) {
		this.miterLimit = miterLimit;
		return this;
	}

	public StrokeBuilder endCaps( int endCaps ) {
		this.endCaps = endCaps;
		return this;
	}

	public StrokeBuilder lineJoins( int lineJoins ) {
		this.lineJoins = lineJoins;
		return this;
	}

	public StrokeBuilder dashArray( float[] dashArray ) {
		this.dashArray = dashArray;
		return this;
	}

	public StrokeBuilder dashPhase( float dashPhase ) {
		this.dashPhase = dashPhase;
		return this;
	}

	public BasicStroke build() {
		return new BasicStroke( width, endCaps, lineJoins, miterLimit, dashArray, dashPhase );
	}
}
